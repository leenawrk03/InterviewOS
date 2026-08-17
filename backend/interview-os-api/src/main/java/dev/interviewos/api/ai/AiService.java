package dev.interviewos.api.ai;

/**
 * Provider-agnostic AI boundary. Gemini today; Hugging Face / OpenRouter
 * can be dropped in by adding another @Service implementation and flipping
 * app.ai.provider — no calling code changes.
 */
public interface AiService {

    InterviewAnalysis analyseInterview(InterviewPrepRequest request);

    String providerName();
}
