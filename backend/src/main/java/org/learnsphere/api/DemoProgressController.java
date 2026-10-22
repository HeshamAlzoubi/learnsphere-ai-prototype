package org.learnsphere.api;
import org.learnsphere.service.DemoProgressService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequestMapping("/api/v1/demo-progress")
public class DemoProgressController {
 private final DemoProgressService service;
 public DemoProgressController(DemoProgressService service){this.service=service;}
 @GetMapping public DemoProgressDto overview(){return service.overview();}
}
