package org.learnsphere.core;

import java.util.List;

/** Dependency-free verification harness for the first design increment. */
public final class PolicySelfTest {
    private static int assertions = 0;
    private static void check(boolean condition, String message) {
        assertions++;
        if (!condition) throw new AssertionError(message);
    }
    private static void rejects(Runnable action) {
        assertions++;
        try { action.run(); } catch (IllegalArgumentException expected) { return; }
        throw new AssertionError("Expected IllegalArgumentException");
    }
    public static void main(String[] args) {
        check(RecommendationPolicy.classify(0) == RecommendationBand.REINFORCE, "zero");
        check(RecommendationPolicy.classify(59.99) == RecommendationBand.REINFORCE, "below 60");
        check(RecommendationPolicy.classify(60) == RecommendationBand.PRACTICE, "60 boundary");
        check(RecommendationPolicy.classify(84.99) == RecommendationBand.PRACTICE, "below 85");
        check(RecommendationPolicy.classify(85) == RecommendationBand.ADVANCE, "85 boundary");
        check(RecommendationPolicy.classify(100) == RecommendationBand.ADVANCE, "100");
        rejects(() -> RecommendationPolicy.classify(Double.NaN));
        rejects(() -> RecommendationPolicy.classify(-1));
        rejects(() -> RecommendationPolicy.classify(101));
        AssessmentResult a = ScoringPolicy.score(List.of("A","B","C","D","A"),List.of("A","B","C","D","B"));
        check(a.correct() == 4 && a.total() == 5, "4 of 5");
        check(a.percentage() == 80.0, "80 percent");
        check(a.band() == RecommendationBand.PRACTICE, "practice band");
        AssessmentResult b = ScoringPolicy.score(List.of("A","B"),List.of("A","B"));
        check(b.percentage() == 100 && b.band() == RecommendationBand.ADVANCE, "full score");
        rejects(() -> ScoringPolicy.score(List.of(),List.of()));
        rejects(() -> ScoringPolicy.score(List.of("A"),List.of("A","B")));
        rejects(() -> ScoringPolicy.score(List.of("A"),List.of("")));
        rejects(() -> new AssessmentResult(0, 1, Double.NaN, RecommendationBand.REINFORCE));
        rejects(() -> new AssessmentResult(0, 1, Double.POSITIVE_INFINITY, RecommendationBand.REINFORCE));
        rejects(() -> new AssessmentResult(1, 2, 75, RecommendationBand.PRACTICE));
        rejects(() -> new AssessmentResult(1, 2, 50, RecommendationBand.ADVANCE));
        rejects(() -> new AssessmentResult(2, 1, 100, RecommendationBand.ADVANCE));
        System.out.println("PASS: " + assertions + " policy assertions");
    }
}
