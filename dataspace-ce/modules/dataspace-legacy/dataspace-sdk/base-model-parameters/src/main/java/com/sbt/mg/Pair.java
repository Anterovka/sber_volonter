package com.sbt.mg;


import java.util.Objects;

/**
 * Para
 *
 * @param <F> Type of the first element
 * @param <S> Type of second element
 */
public class Pair<F, S> {

    private final F first;
    private final S second;

    /**
     * @param first  The first element
     * @param second The second element
     */
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Get the first element
     */
    public F getFirst() {
        return first;
    }

    /**
     * Get the second element
     */
    public S getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) object;
        return (Objects.equals(first, pair.first) && Objects.equals(second, pair.second));
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
