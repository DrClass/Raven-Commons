package io.github.drclass.commons.math;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TestBigDecimalMath {
    public static final BigDecimal POS = new BigDecimal("3.14159");
    public static final BigDecimal ZERO = new BigDecimal("0");
    public static final BigDecimal NEG = new BigDecimal("-3.14159");
    public static final MathContext MATH_CONTEXT = new MathContext(50, RoundingMode.HALF_UP);

    /**
     * Asserts that two BigDecimals are numerically equal (ignoring scale), optionally rounding them with the given
     * MathContext first.
     *
     * @param expected the expected value (must not be null)
     * @param actual   the actual   value (must not be null)
     * @param mc       the MathContext to use for rounding (if null, no rounding)
     */
    public static void assertDecimalEquals(BigDecimal expected, BigDecimal actual, MathContext mc) {
        Objects.requireNonNull(expected, "expected BigDecimal must not be null");
        Objects.requireNonNull(actual, "actual   BigDecimal must not be null");

        BigDecimal e = mc != null ? expected.round(mc) : expected;
        BigDecimal a = mc != null ? actual.round(mc) : actual;

        if (e.compareTo(a) != 0) {
            fail(String.format("⨯ BigDecimal mismatch:%n" +
                    "  expected = <%s>%n" +
                    "    actual = <%s>%n" +
                    "%s", e, a, (mc != null ? "  (rounded using " + mc + ")" : "")
            ));
        }
    }

    @Nested
    @DisplayName("sqrt(...)")
    class SqrtTests {
        static Stream<Arguments> validSqrtCases() {
            return Stream.of(
                    Arguments.of(POS, new BigDecimal("1.7724531023414977791280875500565385146252166183339")),
                    Arguments.of(ZERO, BigDecimal.ZERO)
            );
        }

        @ParameterizedTest(name = "sqrt{0} = {1}")
        @MethodSource("validSqrtCases")
        void sqrt_valid_returnsExpected(BigDecimal input, BigDecimal expected) {
            BigDecimal actual = BigDecimalMath.sqrt(input, MATH_CONTEXT);
            assertDecimalEquals(expected, actual, MATH_CONTEXT);
        }

        static Stream<BigDecimal> invalidSqrtCases() {
            return Stream.of(NEG);
        }

        @ParameterizedTest(name = "sqrt{0} throws")
        @MethodSource("invalidSqrtCases")
        void sqrt_invalid_throwsArithmeticException(BigDecimal input) {
            assertThrows(ArithmeticException.class, () -> BigDecimalMath.sqrt(input, MATH_CONTEXT));
        }
    }

    @Nested
    @DisplayName("ln(...)")
    class LnTests {
        static Stream<Arguments> validLnCases() {
            return Stream.of(
                    Arguments.of(POS, new BigDecimal("1.1447290411851783812164125804361594587905928083447"))
            );
        }

        @ParameterizedTest(name = "ln({0}) = {1}")
        @MethodSource("validLnCases")
        void ln_valid_returnsExpected(BigDecimal input, BigDecimal expected) {
            BigDecimal actual = BigDecimalMath.ln(input, MATH_CONTEXT);
            assertDecimalEquals(expected, actual, MATH_CONTEXT);
        }

        static Stream<BigDecimal> invalidLnCases() {
            return Stream.of(ZERO, NEG);
        }

        @ParameterizedTest(name = "ln({0}) throws")
        @MethodSource("invalidLnCases")
        void ln_invalid_throwsArithmeticException(BigDecimal input) {
            assertThrows(ArithmeticException.class, () -> BigDecimalMath.ln(input, MATH_CONTEXT));
        }
    }

    @Nested
    @DisplayName("exp(...)")
    class ExpTests {
        static Stream<Arguments> expCases() {
            return Stream.of(
                    Arguments.of(POS, new BigDecimal("23.140631226954963164517207589887985761017433256922")),
                    Arguments.of(ZERO, BigDecimal.ONE),
                    Arguments.of(NEG, new BigDecimal("0.043214032935936826737102548769119770695866118181655"))
            );
        }

        @ParameterizedTest(name = "e^{0} = {1}")
        @MethodSource("expCases")
        void exp_various_returnsExpected(BigDecimal input, BigDecimal expected) {
            BigDecimal actual = BigDecimalMath.exp(input, MATH_CONTEXT);
            assertDecimalEquals(expected, actual, MATH_CONTEXT);
        }
    }

    @Nested
    @DisplayName("exp(...)")
    class PowTests {
        static Stream<Arguments> validPowCases() {
            return Stream.of(
                    Arguments.of(POS, POS, new BigDecimal("36.461952093181081892079682721554265730804326980136")),
                    Arguments.of(POS, ZERO, new BigDecimal("1")),
                    Arguments.of(ZERO, POS, new BigDecimal("0")),
                    Arguments.of(POS, NEG, new BigDecimal("0.027425849209730452687455620548729758352796791049586")),
                    Arguments.of(NEG, ZERO, new BigDecimal("-1")),
                    Arguments.of(ZERO, NEG, new BigDecimal("0"))
            );
        }

        @ParameterizedTest(name = "{0} ^ {1} = {2}")
        @MethodSource("validPowCases")
        void pow_various_returnsExpected(BigDecimal input, BigDecimal power, BigDecimal expected) {
            BigDecimal actual = BigDecimalMath.pow(input, power, MATH_CONTEXT);
            assertDecimalEquals(expected, actual, MATH_CONTEXT);
        }

        static Stream<Arguments> invalidPowCases() {
            return Stream.of(
                    Arguments.of(NEG, POS)
            );
        }

        @ParameterizedTest(name = "{0} ^ {1} throws")
        @MethodSource("invalidPowCases")
        void pow_invalid_throwsArithmeticException(BigDecimal input, BigDecimal power) {
            assertThrows(ArithmeticException.class, () -> BigDecimalMath.pow(input, power, MATH_CONTEXT));
        }
    }
}
