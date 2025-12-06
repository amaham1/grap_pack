package co.grap.pack.grap.external.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 주유소 가격 정보 Entity
 * 외부 API: http://api.jejuits.go.kr/api/infoGasPriceList
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalGasPrice {
    /**
     * 내부 ID
     */
    private Long id;

    /**
     * Opinet 주유소 고유 ID
     */
    private String opinetId;

    /**
     * 휘발유 가격 (원)
     */
    private Integer gasolinePrice;

    /**
     * 고급 휘발유 가격 (원)
     */
    private Integer premiumGasolinePrice;

    /**
     * 경유 가격 (원)
     */
    private Integer dieselPrice;

    /**
     * LPG 가격 (원)
     */
    private Integer lpgPrice;

    /**
     * 가격 기준일
     */
    private LocalDate priceDate;

    /**
     * API 원본 데이터 (JSON 문자열)
     */
    private String apiRawData;

    /**
     * API 데이터 수집 시간
     */
    private LocalDateTime fetchedAt;

    /**
     * 생성 시간
     */
    private LocalDateTime createdAt;

    /**
     * 수정 시간
     */
    private LocalDateTime updatedAt;
}
