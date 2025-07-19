package com.example.cukllteam3.domain.airquality;

import com.example.cukllteam3.domain.airquality.AirKoreaClient;
import com.example.cukllteam3.domain.airquality.KakaoMapClient;
import com.example.cukllteam3.dto.airquality.AirQualityResponseDto;
import com.example.cukllteam3.dto.airquality.KakaoCoordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 🌤️ [Service]
 * 비즈니스 로직 처리: 위도/경도를 TM좌표로 변환하고, 측정소 및 대기질 정보 조회
 */
@Service
@RequiredArgsConstructor
public class AirQualityService {

    private final KakaoMapClient kakaoMapClient;
    private final AirKoreaClient airKoreaClient;

    /**
     * 클라이언트로부터 받은 위도/경도를 통해 전체 대기질 조회 처리
     */
    public AirQualityResponseDto getAirQuality(double lat, double lon) {

        // 1. 카카오 API를 이용해 WGS84 좌표 → TM 좌표로 변환

        KakaoCoordResponse.TmCoord tmCoord = kakaoMapClient.convertToTm(lat, lon);

        // 2. TM 좌표를 기반으로 가장 가까운 측정소 이름을 환경공단 API로 조회
        String stationName = airKoreaClient.getNearestStation(tmCoord);
        System.out.println("📍 [Step 2] 조회된 측정소 이름: " + stationName);

        // 3. 해당 측정소의 실시간 대기질 정보(PM10, PM2.5, O3 등) 조회
        AirQualityResponseDto response = airKoreaClient.getAirQuality(stationName);


        return response;
    }
}


