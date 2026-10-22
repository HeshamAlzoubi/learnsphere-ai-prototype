package org.learnsphere.catalog;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class DemoCatalogTest {
 private final DemoCatalog catalog=new DemoCatalog();
 @Test void threeDistinctLessonsAndAssessments(){assertEquals(3,catalog.lessons().size());for(var lesson:catalog.lessons()){var assessment=catalog.assessmentForLesson(lesson.id()).orElseThrow();assertEquals(lesson.id(),assessment.lessonId());assertEquals(3,assessment.questions().size());assertEquals(3,catalog.expected(assessment.id()).size());}}
 @Test void answerKeysNotExposedByQuestionDtos(){for(var lesson:catalog.lessons()){var a=catalog.assessmentForLesson(lesson.id()).orElseThrow();for(var q:a.questions()){assertEquals(3,q.options().size());for(var o:q.options()){assertNotNull(o.id());assertFalse(o.text().isBlank());}}}}
 @Test void unknownResourcesAbsent(){assertTrue(catalog.lesson("missing").isEmpty());assertTrue(catalog.assessment("missing").isEmpty());assertTrue(catalog.assessmentForLesson("missing").isEmpty());}
}
