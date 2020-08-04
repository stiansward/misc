package oblig2;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Worker implements Runnable {
	Oblig2Precode.Mode m;
	int id, start, end;
	double[][] A, B, C;
	CyclicBarrier cb;

	public Worker(int id, Oblig2Precode.Mode m, int start, int end, 
				double[][] A, double[][] B, double[][] C, CyclicBarrier cb) {
		this.id = id;
		this.m = m;
		this.start = start;
		this.end = end;
		this.A = A;
		this.B = B;
		this.C = C;
		this.cb = cb;
	}

	@Override
	public void run() {
		switch (m) {
		case PARA_NOT_TRANSPOSED:
			notTransposed();
			break;
		case PARA_A_TRANSPOSED:
			ATransposed();
			break;
		case PARA_B_TRANSPOSED:
			BTransposed();
			break;
		default:
			System.err.println("ERROR: Parallel worker called with sequential mode");
			break;
		}

		try {
			cb.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}

	public void notTransposed() {
		for (int i = start; i < end; i++) {
			for (int j = 0; j < C[i].length; j++) {
				for (int k = 0; k < A[i].length; k++) {
					C[i][j] += A[i][k] * B[k][j];
				}
			}
		}
	}

	public void ATransposed() {
		for (int i = start; i < end; i++) {
			for (int j = 0; j < C[i].length; j++) {
				for (int k = 0; k < A[i].length; k++) {
					C[i][j] += A[k][i] * B[k][j];
				}
			}
		}
	}

	public void BTransposed() {
		for (int i = start; i < end; i++) {
			for (int j = 0; j < C[i].length; j++) {
				for (int k = 0; k < A[i].length; k++) {
					C[i][j] += A[i][k] * B[j][k];
				}
			}
		}
	}

	public void print(String m) {
		System.out.println("Thread " + id + ": " + m);
	}
}
