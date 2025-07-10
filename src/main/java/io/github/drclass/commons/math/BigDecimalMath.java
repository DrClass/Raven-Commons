package io.github.drclass.commons.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * A utility class providing extended mathematical operations for {@link java.math.BigDecimal}.
 * <p>
 * This class includes methods for performing common mathematical functions such as exponentiation, square roots,
 * logarithms, and trigonometric calculations with {@code BigDecimal} inputs. It is designed to support high-precision
 * arithmetic beyond what is available in the standard {@link Math} class.
 * <p>
 * All methods are static and the class is not intended to be instantiated.
 */
public final class BigDecimalMath {
    private static final String LN2_STR = "0.693147180559945309417232121458176568075500134360255254120680009493393621969694715605863326996418687542001481021923"; // 100 digits

    private BigDecimalMath() {
        // Private constructor to prevent instantiation
    }

    private static MathContext padMathContext(MathContext mc) {
        return new MathContext(mc.getPrecision() + 10, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal add(BigDecimal augend, BigDecimal addend) {
        return augend.add(addend);
    }

    private static BigDecimal ipow(BigDecimal x, long n, MathContext mc) {
        if (n == 0) {
            return BigDecimal.ONE.round(mc);
        }
        if (n < 0) {
            return BigDecimal.ONE.divide(ipow(x, -n, mc), mc);
        }

        BigDecimal result = BigDecimal.ONE;
        BigDecimal base = x;
        long exp = n;
        while (exp > 0) {
            if ((exp & 1L) == 1L)
                result = result.multiply(base, mc);
            base = base.multiply(base, mc);
            exp >>= 1;
        }
        return result;
    }

    public static BigDecimal sqrt(BigDecimal x) {
        return sqrt(x, new MathContext(Math.max(x.scale() / 2, 2), RoundingMode.HALF_UP));
    }

    public static BigDecimal sqrt(BigDecimal x, MathContext mc) {
        if (x.signum() < 0) {
            throw new ArithmeticException("Cannot sqrt of negative BigDecimal");
        }
        if (x.signum() == 0) {
            return BigDecimal.ZERO.round(mc);
        }

        MathContext padded = padMathContext(mc);
        // First guess: 2^(bitLength/2) scaled to current precision
        int bitLength = x.unscaledValue().bitLength();
        BigDecimal guess = BigDecimal.ONE.scaleByPowerOfTen((int) Math.ceil(bitLength / 6.64385618977));
        // Iterate until convergence within the requested precision
        final BigDecimal TWO_BD = BigDecimal.TWO;
        for (; ; ) {
            BigDecimal next = x.divide(guess, padded).add(guess).divide(TWO_BD, padded);
            if (next.compareTo(guess) == 0)
                break;
            guess = next;
        }
        return guess.round(mc);
    }

    public static BigDecimal ln(BigDecimal x) {
        return ln(x, new MathContext(x.scale(), RoundingMode.HALF_UP));
    }

    public static BigDecimal ln(BigDecimal x, MathContext mc) {
        if (x.signum() <= 0) {
            throw new ArithmeticException("Cannot ln of non‑positive BigDecimal");
        }

        MathContext padded = padMathContext(mc);

        // Range‑reduce to [1,2)
        int k = 0;
        while (x.compareTo(BigDecimal.TWO) >= 0) {
            x = x.divide(BigDecimal.TWO, padded);
            k++;
        }
        while (x.compareTo(BigDecimal.ONE) < 0) {
            x = x.multiply(BigDecimal.TWO, padded);
            k--;
        }

        BigDecimal num = x.subtract(BigDecimal.ONE, padded);
        BigDecimal den = x.add(BigDecimal.ONE, padded);
        BigDecimal z = num.divide(den, padded);        // |z| ≤ 1/3
        BigDecimal z2 = z.multiply(z, padded);

        // atanh / Mercator: ln x = 2·(z + z^3/3 + z^5/5 + …)
        BigDecimal term = z;
        BigDecimal sum = BigDecimal.ZERO;
        long n = 1;
        final BigDecimal EPS = BigDecimal.ONE.scaleByPowerOfTen(-padded.getPrecision());
        do {
            sum = sum.add(term.divide(BigDecimal.valueOf(n), padded), padded);
            term = term.multiply(z2, padded);
            n += 2;
        } while (term.abs().compareTo(EPS) > 0);

        BigDecimal ln2 = new BigDecimal(LN2_STR, padded);
        BigDecimal result = sum.multiply(BigDecimal.TWO, padded).add(ln2.multiply(BigDecimal.valueOf(k), padded), padded);
        return result.round(mc);
    }

    public static BigDecimal exp(BigDecimal x) {
        return exp(x, new MathContext(Math.max(x.scale(), 6), RoundingMode.HALF_UP));
    }

    public static BigDecimal exp(BigDecimal x, MathContext mc) {
        if (x.signum() == 0) {
            return BigDecimal.ONE.round(mc);
        }

        MathContext padded = padMathContext(mc);

        BigDecimal ln2 = new BigDecimal(LN2_STR, padded);
        // Split x = k*ln2 + r with k = round(x/ln2)
        long k = x.divide(ln2, 0, RoundingMode.HALF_EVEN).longValue();
        BigDecimal r = x.subtract(ln2.multiply(BigDecimal.valueOf(k), padded), padded);

        // e^r via series 1 + r + r²/2! + r³/3! + …
        BigDecimal term = BigDecimal.ONE;
        BigDecimal sum = BigDecimal.ONE;
        int i = 1;
        final BigDecimal EPS = BigDecimal.ONE.scaleByPowerOfTen(-padded.getPrecision() - 2);
        while (term.abs().compareTo(EPS) > 0) {
            term = term.multiply(r, padded).divide(BigDecimal.valueOf(i), padded);
            sum = sum.add(term, padded);
            i++;
        }

        BigDecimal twoPowK = (k >= 0) ? ipow(BigDecimal.TWO, k, padded) : BigDecimal.ONE.divide(ipow(BigDecimal.TWO, -k, padded), padded);
        return sum.multiply(twoPowK, padded).round(mc);
    }

    public static BigDecimal pow(BigDecimal a, BigDecimal b) {
        return pow(a, b, new MathContext(Math.max(6, a.scale() + b.precision() + 2), RoundingMode.HALF_UP));
    }

    public static BigDecimal pow(BigDecimal a, BigDecimal b, MathContext mc) {
        if (b.signum() == 0) {
            if (a.signum() == 1) {
                return BigDecimal.ONE.round(mc);
            } else {
                return BigDecimal.ONE.negate().round(mc);
            }
        }
        if (a.compareTo(BigDecimal.ONE) == 0 || b.compareTo(BigDecimal.ONE) == 0) {
            return a.round(mc);
        }
        if (a.signum() == 0) {
            return BigDecimal.ZERO.round(mc);
        }

        // integer‑exponent fast path
        try {
            long n = b.longValueExact();
            return ipow(a, n, mc);
        } catch (ArithmeticException ignored) { /* b not an integer */ }

        if (a.signum() < 0) {
            throw new ArithmeticException("Cannot power negative base with non‑integer exponent");
        }

        BigDecimal lnA = ln(a, mc);
        BigDecimal y = b.multiply(lnA, mc);
        return exp(y, mc);
    }
}
