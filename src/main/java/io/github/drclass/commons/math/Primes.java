package io.github.drclass.commons.math;

import java.text.MessageFormat;
import java.util.Arrays;

/**
 * Methods related to prime numbers in the range of <code>int</code>.
 */
public class Primes {

	private Primes() {
		// Private default constructor to prevent instantiation
	}

	private static final String NUMBER_TOO_SMALL = "{0} is smaller than the minimum ({1})";
	private static final String INTEGER_OVERFLOW = "Integer overflow occured finding prime larger than {0}";

	/**
	 * Primality test: tells if the argument is a (provable) prime or not.
	 *
	 * @param n number to test.
	 * @return true if {@code n} is prime. All numbers &lt; 2 return false.
	 */
	public static boolean isPrime(int n) {
		if (n < 2) {
			return false;
		}
		if (n == 2 || n == 3) {
			return true;
		}
		if (n % 2 == 0 || n % 3 == 0) {
			return false;
		}
		int sqrt = (int) Math.sqrt(n);
		int dx = 4;
		for (int i = 5; i <= sqrt; i += dx) {
			if (n % i == 0) {
				return false;
			}
			dx = -(dx - 6);
		}
		return true;
	}

	/**
	 * Return the smallest prime greater than or equal to n.
	 *
	 * @param n positive number.
	 * @return the smallest prime greater than or equal to {@code n}.
	 * @throws IllegalArgumentException if n &lt; 0.
	 * @throws ArithmeticException      if no prime exists &gt; n
	 */
	public static int nextPrime(int n) {
		if (n < 0) {
			throw new IllegalArgumentException(MessageFormat.format(NUMBER_TOO_SMALL, n, 0));
		}
		if (n <= 2) {
			return 2;
		}
		n |= 1;
		if (isPrime(n)) {
			return n;
		}
		final int rem = n % 3;
		if (0 == rem) {
			n += 2;
		} else if (1 == rem) {
			n += 4;
		}
		while (true) {
			// Test if we wrap around and enter the negatives
			if (n < 0) {
				throw new ArithmeticException(MessageFormat.format(INTEGER_OVERFLOW, n));
			}
			if (isPrime(n)) {
				return n;
			}
			n += 2;
			if (isPrime(n)) {
				return n;
			}
			n += 4;
		}
	}

	/**
	 * Returns an array of all primes from 0 to {@code limit}.<br/>
	 * <br/>
	 * Uses the Sieve of Atkins to generate primes. This has an approximate
	 * efficiency of {@code O(N/(log(log N)))}
	 * 
	 * @param limit max bounds for how high to check for primes
	 * @return an array of primes
	 */
	public static int[] sievePrimes(int limit) {
		// Initialise the sieve array with false values
		boolean sieve[] = new boolean[limit + 1];
		for (int i = 0; i <= limit; i++) {
			sieve[i] = false;
		}
		/*
		 * Mark sieve[n] is true if one of the following is true: a) n = (4*x*x)+(y*y)
		 * has odd number of solutions, i.e., there exist odd number of distinct pairs
		 * (x, y) that satisfy the equation and n % 12 = 1 or n % 12 = 5. b) n =
		 * (3*x*x)+(y*y) has odd number of solutions and n % 12 = 7 c) n = (3*x*x)-(y*y)
		 * has odd number of solutions, x > y and n % 12 = 11
		 */
		for (int x = 1; x * x <= limit; x++) {
			for (int y = 1; y * y <= limit; y++) {
				// Main part of Sieve of Atkin
				int n = (4 * x * x) + (y * y);
				if (n <= limit && (n % 12 == 1 || n % 12 == 5)) {
					sieve[n] ^= true;
				}
				n = (3 * x * x) + (y * y);
				if (n <= limit && n % 12 == 7) {
					sieve[n] ^= true;
				}
				n = (3 * x * x) - (y * y);
				if (x > y && n <= limit && n % 12 == 11) {
					sieve[n] ^= true;
				}
			}
		}
		// Mark all multiples of squares as non-prime
		for (int r = 5; r * r <= limit; r++) {
			if (sieve[r]) {
				for (int i = r * r; i <= limit; i += r * r) {
					sieve[i] = false;
				}
			}
		}
		int primes[] = new int[limit];
		int index = 0;
		// 2 and 3 are known to be prime
		if (limit > 2) {
			primes[index] = 2;
			index++;
		}
		if (limit > 3) {
			primes[index] = 3;
			index++;
		}
		// Print primes using sieve[]
		for (int a = 5; a <= limit; a++) {
			if (sieve[a]) {
				primes[index] = a;
				index++;
			}
		}
		return Arrays.copyOf(primes, index);
	}
	
	/**
	 * Finds the specified number of prime numbers.
	 *
	 * @param n the number of prime numbers to find
	 * @return an array containing the first n prime numbers
	 */
	public static int[] findPrimes(int n) {
        int count = 0;
        int number = 2;
        int[] primes = new int[n];
        while (count < n) {
            if (isPrime(number)) {
                primes[count] = number;
                count++;
            }
            number++;
        }
        return primes;
    }
}
