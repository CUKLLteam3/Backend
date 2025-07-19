package com.example.cukllteam3.dto.airquality;

import lombok.Data;
import java.util.List;

/**
 * 📦 [DTO]
 * 카카오 TM 좌표 변환 API 응답 구조
 */
@Data
public class KakaoCoordResponse {
    private List<TmCoord> documents;

    @Data
    public static class TmCoord {
        private double x;  // tmX
        private double y;  // tmY
    }
}


