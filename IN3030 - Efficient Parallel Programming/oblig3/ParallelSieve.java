package oblig3;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelSieve extends SequentialSieve {
	private int threads, rootN, rootRootN;
	CyclicBarrier cb, threadCB;
	AtomicInteger atomicPrimesCounter;

	public ParallelSieve(int n, int threads) {
		super(n);
		this.threads = threads;
		rootN = (int) Math.sqrt(n);
		rootRootN = (int) Math.sqrt(rootN);
		cb = new CyclicBarrier(threads + 1);
		threadCB = new CyclicBarrier(threads);
		atomicPrimesCounter = new AtomicInteger(0);
	}

	public int[] findPrimes() {
		findFirstPrimes();
		findRestPrimes();
		primesCounter = atomicPrimesCounter.get();
		gatherPrimes();
		return primes;
	}

	protected void findFirstPrimes() {
		SequentialSieve seqSieve = new SequentialSieve(rootRootN);
		seqSieve.findPrimes();
		for (int i = 0; i < seqSieve.byteArray.length; i++) {
			byteArray[i] = seqSieve.byteArray[i];
		}
	}

	private void findRestPrimes() {
		// Set up each thread's workspace indices and start the threads
		int start = 0, stop, segmentLength = (n + threads - 1) / threads, threshold = n % threads;
		for (int i = 0; i < threads; i++) {
			stop = start + segmentLength;
			if (i < threshold) stop++;
			new Thread(new Worker(i, start, stop)).start();
			start = stop;
		}
		await();
	}

	/**
	 * Await all threads, including main
	 */
	private void await() {
		try {
			cb.await();
		} catch (BrokenBarrierException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private class Worker implements Runnable {
		private int id, localPrimeCounter, start, stop;

		public Worker(int id, int start, int stop) {
			this.id = id;
			this.start = start;
			this.stop = stop;
			localPrimeCounter = 0;
		}

		public void run() {
			traverseIndices(rootRootN);
			awaitThreads();
			traverseIndices(rootN);
			countPrimesIndices();
			atomicPrimesCounter.addAndGet(localPrimeCounter);
			await();
		}

		/**
		 * Traverse the range for each prime up to the given limit
		 * 
		 * @param lim   Upper limit on the primes to traverse
		 * @param start Lower bound for the range
		 * @param stop  Upper bound for the range
		 */
		private void traverseIndices(int lim) {
			for (int p = findNextPrime(3); p != 0 && p <= lim; p = findNextPrime(p + 2)) {
				int i = p * p;
				while (i < start)
					i += p * 2;
				while (i <= stop) {
					flip(i);
					i += p * 2;
				}
			}
		}

		/**
		 * Count the number of primes between two values
		 * 
		 * @param start Lower bound
		 * @param stop  Upper bound
		 */
		private void countPrimesIndices() {
			for (int i = start; i < stop; i++) {
				if (isPrime(i))
					localPrimeCounter++;
			}
		}

		/**
		 * Await only the parallel threads
		 */
		private void awaitThreads() {
			try {
				threadCB.await();
			} catch (BrokenBarrierException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}