package org.learnsphere.api;
import org.learnsphere.service.AssessmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/assessments")
public class AssessmentController {
 private final AssessmentService service;
 public AssessmentController(AssessmentService service){this.service=service;}
 @PostMapping("/{id}/submissions") public SubmissionResponse submit(@PathVariable String id,@Valid @RequestBody SubmissionRequest request){return service.submit(id,request);}
}
