package com.training.studyfx.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GeminiService {

    private final String apiKey;
    private final String endpoint;        // Địa chỉ URL của API
    private final HttpClient httpClient;
    private float temperature = 0.7f;     // Độ sáng tạo

    public GeminiService(String apiKey) {
        this.apiKey = apiKey;
        this.endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    public GeminiService() {
        this("AIzaSyBv7KAeo7PU5NfWu8lmqR1Zvp8e6Cby51U");  //API KEY
    }

    public GeminiService withTemperature(float temperature) {
        this.temperature = Math.max(0.0f, Math.min(1.0f, temperature));
        return this;
    }

    public String generateResponse(String userMessage) throws Exception {
        JSONObject requestBody = new JSONObject();
        JSONArray contents = new JSONArray();
        JSONObject content = new JSONObject();
        JSONArray parts = new JSONArray();
        JSONObject part = new JSONObject();

        part.put("text", userMessage);  // Nội dung chat người dùng
        parts.put(part);                // Đóng gói nó lại
        content.put("parts", parts);
        contents.put(content);
        requestBody.put("contents", contents);
        requestBody.put("generationConfig", new JSONObject().put("temperature", temperature));

        String payload = requestBody.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                //System.out.println("RESPONSE JSON:\n" + response.body()); -- XEM form output của JSON
                return extractContent(response.body());
            } else {
                JSONObject errorJson = new JSONObject(response.body());
                String errorMessage = errorJson.has("error") ?
                    errorJson.getJSONObject("error").optString("message", "Unknown error") :
                    "Error code: " + response.statusCode();
                throw new RuntimeException("Gemini API error: " + errorMessage);
            }
        } catch (JSONException e) {
            throw new RuntimeException("Failed to parse JSON response", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to communicate with Gemini API", e);
        }
    }


    private String extractContent(String json) {
        try {
            JSONObject responseJson = new JSONObject(json);
            JSONArray candidates = responseJson.getJSONArray("candidates");
            if (candidates.length() > 0) {
                JSONObject candidate = candidates.getJSONObject(0);
                JSONObject content = candidate.getJSONObject("content");
                JSONArray parts = content.getJSONArray("parts");
                if (parts.length() > 0) {
                    return parts.getJSONObject(0).getString("text");
                }
            }
            return "(No content in response)";
        } catch (JSONException e) {
            return "(Error parsing response: " + e.getMessage() + ")";
        }
    }

    public Map<String, Object> generateRichResponse(String userMessage) throws Exception {
        String textResponse = generateResponse(userMessage);
        Map<String, Object> richResponse = new HashMap<>();
        richResponse.put("text", textResponse);
        richResponse.put("success", true);
        return richResponse;
    }
}