package co.grap.pack.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 홈 컨트롤러
 */
@Controller
public class HomeController {

    /**
     * 루트 페이지 - grap 서비스로 리다이렉트
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/grap";
    }

    /**
     * grap 서비스 루트 - 축제 목록으로 리다이렉트
     */
    @GetMapping("/grap")
    public String grapHome() {
        return "redirect:/grap/user/content/festivals";
    }
}
