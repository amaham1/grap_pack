package co.grap.pack.grap.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 홈 컨트롤러
 */
@Controller
public class CmsHomeController {

    /**
     * 루트 랜딩 페이지 - 서비스 소개 및 링크
     */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Grap - QR 코드 생성 & 콘텐츠 관리 플랫폼");
        model.addAttribute("pageDescription", "무료 QR 코드 생성기, 축제·전시 콘텐츠 관리, QR 메뉴판 서비스를 제공하는 Grap 플랫폼입니다.");
        return "landing";
    }

    /**
     * grap 서비스 루트 - 축제 목록으로 리다이렉트
     */
    @GetMapping("/grap")
    public String grapHome() {
        return "redirect:/grap/user/content/festivals";
    }
}
