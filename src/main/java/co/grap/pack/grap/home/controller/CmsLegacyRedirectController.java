package co.grap.pack.grap.home.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * 과거 공개 URL을 현재 Grap 공개 URL로 영구 이동시킨다.
 */
@Controller
public class CmsLegacyRedirectController {

    /**
     * 과거 제주도 부동산 후보 경로를 현재 부동산 실거래가 페이지로 연결한다.
     *
     * @return 301 리다이렉트 뷰
     */
    @GetMapping({"/alljeju/real-estate", "/alljeju/real-estate/"})
    public RedirectView redirectLegacyRealEstate() {
        RedirectView redirectView = new RedirectView("/grap/user/content/real-estate");
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return redirectView;
    }
}
