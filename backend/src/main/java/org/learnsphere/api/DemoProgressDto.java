package org.learnsphere.api;
import java.util.List;
public record DemoProgressDto(int totalAttempts, List<LessonProgress> lessons) {
 public record LessonProgress(String lessonId,String lessonTitle,int attemptCount,Integer latestPercentage,String latestRecommendation,String suggestedAction,String recentTrend) {}
}
