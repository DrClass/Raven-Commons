package io.github.drclass.commons.math;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides utility methods to obtain all unique divisors of a given integer.
 */
public class Divisors {

	private Divisors() {
		// Private default constructor to prevent instantiation
	}

	/**
	 * Returns a list of all unique divisors of the specified integer {@code n}.
	 * <p>
	 * This method iterates from {@code 1} up to the square root of {@code n}. If a
	 * number in this range divides {@code n} evenly (i.e., {@code n % i == 0}),
	 * that number {@code i} is added to the list. Additionally, the corresponding
	 * complementary divisor {@code n / i} is also added, except when {@code i} and
	 * {@code n / i} are the same (this scenario occurs if {@code n} is a perfect
	 * square).
	 *
	 * @param n the integer for which divisors are to be found
	 * @return a list containing all divisors of {@code n}, including 1 and
	 *         {@code n} itself
	 * @throws IllegalArgumentException if {@code n} is less than or equal to 0
	 */
	public static List<Integer> getDivisors(int n) {
		if (n <= 0) {
			throw new IllegalArgumentException("Input number must be greater than 0.");
		}

		List<Integer> divisors = new ArrayList<>();

		for (int i = 1; i <= Math.sqrt(n); i++) {
			if (n % i == 0) {
				// i is a divisor
				divisors.add(i);

				// Avoid adding square root twice for perfect squares
				if (i != n / i) {
					divisors.add(n / i);
				}
			}
		}

		return divisors;
	}
}
