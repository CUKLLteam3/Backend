package com.example.cukllteam3.domain.airquality;

import com.example.cukllteam3.dto.airquality.AirQualityResponseDto;
import com.example.cukllteam3.dto.airquality.KakaoCoordResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class AirKoreaClient {

    @Value("${AIRKOREA_API_KEY}")
    private String airkoreaapikey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 🌐 TM 좌표를 이용해 가장 가까운 측정소 이름 조회
     */
    public String getNearestStation(KakaoCoordResponse.TmCoord coord) {
        try {
            String encodedKey = URLEncoder.encode(airkoreaapikey, StandardCharsets.UTF_8);

            URI uri = UriComponentsBuilder
                    .fromHttpUrl("https://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList?serviceKey=" + encodedKey)
                    .queryParam("tmX", coord.getX())
                    .queryParam("tmY", coord.getY())
                    .queryParam("returnType", "json")
                    .build(true)
                    .toUri();

            System.out.println("🛰️ 호출한 URL: " + uri);

            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            String json = response.getBody();
            System.out.println("📥 측정소 응답(JSON): " + json);

            JsonNode root = objectMapper.readTree(json);

            if (root.has("cmmMsgHeader")) {
                String error = root.path("cmmMsgHeader").path("returnAuthMsg").asText();
                System.err.println("❌ [측정소 조회] 인증 실패: " + error);
                return "Unknown Station";
            }

            JsonNode items = root.path("response").path("body").path("items");
            if (!items.isArray() || items.size() == 0) {
                System.err.println("❗ [측정소 조회] 유효한 측정소 데이터 없음");
                return "Unknown Station";
            }

            JsonNode firstItem = items.get(0);
            String stationName = firstItem.path("stationName").asText(null);

            if (stationName == null) {
                System.err.println("❗ [측정소 조회] stationName 파싱 실패");
                return "Unknown Station";
            }

            System.out.println("📍 [Step 2] 조회된 측정소 이름: " + stationName);
            return stationName;

        } catch (Exception e) {
            System.err.println("❌ [측정소 조회] 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return "Unknown Station";
        }
    }

    /**
     * 🌐 측정소명을 이용한 실시간 대기질 정보 조회
     */
    public AirQualityResponseDto getAirQuality(String stationName) {
        if ("Unknown Station".equals(stationName)) {
            System.err.println("⛔ 유효하지 않은 측정소 이름입니다. 대기질 조회 생략");
            return null;
        }

        try {
            System.out.println(airkoreaapikey);
            String encodedKey = URLEncoder.encode(airkoreaapikey, StandardCharsets.UTF_8);
            System.out.println(encodedKey);
            String encodedStation = URLEncoder.encode(stationName, StandardCharsets.UTF_8);


            URI uri = UriComponentsBuilder
                    /*
                    .fromHttpUrl("https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?serviceKey="
                            + encodedKey +"&stationName=" + encodedStation)
                    //.queryParam("serviceKey", encodedKey)        // ✅ 직접 인코딩된 키 넣기
                    //.queryParam("stationName", encodedStation)   // ✅ 측정소명도 인코딩
                    .queryParam("dataTerm", "DAILY")
                    .queryParam("pageNo", 1)
                    .queryParam("numOfRows", 1)
                    .queryParam("returnType", "json")
                    .build(true) // ❗❗ false로 해야 중복 인코딩 안 함!
                    .toUri();

                     */
                    .fromHttpUrl("https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty")
                    .queryParam("serviceKey", encodedKey)        // ✅ 직접 인코딩된 키 넣기
                    .queryParam("stationName", encodedStation)   // ✅ 측정소명도 인코딩
                    .queryParam("dataTerm", "DAILY")
                    .queryParam("pageNo", 1)
                    .queryParam("numOfRows", 1)
                    .queryParam("returnType", "json")
                    .build(true) // ❗❗ false로 해야 중복 인코딩 안 함!
                    .toUri();


            System.out.println("🛰️ 호출한 URL: " + uri);

            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            String json = response.getBody();
            System.out.println("📥 대기질 응답(JSON): " + json);

            JsonNode root = objectMapper.readTree(json);

            if (root.has("cmmMsgHeader")) {
                String error = root.path("cmmMsgHeader").path("returnAuthMsg").asText();
                System.err.println("❌ [대기질 조회] 인증 실패: " + error);
                return null;
            }

            JsonNode items = root.path("response").path("body").path("items");
            if (!items.isArray() || items.size() == 0) {
                System.err.println("❗ [대기질 조회] 데이터 없음");
                return null;
            }

            JsonNode data = items.get(0);
            return new AirQualityResponseDto(
                    stationName,
                    data.path("pm10Value").asText(),
                    data.path("pm25Value").asText(),
                    data.path("o3Value").asText(),
                    data.path("dataTime").asText()
            );

        } catch (Exception e) {
            System.err.println("❌ [대기질 조회] 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
