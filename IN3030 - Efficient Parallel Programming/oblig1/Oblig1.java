package oblig1;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

public class Oblig1 {
	static Random r = new Random();
	static int availableCores = Runtime.getRuntime().availableProcessors();

	public static void main(String[] args) {
		long start;
		int runs = 7;
		double[] stdTimes = new double[runs], seqTimes = new double[runs], parTimes = new double[runs];
		int[] kValues = {20, 100};

		for (int k : kValues) {
			// Print top of results table
			line();
			System.out.printf("|   k = %4d  |%14s  |%13s   |%12s    |%12s    |%n", k, "Arrays.sort", "Sequential",
					"Parallel", "Speedup");
			line();

			for (int size = 100000000; size >= 1000; size /= 10) {
				// k cannot be larger than size
				if (k > size)
					k = size;

				// Create source array and work arrays, fill with random integers
				int[] a = new int[size];
				int[] workArray = new int[size];
				int[] stdRes = new int[k];
				int[] res = new int[k];
				for (int i = 0; i < a.length; i++) {
					a[i] = r.nextInt();
				}

				// Measure times of multiple runs of Arrays.sort
				for (int i = 0; i < runs; i++) {
					copy(a, workArray);
					start = System.nanoTime();
					Arrays.sort(workArray);
					for (int j = 0; j < k; j++) {
						stdRes[j] = workArray[workArray.length - j - 1];
					}
					stdTimes[i] = (double) (System.nanoTime() - start) / 1000000;
				}

				// Measure times of multiple runs of sequential sort
				for (int i = 0; i < runs; i++) {
					copy(a, workArray);
					start = System.nanoTime();
					sequential(workArray, k, res);
					seqTimes[i] = (double) (System.nanoTime() - start) / 1000000;
					if (!Arrays.equals(res, stdRes))
						System.err.println("ERROR: Incorrect result!");
				}

				// Measure times of multiple runs of parallel sort
				// If an unnecessary amount of available cores, set a reasonable amount of
				// threads
				int threads = Math.min(availableCores, (size / 125000) + 1);
				CyclicBarrier cb = new CyclicBarrier(threads + 1);
				ReentrantLock reLock = new ReentrantLock();
				for (int i = 0; i < runs; i++) {
					Arrays.fill(res, Integer.MIN_VALUE);
					copy(a, workArray);
					start = System.nanoTime();
					parallel(workArray, k, res, threads, cb, reLock);
					parTimes[i] = (double) (System.nanoTime() - start) / 1000000;
					if (!Arrays.equals(res, stdRes))
						System.err.println("ERROR: Incorrect result!");
				}
				threads = availableCores;

				// Sort result times, print to table
				Arrays.sort(stdTimes);
				Arrays.sort(seqTimes);
				Arrays.sort(parTimes);
				System.out.printf("|%12d |%15.4f |%15.4f |%15.4f |%14.4fx |%n", size, stdTimes[runs / 2],
						seqTimes[runs / 2], parTimes[runs / 2], seqTimes[runs / 2] / parTimes[runs / 2]);
			}
			line();
			System.out.println();
		}
	}

	/**
	 * Place the k largest elements from a into res in descending order, using a
	 * sequential algorithm
	 * 
	 * @param a   Array to be pseudo-sorted
	 * @param k   Number of elements to find
	 * @param res Array to place results
	 */
	public static void sequential(int[] a, int k, int[] res) {
		insertSort(a, 0, (k < a.length) ? k : a.length);

		int i, t;
		for (int j = k; j < a.length; j++) {
			if (a[j] > a[k - 1]) {
				t = a[j];
				i = k - 2;
				while (i >= 0 && a[i] < t) {
					a[i + 1] = a[i];
					i--;
				}
				a[i + 1] = t;
			}
		}
		copy(a, res);
	}

	/**
	 * Insert sort in descending order from v to h
	 * 
	 * @param a Array to be sorted
	 * @param v Start index
	 * @param h Stop index (exclusive)
	 */
	public static void insertSort(int[] a, int v, int h) {
		int i, t;

		for (int k = v; k < (h - 1); k++) {
			t = a[k + 1];
			i = k;
			while (i >= v && a[i] < t) {
				a[i + 1] = a[i];
				i--;
			}
			a[i + 1] = t;
		}
	}

	/**
	 * Place the k largest elements from a into res in descending order, using a
	 * parallelized algorithm
	 * 
	 * @param a   Array to be pseudo-sorted
	 * @param k   Number of elements to find
	 * @param res Array to place results
	 */
	public static void parallel(int[] a, int k, int[] res, int threads, CyclicBarrier cb, ReentrantLock reLock) {
		// Using code from TA lecture
		// Author: marahan
		// Link:
		// https://github.uio.no/magnuesp/IN3030-v19/blob/master/marahan/OddNumbers.java
		int start = 0, segmentLength = a.length / threads, end = segmentLength;
		for (int i = 0; i < threads; i++) {
			if (i == threads - 1) {
				new Thread(new Worker(a, res, k, start, a.length, cb, reLock)).start();
			} else {
				new Thread(new Worker(a, res, k, start, ++end, cb, reLock)).start();
				start = end;
				end += segmentLength;
			}
		}
		try {
			cb.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}

	public static void copy(int[] from, int[] to) {
		int min = Math.min(from.length, to.length);
		for (int i = 0; i < min; i++) {
			to[i] = from[i];
		}
	}

	public static void print(String message, int[] a) {
		System.out.printf(message + "[");
		for (int i = 0; i < a.length; i++) {
			if (i > 0)
				System.out.printf(", ");
			System.out.printf(i + ":" + a[i]);
		}
		System.out.println("]");
	}

	public static void line() {
		System.out.println("+-------------+" + "----------------+" + "----------------+" + "----------------+"
				+ "----------------+");
	}
}
