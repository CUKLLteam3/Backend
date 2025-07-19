package com.example.cukllteam3.controller;

import com.example.cukllteam3.dto.airquality.AirQualityResponseDto;
import com.example.cukllteam3.domain.airquality.AirQualityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 🌤️ [Controller]
 * 클라이언트의 /air-quality 요청을 수신하고 응답을 반환하는 역할
 */
@RestController
@RequiredArgsConstructor // 생성자 주입
@RequestMapping("/air-quality")
public class AirQualityController {

    private final AirQualityService airQualityService; // 서비스 계층 주입

    /**
     * ✅ GET /air-quality?lat=위도&lon=경도
     * - 클라이언트로부터 위도/경도를 받아 대기질 정보를 응답
     */
    @GetMapping
    public ResponseEntity<AirQualityResponseDto> getAirQuality(@RequestParam double lat, @RequestParam double lon) {

        // 서비스 호출 → 응답 전달
        AirQualityResponseDto response = airQualityService.getAirQuality(lat, lon);
        return ResponseEntity.ok(response);
    }
}
