package org.learnsphere.api;
import java.util.List;
public record AssessmentDto(String id, String lessonId, String title, List<QuestionDto> questions) {
 public record QuestionDto(String id, String prompt, List<OptionDto> options) {}
 public record OptionDto(String id, String text) {}
}
