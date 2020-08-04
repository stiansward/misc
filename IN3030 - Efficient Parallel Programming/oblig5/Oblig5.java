package oblig5;

public class Oblig5 {
	public static void main(String[] args) {
		int n, runs = 7;
		if (args.length < 1) {
			n = 1000;
		} else {
			n = Integer.parseInt(args[0]);
		}

		double startTime, stopTime;
		double[] seqTimes = new double[runs], parTimes = new double[runs];

		printInfo(n);

		ConvexHull co = new ConvexHull(n);
		ConvexHullPar coP = new ConvexHullPar(n);

		for (int i = 0; i < runs; i++) {
			System.out.printf("| Run %-26d |%n", (i + 1));

			startTime = System.nanoTime();
			co.findCoHull();
			stopTime = System.nanoTime();
			seqTimes[i] = (double) (stopTime - startTime) / 1000000;

			startTime = System.nanoTime();
			coP.findCoHull();
			stopTime = System.nanoTime();
			parTimes[i] = (double) (stopTime - startTime) / 1000000;
		}

		if (n <= 1000) {
			new TegnUt(coP, coP.il);
		}
		printResults(seqTimes[runs / 2], parTimes[runs / 2]);
	}

	public static void printInfo(int n) {
		line();
		System.out.printf("| Testing with %9d points  |%n", n);
		line();
		System.out.println("| Creating objects               |");
	}

	public static void printResults(double seqTime, double parTime) {
		line();
		System.out.printf("| Sequential runtime: %8.3fms |%n", seqTime);
		System.out.printf("| Parallel runtime:   %8.3fms |%n", parTime);
		System.out.printf("| Speedup:            %8.3fx  |%n", (seqTime / parTime));
		line();
	}

	public static void line() {
		System.out.println("+--------------------------------+");
	}
}
