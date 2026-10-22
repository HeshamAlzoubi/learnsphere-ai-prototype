package org.learnsphere.catalog;
import org.learnsphere.api.*;
import org.springframework.stereotype.Component;
import java.util.*;
@Component
public final class DemoCatalog {
 private static AssessmentDto.OptionDto o(String id,String text){return new AssessmentDto.OptionDto(id,text);}
 private final List<LessonDto> lessons=List.of(
  new LessonDto("lesson-001","Introduction to Algorithms","6-8","Sequence, selection, and repetition"),
  new LessonDto("lesson-002","Decisions and Conditions","6-8","Boolean conditions and if/else decisions"),
  new LessonDto("lesson-003","Patterns and Loops","6-8","Recognizing repetition and loop boundaries"));
 private final Map<String,AssessmentDto> assessments=new LinkedHashMap<>();
 private final Map<String,Map<String,String>> answerKeys=new LinkedHashMap<>();
 public DemoCatalog(){
  add("assessment-001","lesson-001","Algorithms check",List.of(
   new AssessmentDto.QuestionDto("q1","What is an algorithm?",List.of(o("a","A type of computer hardware"),o("b","A sequence of steps for solving a problem"),o("c","A random collection of symbols"))),
   new AssessmentDto.QuestionDto("q2","Which structure repeats steps?",List.of(o("a","Selection"),o("b","Sequence"),o("c","Repetition (a loop)"))),
   new AssessmentDto.QuestionDto("q3","Why is order important?",List.of(o("a","Changing the order can change the result"),o("b","Order never affects a program"),o("c","Algorithms have no steps")))),Map.of("q1","b","q2","c","q3","a"));
  add("assessment-002","lesson-002","Conditions check",List.of(
   new AssessmentDto.QuestionDto("c1","What does an if statement do?",List.of(o("a","Checks a condition"),o("b","Repeats forever"),o("c","Deletes a variable"))),
   new AssessmentDto.QuestionDto("c2","When does an else branch run?",List.of(o("a","Always first"),o("b","When the if condition is false"),o("c","Only in loops"))),
   new AssessmentDto.QuestionDto("c3","Which is a Boolean value?",List.of(o("a","Seven"),o("b","Hello"),o("c","True")))),Map.of("c1","a","c2","b","c3","c"));
  add("assessment-003","lesson-003","Loops check",List.of(
   new AssessmentDto.QuestionDto("l1","What is a loop used for?",List.of(o("a","Storing pictures"),o("b","Repeating steps"),o("c","Renaming files"))),
   new AssessmentDto.QuestionDto("l2","What prevents many loops from running forever?",List.of(o("a","A stopping condition"),o("b","A longer variable name"),o("c","A comment"))),
   new AssessmentDto.QuestionDto("l3","How many times does repeat 3 times run its body?",List.of(o("a","Once"),o("b","Twice"),o("c","Three times")))),Map.of("l1","b","l2","a","l3","c"));
 }
 private void add(String id,String lessonId,String title,List<AssessmentDto.QuestionDto> questions,Map<String,String> key){
  assessments.put(id,new AssessmentDto(id,lessonId,title,questions));answerKeys.put(id,Map.copyOf(key));
 }
 public List<LessonDto> lessons(){return lessons;}
 public Optional<LessonDto> lesson(String id){return lessons.stream().filter(l->l.id().equals(id)).findFirst();}
 public Optional<AssessmentDto> assessment(String id){return Optional.ofNullable(assessments.get(id));}
 public Optional<AssessmentDto> assessmentForLesson(String lessonId){return assessments.values().stream().filter(a->a.lessonId().equals(lessonId)).findFirst();}
 public List<String> questionIds(String assessmentId){return assessment(assessmentId).orElseThrow(()->new NoSuchElementException("Assessment not found")).questions().stream().map(AssessmentDto.QuestionDto::id).toList();}
 public List<String> expected(String assessmentId){return questionIds(assessmentId).stream().map(answerKeys.get(assessmentId)::get).toList();}
}
