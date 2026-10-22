export interface Lesson { id: string; title: string; gradeBand: string; summary: string }
export interface Option { id: string; text: string }
export interface Question { id: string; prompt: string; options: Option[] }
export interface Assessment { id: string; lessonId: string; title: string; questions: Question[] }
export interface Answer { questionId: string; optionId: string }
export interface SubmissionResponse { assessmentId: string; correct: number; total: number; percentage: number; recommendation: string }

export interface LessonProgress { lessonId: string; lessonTitle: string; attemptCount: number; latestPercentage: number|null; latestRecommendation: string|null; suggestedAction: string }
export interface DemoProgress { totalAttempts: number; lessons: LessonProgress[] }
