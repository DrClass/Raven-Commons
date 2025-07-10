package io.github.drclass.commons;

/**
 * A generic, immutable 2-tuple representing an ordered pair of values.
 * <p>
 * The {@code Pair} record is a lightweight container frequently used for returning two independent results from a
 * method, joining keys with values, or grouping related items without creating a dedicated class. Both components are
 * stored exactly as they are supplied and may be {@code null}.
 * <p>
 * As a Java <em>record</em> this class is intrinsically:
 * <ul>
 *   <li><strong>Immutable</strong> – its state cannot change after construction.</li>
 *   <li><strong>Value-based</strong> – equality, hash code, and
 *       {@code toString()} are generated automatically (though
 *       {@code toString()} is overridden here for custom formatting).</li>
 *   <li><strong>Serializable</strong> – provided both components are serializable.</li>
 * </ul>
 *
 * @param <F>    the type of the first element
 * @param <S>    the type of the second element
 * @param first  the first element
 * @param second the second element
 */
public record Pair<F, S>(F first, S second) {

    /**
     * Returns the first component of this pair.
     *
     * @return the value supplied as {@code first} when the pair was created
     */
    public F getFirst() {
        return first;
    }

    /**
     * Returns the second component of this pair.
     *
     * @return the value supplied as {@code second} when the pair was created
     */
    public S getSecond() {
        return second;
    }

    /**
     * Returns a string representation in the form {@code "(first, second)"}.
     * <p>
     * This override is mainly cosmetic; the automatically generated {@code toString()} for records would produce
     * {@code "Pair[first=…, second=…]"}.
     *
     * @return a human-readable representation of this pair
     */
    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    /**
     * Creates a new {@code Pair} with the provided components.  This factory method offers type inference and can
     * improve readability compared with the {@code new Pair<>(…)} constructor syntax.
     *
     * @param <F>    the type of the first component
     * @param <S>    the type of the second component
     * @param first  the value to use as the first component (may be {@code null})
     * @param second the value to use as the second component (may be {@code null})
     * @return a new {@code Pair} containing {@code first} and {@code second}
     */
    public static <F, S> Pair<F, S> of(final F first, final S second) {
        return new Pair<>(first, second);
    }
}
