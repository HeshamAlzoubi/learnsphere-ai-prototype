package org.learnsphere.api;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
public record SubmissionRequest(@NotEmpty List<@Valid Answer> answers) {
 public record Answer(@NotBlank String questionId, @NotBlank String optionId) {}
}
