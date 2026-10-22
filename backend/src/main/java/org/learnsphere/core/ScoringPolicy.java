package org.learnsphere.core;

import java.util.List;
import java.util.Objects;

/** Scores objective answers against a server-held answer key. No persistence or identity. */
public final class ScoringPolicy {
    private ScoringPolicy() {}

    public static AssessmentResult score(List<String> expected, List<String> submitted) {
        Objects.requireNonNull(expected, "expected");
        Objects.requireNonNull(submitted, "submitted");
        if (expected.isEmpty() || expected.size() != submitted.size()) {
            throw new IllegalArgumentException("Answer count mismatch or empty assessment");
        }
        int correct = 0;
        for (int i = 0; i < expected.size(); i++) {
            String key = Objects.requireNonNull(expected.get(i), "answer key entry");
            String answer = Objects.requireNonNull(submitted.get(i), "submitted answer");
            if (key.isBlank() || answer.isBlank()) {
                throw new IllegalArgumentException("Blank answer or answer key");
            }
            if (key.equals(answer)) correct++;
        }
        double percentage = correct * 100.0 / expected.size();
        return new AssessmentResult(correct, expected.size(), percentage, RecommendationPolicy.classify(percentage));
    }
}
