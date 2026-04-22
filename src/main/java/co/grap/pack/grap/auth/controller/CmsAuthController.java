package co.grap.pack.grap.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ?몄쬆 而⑦듃濡ㅻ윭
 */
@Controller
@RequestMapping("/grap/auth")
public class CmsAuthController {

    /**
     * 旧 Grap 愿由ъ옄 濡쒓렇??媛濡?瑜??듯빀 ?댁쁺??濡쒓렇?몄쑝濡?蹂대깂?덈떎.
     */
    @GetMapping("/login")
    public String login() {
        return "redirect:/admin/login";
    }
}
