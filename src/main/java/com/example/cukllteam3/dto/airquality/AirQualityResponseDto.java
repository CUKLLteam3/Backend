package com.example.cukllteam3.dto.airquality;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 📦 [DTO]
 * 클라이언트에게 전달할 대기질 정보 응답 객체
 */
@Getter
@AllArgsConstructor
public class AirQualityResponseDto{
    private String stationName;
    private String pm10;
    private String pm25;
    private String o3;
    private String dataTime;

}

