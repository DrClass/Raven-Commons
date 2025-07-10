package io.github.drclass.commons.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * A small utility class that groups together a few fundamental mathematical constants and convenience methods for
 * computing high–precision approximations of them.
 * <p>
 * The class is <strong>immutable</strong> and <strong>thread-safe</strong> - all fields are final and there is no
 * mutable state.
 * <p>
 * This class is intentionally non-instantiable - its constructor is private to prevent accidental construction.
 */
public class Constant {

    private Constant() {
        // Private default constructor to prevent instantiation
    }

    /**
     * The {@code double} value that is closer than any other to <i>pi</i> (&pi;), the ratio of the circumference of a
     * circle to its diameter.
     * <p>
     * Numerical value is the same as {@link java.lang.Math#PI}.
     *
     * @see java.lang.Math#PI
     */
    public static final double PI = Math.PI;

    /**
     * The {@code double} value that is closer than any other to <i>e</i>, the base of the natural logarithms.
     * <p>
     * Numerical value is the same as {@link java.lang.Math#E}.
     *
     * @see java.lang.Math#E
     */
    public static final double E = Math.E;

    /**
     * Returns an approximation of <i>pi</i> (&pi;) with the requested number of digits after the decimal point.
     * <p>
     * The method chooses the algorithm according to the requested precision:
     * <ul>
     *   <li><strong>precision&nbsp;&le;&nbsp;15</strong> &nbsp;&mdash;&nbsp;
     *       Uses the built-in double constant
     *       {@link Constant#PI} (wrapped in a {@code BigDecimal}) because its
     *       15-digit accuracy is already sufficient.</li>
     *   <li><strong>precision&nbsp;&gt;&nbsp;15</strong> &nbsp;&mdash;&nbsp;
     *       Computes &pi; with the Gauss–Legendre arithmetic-geometric–mean
     *       (AGM) algorithm.
     * </ul>
     *
     * <p><strong>Performance warning:</strong>
     * The number of AGM iterations (and therefore the running time) grows
     * roughly exponentially with the requested precision. In practice the
     * method becomes prohibitively slow for
     * precision &gt; 2^15 (&asymp; 32 768) decimal digits, so such values
     * are strongly discouraged.
     *
     * @param precision number of digits to appear to the right of the decimal point; must be non-negative
     * @return a {@code BigDecimal} containing &pi; truncated to {@code precision} fractional digits
     * @throws IllegalArgumentException if {@code precision} is negative
     */
    public static BigDecimal Pi(int precision) {
        if (precision < 0) {
            throw new IllegalArgumentException("precision must be greater than 0.");
        }
        BigDecimal pi = BigDecimal.ZERO;
        if (precision <= 15) {
            pi = new BigDecimal(PI);
        } else {
            MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_EVEN);

            BigDecimal a = BigDecimal.ONE;
            BigDecimal b = a.divide(BigDecimal.TWO.sqrt(mc), mc);
            BigDecimal t = new BigDecimal("0.25", mc);
            BigDecimal p = BigDecimal.ONE;

            BigDecimal eps = BigDecimal.ONE.scaleByPowerOfTen(-precision);

            while (a.subtract(b).abs().compareTo(eps) > 0) {
                BigDecimal an = a.add(b).divide(BigDecimal.TWO, mc);
                b = a.multiply(b, mc).sqrt(mc);
                t = t.subtract(p.multiply(a.subtract(an, mc).pow(2, mc), mc), mc);
                a = an;
                p = p.multiply(BigDecimal.TWO);
            }
            pi = a.add(b).pow(2, mc).divide(t.multiply(new BigDecimal(4), mc), mc);
        }
        return pi.setScale(precision, RoundingMode.FLOOR);
    }
}
