package oblig2;

import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Oblig2 {
	static final int optimalSizePerThread = 4000;

	public static void main(String[] args) {
		boolean printForExcel = false;
		int seed = 42, runs = 7;
		int[] sizes = {100, 200, 500, 1000};
		double[] seqNotTransposedTimes = new double[runs], seqATransposedTimes = new double[runs],
				seqBTransposedTimes = new double[runs], parNotTransposedTimes = new double[runs],
				parATransposedTimes = new double[runs], parBTransposedTimes = new double[runs];

		if (printForExcel) {
			System.out.println("SIZE\tSEQUENTIAL NON-TRANSPOSED\tPARALLEL NON-TRANSPOSED\tSPEEDUP NON-TRANSPOSED\t"
					+ "SEQUENTIAL A TRANSPOSED\tPARALLEL A TRANSPOSED\tSPEEDUP A TRANSPOSED\t"
					+ "SEQUENTIAL B TRANSPOSED\tPARALLEL B TRANSPOSED\tSPEEDUP B TRANSPOSED\tTHREADS");
		} else {
			line();
			System.out.println("| Size |   SEQ_NORM  |  PARA_NORM | SPEEDUP | SEQ_TRANS_A |"
					+ " PARA_TRANS_A | SPEEDUP | SEQ_TRANS_B | PARA_TRANS_B | SPEEDUP | THREADS |");
			line();
		}

		for (int n : sizes) {
			int threads = Math.min(Runtime.getRuntime().availableProcessors(), (n * n / optimalSizePerThread + 1));
			double[][] A = Oblig2Precode.generateMatrixA(seed, n), B = Oblig2Precode.generateMatrixB(seed, n),
					C = new double[n][n], verificationMatrix = new double[n][n];

			// SEQUENTIAL, NOT TRANSPOSED
			for (int i = 0; i < runs; i++) {
				if (i > 0)
					clear(verificationMatrix);
				seqNotTransposedTimes[i] = seqNotTransposed(A, B, verificationMatrix);
			}
			Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.SEQ_NOT_TRANSPOSED, verificationMatrix);

			// PARALLEL, NOT TRANSPOSED
			for (int i = 0; i < runs; i++) {
				if (i > 0)
					clear(C);
				parNotTransposedTimes[i] = parallel(Oblig2Precode.Mode.PARA_NOT_TRANSPOSED, A, B, C, threads);
				if (!equals(C, verificationMatrix))
					System.err.println("ERROR: Incorrect results!");
			}
			Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.PARA_NOT_TRANSPOSED, C);

			// SEQUENTIAL, A TRANSPOSED
			for (int i = 0; i < runs; i++) {
				clear(C);
				seqATransposedTimes[i] = seqATransposed(A, B, C);
				A = Oblig2Precode.generateMatrixA(seed, n);
				if (!equals(C, verificationMatrix))
					System.err.println("ERROR: Incorrect results!");
			}
			Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.SEQ_A_TRANSPOSED, C);

			// PARALLEL, A TRANSPOSED
			for (int i = 0; i < runs; i++) {
				clear(C);
				parATransposedTimes[i] = parallel(Oblig2Precode.Mode.PARA_A_TRANSPOSED, A, B, C, threads);
				A = Oblig2Precode.generateMatrixA(seed, n);
				if (!equals(C, verificationMatrix))
					System.err.println("ERROR: Incorrect results!");
			}
			Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.PARA_A_TRANSPOSED, C);

			// SEQUENTIAL, B TRANSPOSED
			for (int i = 0; i < runs; i++) {
				clear(C);
				seqBTransposedTimes[i] = seqBTransposed(A, B, C);
				B = Oblig2Precode.generateMatrixB(seed, n);
				if (!equals(C, verificationMatrix))
					System.err.println("ERROR: Incorrect results!");
			}
			Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.SEQ_B_TRANSPOSED, C);

			// PARALLEL, B TRANSPOSED
			for (int i = 0; i < runs; i++) {
				clear(C);
				parBTransposedTimes[i] = parallel(Oblig2Precode.Mode.PARA_B_TRANSPOSED, A, B, C, threads);
				B = Oblig2Precode.generateMatrixB(seed, n);
				if (!equals(C, verificationMatrix))
					System.err.println("ERROR: Incorrect results!");
			}
			Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.PARA_B_TRANSPOSED, C);

			String format;
			if (printForExcel)
				format = "%d\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%d\n";
			else
				format = "| %4d | %9.3fms | %8.3fms | %6.3fx | %9.3fms "
						+ "| %10.3fms | %6.3fx | %9.3fms | %10.3fms | %6.3fx | %7d |\n";
			System.out.printf(format, n, seqNotTransposedTimes[runs / 2], parNotTransposedTimes[runs / 2],
					(seqNotTransposedTimes[runs / 2] / parNotTransposedTimes[runs / 2]), seqATransposedTimes[runs / 2],
					parATransposedTimes[runs / 2], (seqATransposedTimes[runs / 2] / parATransposedTimes[runs / 2]),
					seqBTransposedTimes[runs / 2], parBTransposedTimes[runs / 2],
					(seqBTransposedTimes[runs / 2] / parBTransposedTimes[runs / 2]), threads);
		}
		if (!printForExcel)
			line();
	}

	public static double seqNotTransposed(double[][] A, double[][] B, double[][] C) {
		double start = System.nanoTime();
		for (int i = 0; i < C.length; i++) {
			for (int j = 0; j < C[i].length; j++) {
				for (int k = 0; k < A[i].length; k++) {
					C[i][j] += A[i][k] * B[k][j];
				}
			}
		}
		return (double) (System.nanoTime() - start) / 1000000;
	}

	public static double seqATransposed(double[][] A, double[][] B, double[][] C) {
		double start = System.nanoTime();

		transpose(A);

		for (int i = 0; i < C.length; i++) {
			for (int j = 0; j < C[i].length; j++) {
				for (int k = 0; k < A[i].length; k++) {
					C[i][j] += A[k][i] * B[k][j];
				}
			}
		}
		return (double) (System.nanoTime() - start) / 1000000;
	}

	public static double seqBTransposed(double[][] A, double[][] B, double[][] C) {
		double start = System.nanoTime();

		transpose(B);

		for (int i = 0; i < C.length; i++) {
			for (int j = 0; j < C[i].length; j++) {
				for (int k = 0; k < A[i].length; k++) {
					C[i][j] += A[i][k] * B[j][k];
				}
			}
		}
		return (double) (System.nanoTime() - start) / 1000000;
	}

	public static double parallel(Oblig2Precode.Mode m, double[][] A, double[][] B, double[][] C, int threads) {
		double startTime = System.nanoTime();

		if (m == Oblig2Precode.Mode.PARA_A_TRANSPOSED) {
			transpose(A);
		} else if (m == Oblig2Precode.Mode.PARA_B_TRANSPOSED) {
			transpose(B);
		}

		CyclicBarrier cb = new CyclicBarrier(threads + 1);

		// Using code from TA lecture
		// Author: marahan
		// Link:
		// https://github.uio.no/magnuesp/IN3030-v19/blob/master/marahan/OddNumbers.java
		int start = 0, segmentLength = A.length / threads, end = segmentLength;
		for (int i = 0; i < threads; i++) {
			if (i == threads - 1) {
				new Thread(new Worker(i, m, start, A.length, A, B, C, cb)).start();
			} else {
				new Thread(new Worker(i, m, start, end, A, B, C, cb)).start();
				start = end;
				end += segmentLength;
			}
		}

		try {
			cb.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
		return (double) (System.nanoTime() - startTime) / 1000000;
	}

	public static void transpose(double[][] M) {
		for (int i = 0; i < M.length; i++) {
			for (int j = i + 1; j < M[i].length; j++) {
				double t = M[i][j];
				M[i][j] = M[j][i];
				M[j][i] = t;
			}
		}
	}

	public static void clear(double[][] m) {
		for (double[] row : m) {
			Arrays.fill(row, 0.0);
		}
	}

	public static boolean equals(double[][] a, double[][] b) {
		for (int i = 0; i < a.length; i++) {
			if (!Arrays.equals(a[i], b[i]))
				return false;
		}
		return true;
	}

	public static void printMatrix(String name, double[][] M) {
		System.out.println(name + ":");
		for (int i = 0; i < M.length; i++) {
			for (int j = 0; j < M[i].length; j++) {
				System.out.printf("%.2f%s", M[i][j], (j < M[i].length - 1) ? "\t" : "\n");
			}
		}
		System.out.println();
	}

	public static void line() {
		System.out.println("+------+-------------+------------+---------+-------------+--------------+"
				+ "---------+-------------+--------------+---------+---------+");
	}
}
