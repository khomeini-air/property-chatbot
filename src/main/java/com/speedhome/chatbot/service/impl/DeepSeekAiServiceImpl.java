package com.speedhome.chatbot.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.speedhome.chatbot.entity.ChatMessage;
import com.speedhome.chatbot.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.speedhome.chatbot.util.Constant.CHOICES;
import static com.speedhome.chatbot.util.Constant.CONTENT;
import static com.speedhome.chatbot.util.Constant.ERROR;
import static com.speedhome.chatbot.util.Constant.MESSAGE;
import static com.speedhome.chatbot.util.Constant.MESSAGES;
import static com.speedhome.chatbot.util.Constant.MODEL;
import static com.speedhome.chatbot.util.Constant.ROLE;
import static com.speedhome.chatbot.util.Constant.ROLE_USER;
import static com.speedhome.chatbot.util.Constant.TEMPERATURE;

@Service
@Slf4j
public class DeepSeekAiServiceImpl implements AiService {

    @Value("${ai.deepseek.api-key}")
    private String apiKey;

    @Value("${ai.deepseek.api-url}")
    private String apiUrl;

    @Value("${ai.deepseek.model}")
    private String model;

    @Value("${ai.deepseek.temperature:1.3}")
    private Double temperature;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String generateResponse(String sessionId, List<ChatMessage> messages) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(assemblePostRequest(messages))) {

            // Get response code
            int statusCode = response.getStatusLine().getStatusCode();

            // Get the response body
            String responseBody = EntityUtils.toString(response.getEntity());
            log.debug("Deepseek response body: {}", responseBody);

            // Parse and return response content
            return parseResponse(statusCode, responseBody);
        } catch (IOException e) {
            String errorMessage = "DeepSeek Error";
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    private HttpPost assemblePostRequest(List<ChatMessage> messages) throws IOException {
        HttpPost httpPost = new HttpPost(apiUrl);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);

        // Assemble request body
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put(MODEL, model);
        requestBody.put(TEMPERATURE, temperature);

        ArrayNode messagesNode = requestBody.putArray(MESSAGES);
        messages.forEach(msg -> messagesNode.addObject()
                .put("role", msg.getRole())
                .put("content", msg.getContent()));


        // Set http post entity
        httpPost.setEntity(new StringEntity(requestBody.toString()));
        log.debug("DeepSeek request body: {}", requestBody);

        return httpPost;
    }

    private String parseResponse(int statusCode, String responseBody) throws IOException {
        if (statusCode >= 200 && statusCode < 300) {
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            String response = jsonResponse.path(CHOICES)
                    .path(0)
                    .path(MESSAGE)
                    .path(CONTENT)
                    .asText();
            log.info("DeepSeek success response: {}", response);
            return response;
        } else {
            log.error("DeepSeek API Error - Status: {}, Response: {}", statusCode, responseBody);
            throw new RuntimeException(
                    String.format("DeepSeek API failed with status %d: %s", statusCode, extractErrorMessage(responseBody))
            );
        }
    }

    private String extractErrorMessage(String responseBody) {
        try {
            JsonNode errorNode = objectMapper.readTree(responseBody).path(ERROR);
            return errorNode.path(MESSAGE).asText("Unknown error");
        } catch (Exception e) {
            return "Unable to parse error response";
        }
    }
}
