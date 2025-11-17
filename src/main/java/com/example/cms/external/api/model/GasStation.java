package com.example.cms.external.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 주유소 기본 정보 Entity
 * 외부 API: http://api.jejuits.go.kr/api/infoGasPriceList
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GasStation {
    /**
     * 내부 ID
     */
    private Long id;

    /**
     * Opinet 주유소 고유 ID
     */
    private String opinetId;

    /**
     * 주유소명
     */
    private String stationName;

    /**
     * 브랜드명 (SK, GS, S-OIL 등)
     */
    private String brand;

    /**
     * 주소
     */
    private String address;

    /**
     * 위도
     */
    private BigDecimal latitude;

    /**
     * 경도
     */
    private BigDecimal longitude;

    /**
     * 전화번호
     */
    private String phone;

    /**
     * API 원본 데이터 (JSON 문자열)
     */
    private String apiRawData;

    /**
     * 생성 시간
     */
    private LocalDateTime createdAt;

    /**
     * 수정 시간
     */
    private LocalDateTime updatedAt;
}
