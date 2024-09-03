package com.example.ai01.metrics.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@Service
public class PrometheusService {

    private final RestTemplate restTemplate;

    @Value("${prometheus.api.url}")
    private String prometheusUrl;



    public String query(String query) {
        // 서비스명을 hostname으로 하여 Prometheus에 HTTP 쿼리날리기
        // Prometheus POST 요청 URL << JSON 데이터 보낼때 get 요청 쓰면  "Not enough variable values available to expand" 에러가 발생한다
        String prometheusUrl = UriComponentsBuilder.fromHttpUrl(this.prometheusUrl)
                .encode()
                .toUriString();

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 본문 설정 (쿼리를 form 데이터로 전송)
        String requestBody = "query=" + query;
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // POST 요청 전송
        return restTemplate.postForObject(prometheusUrl, requestEntity, String.class);
    }

    public Map<String, Object> getJsonFormatUserUsage(String userId) {

        // [1h]는 최근 1시간의 데이터를 의미하며, 이 기간 이전의 기록은 포함되지 않는다.
        // Prometheus 쿼리를 통해 사용자 요청 횟수를 가져옴
        String query = String.format("sum(http_server_requests_user_total{user_id=\"%s\"}) by (user_id)", userId);
        String result = query(query);
        log.info("Prometheus 쿼리 결과: {}", result);


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

        log.info("dataNode: {}", dataNode.toString());


        if (dataNode.isArray() && dataNode.size() > 0) {
            for (JsonNode node : dataNode) {
                double requestCount = node.path("value").get(1).asDouble();
                log.info("requestCount: {}", requestCount);

                // 각 경로별 비용 계산
                double cost = requestCount * costPerRequest;
                totalCost += cost;

                // 필요시 사용자별로 비용을 별도 저장 가능
                usageData.put("request_count", requestCount);
            }
        }

        // 총 비용과 사용자 ID를 응답에 포함
        usageData.put("user_id", userId);
        usageData.put("total_cost", totalCost);

        return usageData;
    }

}