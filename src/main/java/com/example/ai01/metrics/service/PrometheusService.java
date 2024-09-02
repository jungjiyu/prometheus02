package com.example.ai01.metrics.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
@Service
public class PrometheusService {

    private final RestTemplate restTemplate;

    public String query(String query) {
        // 서비스명을 hostname으로 하여 Prometheus에 HTTP 쿼리날리기
        // UriComponentsBuilder를 사용하여 URL을 안전하게 빌드

        String prometheusUrl = UriComponentsBuilder.fromHttpUrl("http://prometheus:9090/api/v1/query")
                .queryParam("query", query)
                .encode()
                .toUriString();

        return restTemplate.getForObject(prometheusUrl, String.class);
    }

    public Map<String, Object> getJsonFormatUserUsage(String userId) {

        String query = String.format("sum(rate(http_server_requests_user_total{user_id=\"%s\"}[5m])) by (path)", userId);
        String result = query(query);

        // API 요청량과 과금 계산 (간단한 예시)
        double costPerRequest = 0.05; // 예: 요청당 $0.05
        double totalCost = 0.0;
        Map<String, Object> usageData = new HashMap<>();

        // JSON 파싱을 위해 ObjectMapper 사용
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(result);
        } catch (JsonProcessingException e) {
            usageData.put("error", "Error processing Prometheus response");
            return usageData;
        }

        // Prometheus 결과 파싱
        JsonNode dataNode = rootNode.path("data").path("result");
        if (dataNode.isArray()) {
            for (JsonNode node : dataNode) {
                String path = node.path("metric").path("path").asText();
                double requestCount = node.path("value").get(1).asDouble();

                // 각 경로별 비용 계산
                double cost = requestCount * costPerRequest;
                totalCost += cost;

                usageData.put(path, cost);
            }
        }

        // 총 비용과 사용자 ID를 응답에 포함
        usageData.put("user_id", userId);
        usageData.put("total_cost", totalCost);

        return usageData;
    }

}