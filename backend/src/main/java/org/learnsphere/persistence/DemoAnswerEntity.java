package org.learnsphere.persistence;
import jakarta.persistence.*;
@Entity @Table(name="demo_attempt_answers",uniqueConstraints=@UniqueConstraint(name="uq_demo_attempt_question",columnNames={"attempt_id","question_id"}))
public class DemoAnswerEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="attempt_id",nullable=false) private DemoAttemptEntity attempt;
 @Column(name="question_id",nullable=false,length=80) private String questionId;
 @Column(name="option_id",nullable=false,length=80) private String optionId;
 protected DemoAnswerEntity() {}
 DemoAnswerEntity(DemoAttemptEntity attempt,String questionId,String optionId){this.attempt=attempt;this.questionId=questionId;this.optionId=optionId;}
 public String getQuestionId(){return questionId;}
 public String getOptionId(){return optionId;}
}
