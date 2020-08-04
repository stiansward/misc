package oblig3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Oblig3 {
	static final boolean prettyPrint = true;
	static int N, threads, runs = 7;
	static long startTime;
	static double[] seqPrimeTimes = new double[runs],
					parPrimeTimes = new double[runs],
					seqFactorTimes = new double[runs],
					parFactorTimes = new double[runs];
	static int[] seqPrimes, parPrimes;
	static HashMap<Long, ArrayList<Long>> seqMap, parMap;

	public static void main(String[] args) {
		// Parse variables from command line arguments
		if (args.length < 1) {
			System.err.println("Usage: java Oblig3 <N> [threads]");
			System.exit(1);
		}
		try {
			N = Integer.parseInt(args[0]);
			if (args.length > 1)
				threads = Integer.parseInt(args[1]);
			else {
				threads = Runtime.getRuntime().availableProcessors();
				log("Argument to threads is not given. Using available processors (" + threads + ")");
			}
		} catch (NumberFormatException e) {
			System.err.println("ERROR: Invalid number format");
			e.printStackTrace();
			System.exit(1);
		}
		if (threads == 0) {
			threads = Runtime.getRuntime().availableProcessors();
			log("Argument to threads is 0. Using available processors (" + threads + ")");
		}

		// Run tests
		for (int i = 0; i < runs; i++) {
			startTime = System.nanoTime();
			SequentialSieve seqSieve = new SequentialSieve(N);
			seqPrimes = seqSieve.findPrimes();
			seqPrimeTimes[i] = (double) (System.nanoTime() - startTime) / 1000000;

			startTime = System.nanoTime();
			ParallelSieve parSieve = new ParallelSieve(N, threads);
			parPrimes = parSieve.findPrimes();
			parPrimeTimes[i] = (double) (System.nanoTime() - startTime) / 1000000;

			// Check for errors in parallel primes
			if (!Arrays.equals(seqPrimes, parPrimes))
				System.err.println("ERROR: Incorrect primes in run " + i);

			startTime = System.nanoTime();
			SequentialFactorizer seqFactors = new SequentialFactorizer(N, seqPrimes);
			seqMap = seqFactors.findFactors();
			seqFactorTimes[i] = (double) (System.nanoTime() - startTime) / 1000000;

			startTime = System.nanoTime();
			ParallelFactorizer parFactors = new ParallelFactorizer(N, seqPrimes, threads);
			parMap = parFactors.findFactors();
			parFactorTimes[i] = (double) (System.nanoTime() - startTime) / 1000000;
		}

		// Save factors and print timing results
		writeFactors();

		Arrays.sort(seqPrimeTimes);
		Arrays.sort(parPrimeTimes);
		Arrays.sort(seqFactorTimes);
		Arrays.sort(parFactorTimes);
		if (prettyPrint)
			prettyPrintResults();
		else
			simplePrintResults();
	}

	public static void writeFactors() {
		Oblig3Precode precode = new Oblig3Precode(N);
		for (int i = 0; i < parMap.size(); i++) {
			long base = (long) N * N - 100 + i;
			for (long factor : parMap.get(base)) {
				precode.addFactor((long) base, factor);
			}
		}
		precode.writeFactors();
	}

	// UTILITY METHODS
	public static void log(String m) {
		System.out.println("LOG: " + m);
	}

	public static void prettyPrintResults() {
		// Set the appropriate suffix for the SIZE column
		int i = 0;
		String[] suffixes = {"", "K", "M", "B", "T"};
		while (N >= 1000) {
			i++;
			N /= 1000;
		}
		String s = N + suffixes[i];

		line();
		System.out.printf("|              FINDING PRIMES < %4s             |\n", s);
		line();
		System.out.println("|   SEQUENTIAL   |   PARALLEL    |    SPEEDUP    |");
		System.out.printf("| %11.3fms  | %10.3fms  | %9.3fx    |\n", seqPrimeTimes[runs / 2], parPrimeTimes[runs / 2],
				(seqPrimeTimes[runs / 2] / parPrimeTimes[runs / 2]));
		line();
		System.out.println();
		line();
		System.out.printf("|                 FINDING FACTORS                |\n", s);
		line();
		System.out.println("|   SEQUENTIAL   |   PARALLEL    |    SPEEDUP    |");
		System.out.printf("| %11.3fms  | %10.3fms  | %9.3fx    |\n", seqFactorTimes[runs / 2], parFactorTimes[runs / 2],
				(seqFactorTimes[runs / 2] / parFactorTimes[runs / 2]));
		line();
	}

	public static void simplePrintResults() {
		System.out.printf(
				"SIZE\tPRIMES: SEQUENTIAL\tPRIMES: PARALLEL\tSPEEDUP\tFACTORS: SEQUENTIAL\tFACTORS: PARALLEL\tSPEEDUP%n"
						+ "%d\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f%n",
				N, seqPrimeTimes[runs / 2], parPrimeTimes[runs / 2],
				(seqPrimeTimes[runs / 2] / parPrimeTimes[runs / 2]), seqFactorTimes[runs / 2], parFactorTimes[runs / 2],
				(seqFactorTimes[runs / 2] / parFactorTimes[runs / 2]));
	}

	public static void line() {
		System.out.println("+----------------+---------------+---------------+");
	}
}
