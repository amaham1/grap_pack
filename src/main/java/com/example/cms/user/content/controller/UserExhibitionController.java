package com.example.cms.user.content.controller;

import com.example.cms.user.content.service.UserExhibitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 사용자 공연/전시 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/user/content/exhibitions")
@RequiredArgsConstructor
public class UserExhibitionController {

    private final UserExhibitionService exhibitionService;

    /**
     * 공연/전시 목록 페이지
     */
    @GetMapping
    public String exhibitionList(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "page", required = false) Integer page,
                                  @RequestParam(value = "size", required = false) Integer size,
                                  Model model) {
        Map<String, Object> result = exhibitionService.getExhibitionList(keyword, page, size);

        // 디버깅: 첫 번째 exhibition의 키 확인
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> exhibitionList = (List<Map<String, Object>>) result.get("exhibitionList");
        if (exhibitionList != null && !exhibitionList.isEmpty()) {
            Map<String, Object> firstExhibition = exhibitionList.get(0);
            log.info("=== Exhibition Keys Debug ===");
            log.info("Keys in first exhibition: {}", firstExhibition.keySet());
            log.info("Sample data: {}", firstExhibition);
        }

        model.addAllAttributes(result);
        model.addAttribute("content", "user/content/exhibition-list");
        return "user/layout/user-layout";
    }

    /**
     * 공연/전시 상세 페이지
     */
    @GetMapping("/{id}")
    public String exhibitionDetail(@PathVariable("id") Long id, Model model) {
        Map<String, Object> exhibition = exhibitionService.getExhibitionDetail(id);

        if (exhibition == null) {
            return "redirect:/user/content/exhibitions";
        }

        model.addAttribute("exhibition", exhibition);
        model.addAttribute("content", "user/content/exhibition-detail");
        return "user/layout/user-layout";
    }
}
