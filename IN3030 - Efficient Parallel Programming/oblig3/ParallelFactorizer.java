package oblig3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

public class ParallelFactorizer extends SequentialFactorizer {
	private int threads;
	private long[] globalMs;
	CyclicBarrier cb, threadCB;
	ReentrantLock mLock, listLock;

	public ParallelFactorizer(int n, int[] primes, int threads) {
		super(n, primes);
		this.threads = threads;
		globalMs = new long[100];
		cb = new CyclicBarrier(threads + 1);
		threadCB = new CyclicBarrier(threads);
		mLock = new ReentrantLock();
		listLock = new ReentrantLock();
	}

	public HashMap<Long, ArrayList<Long>> findFactors() {
		for (int i = 0; i < 100; i++) {
			globalMs[i] = M - 100 + i;
			map.put(globalMs[i], new ArrayList<Long>());
		}
		for (int i = 0; i < threads; i++) {
			new Thread(new Worker(i)).start();
		}
		try {
			cb.await();
		} catch (BrokenBarrierException | InterruptedException e) {
			e.printStackTrace();
		}
		return map;
	}

	private class Worker implements Runnable {
		private int id, sqrtM;
		private long localM;
		private ArrayList<Long> localFactors;

		public Worker(int id) {
			this.id = id;
		}

		public void run() {
			for (int i = 0; i < 100; i++) {
				localM = M - 100 + i;
				localFactors = map.get(localM);
				sqrtM = (int) Math.sqrt(localM);
				for (int p = id; p < primes.length;) {
					if (primes[p] > sqrtM)
						break;
					if (localM % primes[p] == 0) {
						try {
							listLock.lock();
							localFactors.add((long) primes[p]);
							localM /= primes[p];
							globalMs[i] = localM;
						} finally {
							listLock.unlock();
						}
						sqrtM = (int) Math.sqrt(localM);
					} else {
						if (localM != globalMs[i]) {
							try {
								mLock.lock();
								localM = globalMs[i];
							} finally {
								mLock.unlock();
							}
							sqrtM = (int) Math.sqrt(localM);
						}
						p += threads;
					}
				}
			}

			try {
				threadCB.await();
			} catch (BrokenBarrierException | InterruptedException e) {
				e.printStackTrace();
			}

			for (int i = id; i < map.size(); i += threads) {
				localM = M - 100 + i;
				localFactors = map.get(localM);
				for (long p : localFactors) {
					localM /= p;
				}
				if (localM > 1)
					localFactors.add(localM);
			}

			try {
				cb.await();
			} catch (BrokenBarrierException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
