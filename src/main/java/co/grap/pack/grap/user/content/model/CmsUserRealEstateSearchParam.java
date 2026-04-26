package co.grap.pack.grap.user.content.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 사용자 부동산 실거래가 목록 검색 조건.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CmsUserRealEstateSearchParam {

    private String keyword;
    private String dealYearMonth;
    private String sort;
    private Integer page;
    private String sggName;
    private String umdName;
    private String propertyCategory;
    private String transactionType;
    private Integer minAmount;
    private Integer maxAmount;
    private BigDecimal minAreaPyeong;
    private BigDecimal maxAreaPyeong;
    private Integer minBuildYear;
    private Integer maxBuildYear;
}
