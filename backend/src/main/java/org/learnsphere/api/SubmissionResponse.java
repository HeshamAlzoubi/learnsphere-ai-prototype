package org.learnsphere.api;
public record SubmissionResponse(String assessmentId, int correct, int total, double percentage, String recommendation) {}
