/**
 * This code was written by user marahan on the UiO GitHub
 * Source: https://github.uio.no/magnuesp/IN3030-v19/blob/master/marahan/SequentialSieve/SequentialSieve.java
 * NOTE: Some minor changes have been made
 */

package oblig3;

class SequentialSieve {
	protected int[] primes;
	protected byte[] byteArray;
	protected int n;
	protected int primesCounter;

	public SequentialSieve(int n) {
		this.n = n;
		int cells = n / 16 + 1;
		byteArray = new byte[cells];
	}

	public int[] findPrimes() {
		findFirstPrimes();
		countRestPrimes();
		gatherPrimes();
		return primes;
	}

	protected void findFirstPrimes() {
		primesCounter = 1;
		int currentPrime = 3;
		int squareRootN = (int) Math.sqrt(n);

		while (currentPrime != 0 && currentPrime <= squareRootN) {
			traverse(currentPrime);
			currentPrime = findNextPrime(currentPrime + 2);
			primesCounter++;
		}
	}

	protected void traverse(int p) {
		for (int i = p * p; i < n; i += p * 2) {
			flip(i);
		}
	}

	protected void flip(int i) {
		if (i % 2 == 0)
			return;

		int byteCell = i / 16;
		int bit = (i / 2) % 8;

		byteArray[byteCell] |= (1 << bit);
	}

	protected int findNextPrime(int startAt) {
		for (int i = startAt; i < n; i += 2) {
			if (isPrime(i))
				return i;
		}
		return 0;
	}

	protected boolean isPrime(int i) {
		if ((i % 2) == 0)
			return false;

		int byteCell = i / 16;
		int bit = (i / 2) % 8;

		return (byteArray[byteCell] & (1 << bit)) == 0;
	}

	protected void countRestPrimes() {
		int startAt = (int) Math.sqrt(n) + 1;

		if (startAt % 2 == 0)
			startAt++;

		startAt = findNextPrime(startAt);
		while (startAt != 0) {
			primesCounter++;
			startAt = findNextPrime(startAt + 2);
		}
	}

	protected void gatherPrimes() {
		primes = new int[primesCounter];
		primes[0] = 2;

		int currentPrime = 3;
		for (int i = 1; i < primesCounter; i++) {
			primes[i] = currentPrime;
			currentPrime = findNextPrime(currentPrime + 2);
		}
	}
}
