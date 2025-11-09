package com.example.cms.user.content.controller;

import com.example.cms.user.content.service.UserWelfareService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 사용자 복지서비스 컨트롤러
 */
@Controller
@RequestMapping("/user/content/welfare")
@RequiredArgsConstructor
public class UserWelfareController {

    private final UserWelfareService welfareService;

    /**
     * 복지서비스 목록 페이지
     */
    @GetMapping
    public String welfareList(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "page", required = false) Integer page,
                               @RequestParam(value = "size", required = false) Integer size,
                               Model model) {
        Map<String, Object> result = welfareService.getWelfareList(keyword, page, size);

        model.addAllAttributes(result);
        model.addAttribute("content", "user/content/welfare-list");
        return "user/layout/user-layout";
    }

    /**
     * 복지서비스 상세 페이지
     */
    @GetMapping("/{id}")
    public String welfareDetail(@PathVariable("id") Long id, Model model) {
        Map<String, Object> welfare = welfareService.getWelfareDetail(id);

        if (welfare == null) {
            return "redirect:/user/content/welfare";
        }

        model.addAttribute("welfare", welfare);
        model.addAttribute("content", "user/content/welfare-detail");
        return "user/layout/user-layout";
    }
}
