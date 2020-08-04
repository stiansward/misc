package oblig3;

import java.util.ArrayList;
import java.util.HashMap;

public class SequentialFactorizer {
	protected long M;
	protected int[] primes;
	protected HashMap<Long, ArrayList<Long>> map;
	protected ArrayList<Long> factors;

	public SequentialFactorizer(int n, int[] primes) {
		this.M = (long) n * n;
		this.primes = primes;
		map = new HashMap<Long, ArrayList<Long>>(100, 1.0f);
	}

	public HashMap<Long, ArrayList<Long>> findFactors() {
		int sqrtM;
		long currentM;
		for (int i = 0; i < 100; i++) {
			currentM = M - 100 + i;
			factors = new ArrayList<Long>();
			sqrtM = (int) Math.sqrt(currentM);
			for (int p = 0; p < primes.length;) {
				if (primes[p] > sqrtM)
					break;
				if (currentM % primes[p] == 0) {
					factors.add((long) primes[p]);
					currentM /= primes[p];
					sqrtM = (int) Math.sqrt(currentM);
				} else
					p++;
			}
			if (currentM > 1)
				factors.add(currentM);
			map.put(M - 100 + i, factors);
		}
		return map;
	}
}
