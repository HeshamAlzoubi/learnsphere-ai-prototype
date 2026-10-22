package org.learnsphere.persistence;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity @Table(name="demo_assessment_attempts")
public class DemoAttemptEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="assessment_id",nullable=false,length=80) private String assessmentId;
 @Column(name="correct_count",nullable=false) private int correctCount;
 @Column(name="total_count",nullable=false) private int totalCount;
 @Column(name="percentage",nullable=false,precision=5,scale=2) private BigDecimal percentage;
 @Column(name="recommendation",nullable=false,length=32) private String recommendation;
 @Column(name="created_at",insertable=false,updatable=false,nullable=false) private LocalDateTime createdAt;
 @OneToMany(mappedBy="attempt",cascade=CascadeType.ALL,orphanRemoval=true) private List<DemoAnswerEntity> answers=new ArrayList<>();
 protected DemoAttemptEntity() {}
 public DemoAttemptEntity(String assessmentId,int correctCount,int totalCount,double percentage,String recommendation){
  this.assessmentId=assessmentId;this.correctCount=correctCount;this.totalCount=totalCount;
  this.percentage=BigDecimal.valueOf(percentage).setScale(2,java.math.RoundingMode.HALF_UP);this.recommendation=recommendation;
 }
 public void addAnswer(String questionId,String optionId){answers.add(new DemoAnswerEntity(this,questionId,optionId));}
 public Long getId(){return id;}
 public String getAssessmentId(){return assessmentId;}
 public int getCorrectCount(){return correctCount;}
 public int getTotalCount(){return totalCount;}
 public BigDecimal getPercentage(){return percentage;}
 public String getRecommendation(){return recommendation;}
 public List<DemoAnswerEntity> getAnswers(){return answers;}
}
