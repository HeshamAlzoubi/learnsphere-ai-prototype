package org.learnsphere.api;
import org.learnsphere.catalog.DemoCatalog;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@RestController @RequestMapping("/api/v1")
public class CatalogController {
 private final DemoCatalog catalog;
 public CatalogController(DemoCatalog catalog){this.catalog=catalog;}
 @GetMapping("/lessons") public List<LessonDto> lessons(){return catalog.lessons();}
 @GetMapping("/lessons/{id}") public LessonDto lesson(@PathVariable String id){return catalog.lesson(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Lesson not found"));}
 @GetMapping("/lessons/{id}/assessment") public AssessmentDto lessonAssessment(@PathVariable String id){return catalog.assessmentForLesson(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Assessment not found"));}
 @GetMapping("/assessments/{id}") public AssessmentDto assessment(@PathVariable String id){return catalog.assessment(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Assessment not found"));}
}
