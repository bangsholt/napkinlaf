package net.sourceforge.napkinlaf.util;

/**
 * This interface defines the behavior of a source for random values.  Random
 * values will be distributed in a gaussian fashion around a middle point,
 * within a range that is one standard deviation away from the middle.
 * (Obviously actually generated values can be farther than one standard
 * deviation from the middle, so the "range" is really a "normative range", but
 * it's useful for predicting probable values.)  The source should have a random
 * value that remains the same until it is told to pick a new one via {@link
 * #randomize()}.
 */
public interface RandomValueSource {
    /** Selects a new random value for the source. */
    void randomize();

    /** @return The current value. */
    double get();

    /**
     * Equivalent to calling {@link @randomize()} and then {@link #get()}.
     *
     * @return A newly current value.
     */
    double generate();

    /**
     * Returns the middle value of the range around which values will be
     * generated.  New values will be distributed in a guassian fashion around
     * this middle point.
     *
     * @return The middle value of this source's range.
     */
    double getMid();

    /**
     * The size of the range around the middle value.  This will be one standard
     * deviation away from the middle value.  If the range is zero, the value
     * after a {@link #randomize()} will always be the mid point.
     *
     * @return The size of the range around the middle value.
     */
    double getRange();

    /**
     * Returns the current value's adjustment away from the middle.  Equivalent
     * to {@link #getMid()} - {@link #get()}.
     *
     * @return The current value's adjustment away from the middle.
     */
    double getAdjust();
}

