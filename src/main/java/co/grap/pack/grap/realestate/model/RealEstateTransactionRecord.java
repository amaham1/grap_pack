package co.grap.pack.grap.realestate.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 정규화된 부동산 실거래 레코드.
 */
@Getter
@Builder
public class RealEstateTransactionRecord {

    private Long id;
    private String externalRowKey;
    private String propertyMatchKey;
    private String datasetId;
    private String propertyCategory;
    private String transactionType;
    private String lawdCode;
    private String sggName;
    private String umdName;
    private String jibun;
    private String address;
    private String displayName;
    private String nameSource;
    private BigDecimal areaM2;
    private BigDecimal areaPyeong;
    private Integer floor;
    private Integer buildYear;
    private LocalDate dealDate;
    private String dealYearMonth;
    private Integer tradeAmountManwon;
    private Integer depositAmountManwon;
    private Integer monthlyRentManwon;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String rawJson;
    private LocalDateTime fetchedAt;
    private LocalDateTime updatedAt;
}
