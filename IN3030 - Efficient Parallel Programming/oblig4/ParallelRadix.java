package oblig4;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

public class ParallelRadix extends SequentialRadix {
	private int numThreads, globalMax, mask, shift;
	private int[] a, b, bit;
	private int[][] globalFrequencies;
	CyclicBarrier cb, tcb;
	ReentrantLock lock;

	/**
	 * Static wrapper-method to enable the same interface as the sequential version
	 * 
	 * @param unsorted The array to sort
	 * @return The sorted array
	 */
	public static int[] sort(int[] unsorted) {
		ParallelRadix pr = new ParallelRadix();
		return pr.internalSort(unsorted);
	}

	public int[] internalSort(int[] unsorted) {
		numThreads = Runtime.getRuntime().availableProcessors();
		a = unsorted;
		globalFrequencies = new int[numThreads][];
		cb = new CyclicBarrier(numThreads + 1);
		tcb = new CyclicBarrier(numThreads);
		lock = new ReentrantLock();

		// Divide the array between threads.
		// Based on this answer on StackOverflow:
		// https://stackoverflow.com/a/36689048
		int chunkSize = (a.length + numThreads - 1) / numThreads;
		for (int i = 0; i < numThreads; i++) {
			int start = i * chunkSize;
			int stop = Math.min(start + chunkSize, a.length);
			new Thread(new Worker(i, start, stop)).start();
		}

		await(); // Wait for threads to complete their part of STEP A

		// Find number of bits needed for max
		int numBits = Integer.SIZE - Integer.numberOfLeadingZeros(globalMax);

		// Find a perfect digit size if exists, otherwise use 6
		int digitSize = findDigitSize(numBits);

		// Find how many digits we need
		int numDigits = Math.max(1, numBits / digitSize);

		// Create a list of bits to use for each digit
		bit = new int[numDigits];
		int rest = numBits % numDigits;

		for (int i = 0; i < bit.length; i++) {
			bit[i] = numBits / numDigits;
			if (rest-- > 0)
				bit[i]++;
		}

		b = new int[a.length];
		shift = 0;
		mask = (1 << bit[0]) - 1;

		await(); // STEP A COMPLETE

		for (int i = 0; i < bit.length; i++) {
			mask = (1 << bit[i]) - 1;
			await(); // Release threads
			await(); // STEP D COMPLETE
			shift += bit[i];
			int[] tmp = a;
			a = b;
			b = tmp;
		}

		b = null;
		return a;
	}

	/**
	 * Synchronize on the cb CyclicBarrier object which includes the main thread
	 */
	private void await() {
		try {
			cb.await();
		} catch (BrokenBarrierException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public class Worker implements Runnable {
		public int id, start, stop, localMax;
		public int[] localFrequency;

		public Worker(int id, int start, int stop) {
			this.id = id;
			this.start = start;
			this.stop = stop;
		}

		public void run() {
			// STEP A: Find largest element and update globalMax
			localMax = a[start];
			for (int i = start + 1; i < stop; i++) {
				if (a[i] > localMax)
					localMax = a[i];
			}
			try {
				lock.lock();
				if (localMax > globalMax)
					globalMax = localMax;
			} finally {
				lock.unlock();
			}

			await(); // Main handles rest of STEP A
			await(); // STEP A COMPLETE

			for (int i = 0; i < bit.length; i++) {
				await(); // Wait for main to set mask

				// STEP B: Find frequency of each digit
				localFrequency = new int[mask + 1];
				for (int j = start; j < stop; j++) {
					localFrequency[(a[j] >>> shift) & mask]++;
				}
				globalFrequencies[id] = localFrequency;

				threadAwait(); // STEP B COMPLETE

				// STEP C: Calculate pointers for digits
				int accumulated = 0;
				int[] digitPointers = new int[localFrequency.length];
				for (int j = 0; j < digitPointers.length; j++) {
					for (int k = 0; k < id; k++) {
						accumulated += globalFrequencies[k][j];
					}
					digitPointers[j] = accumulated;
					for (int k = id; k < numThreads; k++) {
						accumulated += globalFrequencies[k][j];
					}
				}

				// STEP D: Move numbers into correct bucket
				for (int j = start; j < stop; j++) {
					b[digitPointers[(a[j] >>> shift) & mask]++] = a[j];
				}

				await(); // STEP D COMPLETE
			}
		}

		/**
		 * Synchronize on the tcb CyclicBarrier object which excludes the main thread
		 */
		private void threadAwait() {
			try {
				tcb.await();
			} catch (BrokenBarrierException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
