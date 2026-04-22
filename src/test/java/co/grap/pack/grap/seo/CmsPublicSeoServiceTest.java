package co.grap.pack.grap.seo;

import co.grap.pack.grap.user.content.model.CmsUserContentSearchParam;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;

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
}
