package com.example.cukllteam3.domain.airquality;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import com.example.cukllteam3.dto.airquality.AirQualityResponseDto;
import com.example.cukllteam3.dto.airquality.KakaoCoordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 🌬️ [Client]
 * 환경공단 대기질 관련 OpenAPI 요청 및 응답 파싱
 */
@Component
@RequiredArgsConstructor
public class AirKoreaClient {
    @Value("${AIRKOREA_API_KEY}")
    private String airkoreaapikey;



    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper(); // ✅ JSON 전용 파서
    private final XmlMapper xmlMapper = new XmlMapper(); // ✅ XML → JSON 변환용 객체 추가


    /**
     * TM 좌표를 이용해 가장 가까운 측정소 이름 조회
     */
    public String getNearestStation(KakaoCoordResponse.TmCoord coord) {
        String url = "http://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList"
                + "?serviceKey=" + airkoreaapikey
                + "&tmX=" + coord.getX()
                + "&tmY=" + coord.getY()
                + "&returnType=json"; // ✅ JSON으로 받기

        System.out.println("🛰️ 호출한 URL: " + url);

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String json = response.getBody();
            System.out.println("📥 측정소 응답(JSON): " + json);

            JsonNode root = objectMapper.readTree(json);

            // 인증 실패 확인
            if (root.has("cmmMsgHeader")) {
                String error = root.path("cmmMsgHeader").path("returnAuthMsg").asText();
                System.err.println("❌ API 인증 실패: " + error);
                return "Unknown Station";
            }

            JsonNode itemsNode = root
                    .path("response")
                    .path("body")
                    .path("items");

            if (itemsNode.isMissingNode() || itemsNode.isNull()) {
                System.err.println("❗ items 노드가 존재하지 않음");
                return "Unknown Station";
            }

            JsonNode firstItem;
            if (itemsNode.isArray()) {
                firstItem = itemsNode.get(0); // 배열이면 첫 번째 항목
            } else {
                JsonNode itemNode = itemsNode.path("item");
                firstItem = itemNode.isArray() ? itemNode.get(0) : itemNode; // 단일 객체일 수도 있음
            }

            if (firstItem == null || !firstItem.has("stationName")) {
                System.err.println("❗ stationName 파싱 실패");
                return "Unknown Station";
            }

            String stationName = firstItem.path("stationName").asText();
            System.out.println("📍 [Step 2] 조회된 측정소 이름: " + stationName);
            return stationName;

        } catch (Exception e) {
            System.err.println("❌ 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return "Unknown Station";
        }
    }


    /**
     * 측정소명을 이용해 해당 지역의 실시간 대기질 정보 조회 (JSON 버전)
     */
    public AirQualityResponseDto getAirQuality(String stationName) {

        if ("Unknown Station".equals(stationName)) {
            System.err.println("⛔ 유효하지 않은 측정소 이름입니다. 대기질 조회 생략");
            return null;
        }

        String url = "https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"
                + "?stationName=" + stationName
                + "&dataTerm=DAILY&pageNo=1&numOfRows=1"
                + "&returnType=json"  // ✅ JSON 응답 요청
                + "&serviceKey=" + airkoreaapikey;

        System.out.println("🛰️ 호출한 URL: " + url);

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String json = response.getBody();
            System.out.println("📥 대기질 응답(JSON): " + json);

            JsonNode root = objectMapper.readTree(json); // ✅ ObjectMapper 사용

            // ✅ 인증 실패 응답 처리
            if (root.has("cmmMsgHeader")) {
                String error = root.path("cmmMsgHeader").path("returnAuthMsg").asText();
                System.err.println("❌ API 인증 실패: " + error);
                return null;
            }

            // ✅ 데이터 유효성 체크
            JsonNode items = root.path("response").path("body").path("items");
            if (!items.isArray() || items.size() == 0) {
                System.err.println("❗ 대기질 데이터 없음");
                return null;
            }

            JsonNode data = items.get(0); // 첫 번째 데이터만 사용
            return new AirQualityResponseDto(
                    stationName,
                    data.path("pm10Value").asText(),
                    data.path("pm25Value").asText(),
                    data.path("o3Value").asText(),
                    data.path("dataTime").asText()
            );
        } catch (Exception e) {
            System.err.println("❌ 대기질 조회 실패: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }



}
