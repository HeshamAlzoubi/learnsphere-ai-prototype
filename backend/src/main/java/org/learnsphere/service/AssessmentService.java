package org.learnsphere.service;
import org.learnsphere.api.*;
import org.learnsphere.catalog.DemoCatalog;
import org.learnsphere.core.*;
import org.learnsphere.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service
public class AssessmentService {
 private final DemoCatalog catalog;
 private final DemoAttemptRepository attempts;
 public AssessmentService(DemoCatalog catalog,DemoAttemptRepository attempts){this.catalog=catalog;this.attempts=attempts;}
 @Transactional
 public SubmissionResponse submit(String assessmentId,SubmissionRequest request){
  AssessmentDto assessment=catalog.assessment(assessmentId).orElseThrow(()->new NoSuchElementException("Assessment not found"));
  List<String> ids=catalog.questionIds(assessmentId);
  if(request==null || request.answers()==null || request.answers().size()!=ids.size())throw new IllegalArgumentException("Exactly one answer is required per question");
  Map<String,String> byQuestion=new HashMap<>();
  Map<String,Set<String>> allowed=new HashMap<>();
  for(AssessmentDto.QuestionDto q:assessment.questions()){
   Set<String> choices=new HashSet<>();for(AssessmentDto.OptionDto o:q.options())choices.add(o.id());allowed.put(q.id(),choices);
  }
  for(SubmissionRequest.Answer answer:request.answers()){
   if(answer==null || answer.questionId()==null || answer.optionId()==null || !allowed.containsKey(answer.questionId()) || !allowed.get(answer.questionId()).contains(answer.optionId()) || byQuestion.putIfAbsent(answer.questionId(),answer.optionId())!=null)
    throw new IllegalArgumentException("Unknown or duplicate question/option ID");
  }
  List<String> ordered=ids.stream().map(byQuestion::get).toList();
  AssessmentResult result=ScoringPolicy.score(catalog.expected(assessmentId),ordered);
  DemoAttemptEntity attempt=new DemoAttemptEntity(assessmentId,result.correct(),result.total(),result.percentage(),result.band().name());
  for(String id:ids)attempt.addAnswer(id,byQuestion.get(id));
  attempts.saveAndFlush(attempt);
  return new SubmissionResponse(assessmentId,result.correct(),result.total(),result.percentage(),result.band().name());
 }
}
