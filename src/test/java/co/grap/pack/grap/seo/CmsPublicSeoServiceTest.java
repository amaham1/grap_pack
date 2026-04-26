package co.grap.pack.grap.seo;

import co.grap.pack.grap.user.content.model.CmsUserContentSearchParam;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Grap 공개 SEO 서비스 테스트다.
 */
class CmsPublicSeoServiceTest {

    private final CmsPublicSeoService cmsPublicSeoService = new CmsPublicSeoService();

    /**
     * 필터가 걸린 목록 페이지는 noindex 정책을 사용하는지 검증한다.
     */
    @Test
    void applyContentListSeoMarksFilteredPageAsNoindex() {
        Model model = new ExtendedModelMap();
        CmsUserContentSearchParam searchParam = CmsUserContentSearchParam.builder()
                .keyword("제주")
                .page(2)
                .build();

        cmsPublicSeoService.applyContentListSeo(model, searchParam);

        assertThat(model.getAttribute("seoRobots")).isEqualTo("noindex, follow");
        assertThat(model.getAttribute("seoCanonical")).isEqualTo("https://grap.co.kr/grap/user/content/list");
        assertThat((List<?>) model.getAttribute("seoStructuredDataList")).isNotEmpty();
    }

    /**
     * 부동산 목록 페이지는 "제주도 부동산" 핵심 검색어를 명확히 포함하는지 검증한다.
     */
    @Test
    void applyRealEstateListSeoTargetsJejudoRealEstateKeyword() {
        Model model = new ExtendedModelMap();

        cmsPublicSeoService.applyRealEstateListSeo(model, null, null, null, 1, "202604");

        assertThat(model.getAttribute("seoTitle")).isEqualTo("제주도 부동산 실거래가");
        assertThat(model.getAttribute("seoDescription").toString()).contains("제주도 부동산");
        assertThat(model.getAttribute("seoRobots")).isEqualTo("index, follow");
        assertThat(model.getAttribute("seoCanonical")).isEqualTo("https://grap.co.kr/grap/user/content/real-estate");
        assertThat(String.join("", (List<String>) model.getAttribute("seoStructuredDataList")))
                .contains("제주도 부동산")
                .contains("제주특별자치도");
    }

    /**
     * 부동산 상세 페이지 제목에도 지역과 실거래가 검색어가 포함되는지 검증한다.
     */
    @Test
    void applyRealEstateDetailSeoUsesKeywordRichTitle() {
        Model model = new ExtendedModelMap();
        Map<String, Object> property = Map.of(
                "id", 120L,
                "displayName", "염광",
                "sggName", "제주시",
                "umdName", "아라일동",
                "address", "제주특별자치도 제주시 아라일동 6142-13",
                "formattedDisplayAmount", "2억 1,800만원",
                "formattedDealDate", "2026년 04월 20일",
                "dealDate", LocalDate.of(2026, 4, 20)
        );

        cmsPublicSeoService.applyRealEstateDetailSeo(model, property, null);

        assertThat(model.getAttribute("seoTitle")).isEqualTo("제주도 부동산 제주시 아라일동 염광 실거래가");
        assertThat(model.getAttribute("seoDescription").toString()).contains("제주도 부동산 실거래가");
        assertThat(model.getAttribute("seoCanonical"))
                .isEqualTo("https://grap.co.kr/grap/user/content/real-estate/120");
        assertThat(String.join("", (List<String>) model.getAttribute("seoStructuredDataList")))
                .contains("제주도 부동산")
                .contains("부동산 실거래가");
    }
}
