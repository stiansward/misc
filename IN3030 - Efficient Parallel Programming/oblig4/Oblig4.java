package oblig4;

import java.util.Arrays;

public class Oblig4 {
	private static int size, seed, runs = 7;
	private static double[] seqTimes = new double[runs],
							parTimes = new double[runs];
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("This program takes two arguments: <size> <seed>");
			System.exit(1);
		}
		size = Integer.parseInt(args[0]);
		seed = Integer.parseInt(args[1]);
		
		int[] unsortedArray = Oblig4Precode.generateArray(size, seed),
				seqSorted = null,
				parSorted = null;
		long start;

		for (int i = 0; i < runs; i++) {
			start = System.nanoTime();
			seqSorted = SequentialRadix.sort(unsortedArray);
			seqTimes[i] = (double)(System.nanoTime() - start) / 1000000;
			
			start = System.nanoTime();
			parSorted = ParallelRadix.sort(unsortedArray);
			parTimes[i] = (double)(System.nanoTime() - start) / 1000000;
			
			if (!Arrays.equals(seqSorted, parSorted)) System.err.println("Incorrect results in run " + (i + 1));
		}
		
		Oblig4Precode.saveResults(Oblig4Precode.Algorithm.SEQ, seed, seqSorted);
		Oblig4Precode.saveResults(Oblig4Precode.Algorithm.PARA, seed, parSorted);
		
		printResults();
	}
	
	private static void printResults() {
		Arrays.sort(seqTimes);
		Arrays.sort(parTimes);
		
		line();
		System.out.printf("|       SIZE: %10d     |%n", size);
		line();
		System.out.printf("| Sequential:\t%10.3fms |%n", seqTimes[runs/2]);
		System.out.printf("| Parallel:\t%10.3fms |%n", parTimes[runs/2]);
		System.out.printf("| Speedup:\t%10.3fx  |%n", seqTimes[runs/2]/parTimes[runs/2]);
		line();
	}
	
	private static void line() {
		System.out.println("+----------------------------+");
	}
}
