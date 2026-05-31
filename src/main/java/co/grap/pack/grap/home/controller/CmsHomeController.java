package co.grap.pack.grap.home.controller;

import co.grap.pack.grap.seo.CmsPublicSeoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * 루트 홈 컨트롤러다.
 */
@Controller
@RequiredArgsConstructor
public class CmsHomeController {

    private final CmsPublicSeoService cmsPublicSeoService;

    /**
     * 루트 랜딩 페이지를 보여준다.
     *
     * @param model 화면 모델
     * @return 랜딩 템플릿
     */
    @GetMapping("/")
    public String home(Model model) {
        cmsPublicSeoService.applyLandingSeo(model);
        return "landing";
    }

    /**
     * Grap 서비스 루트는 제주도 부동산 대표 페이지로 보낸다.
     *
     * @return 리다이렉트 경로
     */
    @GetMapping("/grap")
    public RedirectView grapHome() {
        RedirectView redirectView = new RedirectView("https://grap.co.kr/grap/user/content/real-estate");
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return redirectView;
    }
}
