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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class GrafanaService {

    private final RestTemplate restTemplate;

    @Value("${grafana.api.url}")
    private String grafanaUrl;

    @Value("${grafana.api.key}")
    private String apiKey;

    @Value("${grafana.admin.username}")
    private String adminUsername;

    @Value("${grafana.admin.password}")
    private String adminPassword;


    public int createOrganization(String orgName) {
        String url = UriComponentsBuilder.fromHttpUrl(grafanaUrl + "/api/orgs")
                .encode()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(adminUsername, adminPassword);  // admin 계정의 기본 인증 설정
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("name", orgName);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        // 응답 로그로 찍어보기
        log.info("Response from Grafana create organization API: {}", response.getBody());

        // JSON 응답에서 조직 ID 추출
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(response.getBody());
            int orgId = rootNode.path("orgId").asInt();  // "orgId" 대신 "id"로 조직 ID를 반환
            log.info("Parsed orgId: {}", orgId);
            return orgId;
        } catch (JsonProcessingException e) {
            log.error("Failed to parse Grafana response", e);
            throw new RuntimeException("Failed to parse Grafana response", e);
        }
    }



    // 회원가입 시 입력한 username과 password를 사용하여 grafana 계정 생성
    public void createGrafanaUser(String username, String email, String password) {
        String url = UriComponentsBuilder.fromHttpUrl(grafanaUrl + "/api/admin/users")
                .encode()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(adminUsername, adminPassword);

        Map<String, Object> body = new HashMap<>();
        body.put("name", username);
        body.put("email", email);
        body.put("login", username);
        body.put("password", password);
        body.put("OrgId", 1);  // 기본 조직 ID로 설정. 나중에 사용자를 특정 조직으로 이동시킬 수 있습니다.

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, requestEntity, String.class);
    }



    public void addUserToOrganization( String username, int orgId) {
        String url = UriComponentsBuilder.fromHttpUrl(grafanaUrl + "/api/orgs/" + orgId + "/users")
                .encode()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(adminUsername, adminPassword);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("loginOrEmail", username);
        body.put("role", "Viewer");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, requestEntity, String.class);
    }



    public void createDashboardForOrganization(int orgId, String dashboardJson) {
        String url = UriComponentsBuilder.fromHttpUrl(grafanaUrl + "/api/dashboards/db")
                .encode()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(adminUsername, adminPassword);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(dashboardJson, headers);
        restTemplate.postForEntity(url, requestEntity, String.class);
    }

    public void addDataSourceToOrganization(int orgId, String dataSourceJson) {
        String url = UriComponentsBuilder.fromHttpUrl(grafanaUrl + "/api/datasources")
                .encode()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);  // Bearer 토큰 사용
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(dataSourceJson, headers);
        restTemplate.postForEntity(url, requestEntity, String.class);
    }







}
