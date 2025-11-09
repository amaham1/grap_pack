package com.example.cms.user.content.controller;

import com.example.cms.user.content.service.UserFestivalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 사용자 축제/행사 컨트롤러
 */
@Controller
@RequestMapping("/user/content/festivals")
@RequiredArgsConstructor
public class UserFestivalController {

    private final UserFestivalService festivalService;

    /**
     * 축제/행사 목록 페이지
     */
    @GetMapping
    public String festivalList(@RequestParam(value = "keyword", required = false) String keyword,
                                @RequestParam(value = "page", required = false) Integer page,
                                @RequestParam(value = "size", required = false) Integer size,
                                Model model) {
        Map<String, Object> result = festivalService.getFestivalList(keyword, page, size);

        model.addAllAttributes(result);
        model.addAttribute("content", "user/content/festival-list");
        return "user/layout/user-layout";
    }

    /**
     * 축제/행사 상세 페이지
     */
    @GetMapping("/{id}")
    public String festivalDetail(@PathVariable("id") Long id, Model model) {
        Map<String, Object> festival = festivalService.getFestivalDetail(id);

        if (festival == null) {
            return "redirect:/user/content/festivals";
        }

        model.addAttribute("festival", festival);
        model.addAttribute("content", "user/content/festival-detail");
        return "user/layout/user-layout";
    }
}
