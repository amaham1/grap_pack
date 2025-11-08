package com.example.cms.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 홈 컨트롤러
 */
@Controller
public class HomeController {

    /**
     * 루트 페이지 - 사용자 콘텐츠 목록으로 리다이렉트
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/user/content/list";
    }
}
