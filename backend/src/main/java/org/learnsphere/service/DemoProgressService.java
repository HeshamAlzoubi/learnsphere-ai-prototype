package org.learnsphere.service;
import org.learnsphere.api.DemoProgressDto;
import org.learnsphere.catalog.DemoCatalog;
import org.learnsphere.core.ProgressTrendPolicy;
import org.learnsphere.persistence.DemoAttemptEntity;
import org.learnsphere.persistence.DemoAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service
public class DemoProgressService {
 private final DemoCatalog catalog;
 private final DemoAttemptRepository repository;
 public DemoProgressService(DemoCatalog catalog,DemoAttemptRepository repository){this.catalog=catalog;this.repository=repository;}
 @Transactional(readOnly=true)
 public DemoProgressDto overview(){
  // These attempts belong to a SHARED synthetic demo, not an identified learner.
  List<DemoAttemptEntity> attempts=repository.findAllByOrderByIdDesc();
  List<DemoProgressDto.LessonProgress> summaries=new ArrayList<>();
  catalog.lessons().forEach(lesson->{
   String assessmentId=catalog.assessmentForLesson(lesson.id()).orElseThrow().id();
   List<DemoAttemptEntity> history=attempts.stream().filter(a->assessmentId.equals(a.getAssessmentId())).toList();
   DemoAttemptEntity latest=history.isEmpty()?null:history.get(0);
   Integer percentage=latest==null?null:latest.getPercentage().intValue();
   String band=latest==null?null:latest.getRecommendation();
   String action=band==null?"START_LESSON":switch(band){case "REINFORCE"->"REVIEW_LESSON";case "PRACTICE"->"RETRY_ASSESSMENT";case "ADVANCE"->"EXPLORE_NEXT_LESSON";default->"REVIEW_LESSON";};
   Integer previous = history.size() < 2 ? null : history.get(1).getPercentage().intValue();
   String recentTrend = ProgressTrendPolicy.classify(percentage, previous);
   summaries.add(new DemoProgressDto.LessonProgress(lesson.id(),lesson.title(),history.size(),percentage,band,action,recentTrend));
  });
  return new DemoProgressDto(attempts.size(),List.copyOf(summaries));
 }
}
