package oblig1;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

public class Worker implements Runnable {
	int k, start, end;
	int[] a, res;
	CyclicBarrier cb;
	ReentrantLock reLock;

	public Worker(int[] a, int[] res, int k, int start, int end, CyclicBarrier cb, ReentrantLock reLock) {
		this.a = a;
		this.res = res;
		this.k = k;
		this.start = start;
		this.end = end;
		this.cb = cb;
		this.reLock = reLock;
	}

	public void run() {
		// Sort this thread's part of the array
		Oblig1.insertSort(a, start, ((start + k) < end) ? (start + k) : end);
		int i, t;

		for (int j = start + k; j < end; j++) {
			if (a[j] > a[k - 1]) {
				t = a[j];
				i = start + k - 2;
				while (i >= start && a[i] < t) {
					a[i + 1] = a[i];
					i--;
				}
				a[i + 1] = t;
			}
		}

		// Put k largest into res if larger than existing values
		int endK = ((start + k) > end) ? end : (start + k);
		try {
			reLock.lock();
			for (int j = start; j < endK; j++) {
				if (a[j] < res[k - 1])
					break;
				t = a[j];
				i = k - 2;
				while (i >= 0 && res[i] < t) {
					res[i + 1] = res[i];
					i--;
				}
				res[i + 1] = t;
			}
		} finally {
			reLock.unlock();
		}

		// Let main thread know it's finished
		try {
			cb.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}
}
