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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class GrafanaService {


    private final RestTemplate restTemplate;

    @Value("${server.ip}")
    private String serverIp;

    @Value("${grafana.admin.username}")
    private String adminUsername;

    @Value("${grafana.admin.password}")
    private String adminPassword;

    // 조직별 API 키를 저장하기 위한 Map
    private final Map<Integer, String> orgApiKeys = new HashMap<>();

    public int createOrganization(String orgName) {
        String url = UriComponentsBuilder.fromHttpUrl("http://"+serverIp + ":3000/api/orgs")
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
            int orgId = rootNode.path("orgId").asInt();
            log.info("Parsed orgId: {}", orgId);

            // 조직 생성 후, 서비스 계정 및 API 키 생성
            String apiKey = createServiceAccountAndApiKey(orgId, orgName);
            orgApiKeys.put(orgId, apiKey); // 생성한 API 키를 Map에 저장

            return orgId;
        } catch (JsonProcessingException e) {
            log.error("Failed to parse Grafana response", e);
            throw new RuntimeException("Failed to parse Grafana response", e);
        }
    }

    // 서비스 계정 생성 및 API 토큰 생성
    private String createServiceAccountAndApiKey(int orgId, String orgName) {

        // Switch the org context for the Admin user to the new org:
        String switchConetextUrl = UriComponentsBuilder.fromHttpUrl(" http://"+adminUsername+":"+adminPassword+"@"+serverIp + ":3000/api/user/using/"+orgId)
                .encode()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(null,headers);
        ResponseEntity<String> switchConetextResponse = restTemplate.postForEntity(switchConetextUrl, requestEntity, String.class);

        // 응답 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(switchConetextResponse.getBody());
            log.info("정상적으로 admin 으로 조직의 컨텍스트가 전환됨: ", rootNode.asText() );

            
        } catch (JsonProcessingException e) {
            log.error("Failed to parse Switching the org context response", e);
            throw new RuntimeException("Failed to switch service context to admin", e);
        }




        String createServiceAccountUrl = UriComponentsBuilder.fromHttpUrl(" http://"+adminUsername+":"+adminPassword+"@"+serverIp + ":3000/api/user/using/"+orgId)
                .encode()
                .toUriString();


        Map<String, Object> body = new HashMap<>();
        body.put("name", orgName + "-service-account");
        body.put("role", "Admin"); // 서비스 계정에 할당할 역할 설정

        HttpHeaders headers2 = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity2 = new HttpEntity<>(body, headers2);
        ResponseEntity<String> serviceAccountResponse = restTemplate.postForEntity(createServiceAccountUrl, requestEntity2, String.class);



        // 응답으로부터 서비스 계정 ID 추출
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(serviceAccountResponse.getBody());
            int serviceAccountId = rootNode.path("id").asInt();
            log.info("정상적으로 서비스 계정이 생성됨");

            // 서비스 계정에 대한 API 토큰 생성
            return createApiTokenForServiceAccount(serviceAccountId);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse service account creation response", e);
            throw new RuntimeException("Failed to create service account", e);
        }
    }

    private String createApiTokenForServiceAccount(int serviceAccountId) {
        String apiTokenUrl = UriComponentsBuilder.fromHttpUrl(grafanaUrl + "/api/serviceaccounts/" + serviceAccountId + "/tokens")
                .encode()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(adminUsername, adminPassword);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("name", "api-token-for-service-account");
        body.put("role", "Admin"); // API 토큰에 대한 역할 설정

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> tokenResponse = restTemplate.postForEntity(apiTokenUrl, requestEntity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(tokenResponse.getBody());
            return rootNode.path("key").asText(); // API 키 반환
        } catch (JsonProcessingException e) {
            log.error("Failed to parse API token creation response", e);
            throw new RuntimeException("Failed to create API token", e);
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

    public void addUserToOrganization(String username, int orgId) {
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

        String apiKey = orgApiKeys.get(orgId); // 조직별 API 키 가져오기
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey); // 조직별 API 키 사용
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(dashboardJson, headers);
        restTemplate.postForEntity(url, requestEntity, String.class);
    }

    public void addDataSourceToOrganization(int orgId, String dataSourceJson) {
        String apiKey = orgApiKeys.get(orgId); // 조직별 API 키 가져오기

        String checkUrl = UriComponentsBuilder.fromHttpUrl(grafanaUrl + "/api/datasources/name/Prometheus")
                .encode()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey); // 조직별 API 키 사용
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response;
        try {
            response = restTemplate.getForEntity(checkUrl, String.class);
        } catch (HttpClientErrorException.NotFound e) {
            // 데이터 소스가 존재하지 않으면 추가
            String url = UriComponentsBuilder.fromHttpUrl(grafanaUrl + "/api/datasources")
                    .encode()
                    .toUriString();

            HttpEntity<String> requestEntity = new HttpEntity<>(dataSourceJson, headers);
            restTemplate.postForEntity(url, requestEntity, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to check data source existence", e);
        }
    }

}
