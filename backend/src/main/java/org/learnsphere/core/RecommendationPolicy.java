package org.learnsphere.core;

/** Illustrative thresholds; not experimentally validated learning cutoffs. */
public final class RecommendationPolicy {
    private RecommendationPolicy() {}

    public static RecommendationBand classify(double percentage) {
        if (!Double.isFinite(percentage) || percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be finite and in [0,100]");
        }
        if (percentage < 60.0) return RecommendationBand.REINFORCE;
        if (percentage < 85.0) return RecommendationBand.PRACTICE;
        return RecommendationBand.ADVANCE;
    }
}
