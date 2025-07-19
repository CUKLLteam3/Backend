package com.example.cukllteam3.domain.airquality;

import com.example.cukllteam3.dto.airquality.KakaoCoordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 🗺️ [Client]
 * 카카오 API를 사용하여 WGS84 좌표(lat, lon)를 TM 좌표로 변환
 */
@Component
@RequiredArgsConstructor
public class KakaoMapClient {

    @Value("${KAKAO_API_KEY}")
    private String kakaoApiKey; // 카카오 REST API 키 (KakaoAK 형태로 사용)

    private final RestTemplate restTemplate;

    /**
     * 위도(lat), 경도(lon) → TM 좌표(x, y)로 변환
     */
    public KakaoCoordResponse.TmCoord convertToTm(double lat, double lon) {
        // API 요청 URL 생성
        String url = "https://dapi.kakao.com/v2/local/geo/transcoord.json"
                + "?x=" + lon
                + "&y=" + lat
                + "&input_coord=WGS84"
                + "&output_coord=TM";

        // Authorization 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // 카카오 API 호출
        ResponseEntity<KakaoCoordResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, KakaoCoordResponse.class);

        // 첫 번째 좌표 결과 반환
        return response.getBody().getDocuments().get(0);
    }
}


