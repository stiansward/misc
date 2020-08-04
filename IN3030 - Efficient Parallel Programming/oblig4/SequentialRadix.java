/**
 * Sequential solution to radix sorting.
 * Based on Magnus Espeland's code from TA session March 29, 2019
 * Source: https://github.uio.no/magnuesp/IN3030-v19/blob/master/magnuesp/Radix/Oblig4.java
 */

package oblig4;

public class SequentialRadix {
	public static int[] sort(int[] unsorted) {
		int[] a = unsorted;

		// STEP A: Find largest element
		int max = getMax(a, 0, a.length);

		// Find number of bits needed for max
		int numBits = Integer.SIZE - Integer.numberOfLeadingZeros(max);

		// Find a perfect digit size if exists, otherwise use 6
		int digitSize = findDigitSize(numBits);

		// Find how many digits we need
		int numDigits = Math.max(1, numBits / digitSize);

		// Create a list of bits to use for each digit
		int[] bit = new int[numDigits];
		int rest = numBits % numDigits;

		for (int i = 0; i < bit.length; i++) {
			bit[i] = numBits / numDigits;
			if (rest-- > 0)
				bit[i]++;
		}

		// Create destination array and shift length, call sorting method
		int[] b = new int[a.length];
		int shift = 0;
		for (int i = 0; i < bit.length; i++) {
			radix(a, b, bit[i], shift);

			shift += bit[i];

			int[] tmp = a;
			a = b;
			b = tmp;
		}
		b = null;
		return a;
	}

	private static void radix(int[] a, int[] b, int maskLen, int shift) {
		int mask = (1 << maskLen) - 1, accumulated = 0;
		int[] digitFrequency = new int[mask + 1];
		int[] digitPointers = new int[digitFrequency.length];

		// STEP B: Count frequency of each digit
		for (int i = 0; i < a.length; i++) {
			digitFrequency[(a[i] >>> shift) & mask]++;
		}

		// STEP C: Calculate pointers for digits
		for (int i = 0; i < digitFrequency.length; i++) {
			digitPointers[i] = accumulated;
			accumulated += digitFrequency[i];
		}

		// STEP D: Move numbers into correct bucket
		for (int i = 0; i < a.length; i++) {
			b[digitPointers[(a[i] >>> shift) & mask]++] = a[i];
		}
	}

	/**
	 * Find the largest element between start and stop-1 in an array
	 * 
	 * @param a     Array to search through
	 * @param start Start index
	 * @param stop  Stop index (exclusive)
	 * @return Largest element
	 */
	protected static int getMax(int[] a, int start, int stop) {
		int max = 0;
		for (int i = start; i < stop; i++) {
			if (a[i] > max)
				max = a[i];
		}
		return max;
	}

	/**
	 * Check if there is a digit size between 6 and 11 that gives a perfect
	 * distribution over the digits, otherwise use 6
	 * 
	 * @param numBits The number of bits needed to represent the largest number in the array
	 * @return The optimal digit size for sorting
	 */
	protected static int findDigitSize(int numBits) {
		for (int i = 6; i <= 11; i++) {
			if (numBits % i == 0) {
				return i;
			}
		}
		return 6;
	}
}
