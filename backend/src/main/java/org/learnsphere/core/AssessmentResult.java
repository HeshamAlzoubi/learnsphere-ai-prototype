package org.learnsphere.core;

/** Immutable, internally consistent objective assessment result. */
public record AssessmentResult(int correct, int total, double percentage, RecommendationBand band) {
    public AssessmentResult {
        if (total <= 0 || correct < 0 || correct > total) {
            throw new IllegalArgumentException("Invalid assessment totals");
        }
        if (!Double.isFinite(percentage) || percentage < 0 || percentage > 100 || band == null) {
            throw new IllegalArgumentException("Invalid assessment result");
        }
        double expected = correct * 100.0 / total;
        if (Math.abs(percentage - expected) > 1e-9) {
            throw new IllegalArgumentException("Percentage disagrees with correct/total");
        }
        if (band != RecommendationPolicy.classify(percentage)) {
            throw new IllegalArgumentException("Recommendation band disagrees with percentage");
        }
    }
}
