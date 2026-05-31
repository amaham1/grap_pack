package co.grap.pack.grap.home.controller;

import co.grap.pack.grap.seo.CmsPublicSeoService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.view.RedirectView;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Grap 홈 컨트롤러 테스트다.
 */
class CmsHomeControllerTest {

    /**
     * Grap 서비스 루트가 제주도 부동산 대표 페이지로 이동하는지 검증한다.
     */
    @Test
    void grapHomeRedirectsToRealEstateLandingPage() {
        CmsHomeController controller = new CmsHomeController(mock(CmsPublicSeoService.class));

        RedirectView redirectView = controller.grapHome();

        assertThat(redirectView.getUrl()).isEqualTo("https://grap.co.kr/grap/user/content/real-estate");
        assertThat(ReflectionTestUtils.getField(redirectView, "statusCode"))
                .isEqualTo(HttpStatus.MOVED_PERMANENTLY);
    }
}
