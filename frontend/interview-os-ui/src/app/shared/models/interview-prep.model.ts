export type TopicPriority = 'HIGH' | 'MEDIUM' | 'LOW';

export interface PrepTopic {
  name: string;
  priority: TopicPriority;
  why: string;
}

export interface PrepQuestion {
  question: string;
  category: string;
  answerHint: string;
}

export interface PrepStep {
  order: number;
  title: string;
  detail: string;
  estimatedMinutes: number | null;
}

export interface PrepResource {
  title: string;
  url: string;
  type: string;
  why: string;
  urlVerified: boolean;
}

export interface InterviewAnalysis {
  summary: string;
  topics: PrepTopic[];
  questions: PrepQuestion[];
  preparationPlan: PrepStep[];
  resources: PrepResource[];
  lastMinuteTips: string[];
}

export interface InterviewPrep {
  eventId: string;
  analysis: InterviewAnalysis;
  provider: string;
  generatedAt: string;
  fromCache: boolean;
}
