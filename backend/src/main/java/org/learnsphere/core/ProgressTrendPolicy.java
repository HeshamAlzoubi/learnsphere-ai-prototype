package org.learnsphere.core;

/** Descriptive comparison of two synthetic demo attempts; not a learning prediction. */
public final class ProgressTrendPolicy {
    private ProgressTrendPolicy() {}

    public static String classify(Integer latest, Integer previous) {
        if (latest == null || previous == null) return "INSUFFICIENT_DATA";
        if (latest < 0 || latest > 100 || previous < 0 || previous > 100)
            throw new IllegalArgumentException("Scores must be in [0,100]");
        if (latest > previous) return "IMPROVING";
        if (latest < previous) return "DECLINING";
        return "UNCHANGED";
    }
}
