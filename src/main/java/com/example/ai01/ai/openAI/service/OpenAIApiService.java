package com.example.ai01.ai.openAI.service;

import com.example.ai01.ai.openAI.dto.OpenAIRequest;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OpenAIApiService {
    @Value("${openai.api.key}")
    private String apiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/completions";
    private final OkHttpClient client = new OkHttpClient();

    public String getCompletion(OpenAIRequest openAIRequest) throws IOException {
        String jsonBody = String.format(
                "{\"model\": \"%s\", \"prompt\": \"%s\", \"max_tokens\": %d}",
                openAIRequest.getModel(),
                openAIRequest.getPrompt(),
                openAIRequest.getMaxTokens()
        );

        RequestBody body = RequestBody.create(
                MediaType.get("application/json"), jsonBody
        );

        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .header("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }



}
