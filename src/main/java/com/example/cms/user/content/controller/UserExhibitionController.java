package com.example.cms.user.content.controller;

import com.example.cms.user.content.model.UserExhibitionRequest;
import com.example.cms.user.content.service.UserExhibitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
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
     * @param tab 탭 구분 (ongoing: 진행 중, upcoming: 다가올 공연, ended: 끝난 공연)
     */
    @GetMapping
    public String exhibitionList(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "tab", required = false) String tab,
                                  @RequestParam(value = "page", required = false) Integer page,
                                  @RequestParam(value = "size", required = false) Integer size,
                                  Model model) {
        Map<String, Object> result = exhibitionService.getExhibitionList(keyword, tab, page, size);

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

    /**
     * 공연/전시 등록 요청 페이지 (폼)
     */
    @GetMapping("/request")
    public String exhibitionRequestForm(Model model) {
        model.addAttribute("content", "user/content/exhibition-request");
        return "user/layout/user-layout";
    }

    /**
     * 공연/전시 등록 요청 처리
     */
    @PostMapping("/request")
    public String createExhibitionRequest(@ModelAttribute UserExhibitionRequest request,
                                          @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                          @RequestParam(value = "thumbnailIndex", required = false) Integer thumbnailIndex,
                                          RedirectAttributes redirectAttributes) {
        try {
            log.info("✅ [CHECK] 공연/전시 등록 요청 컨트롤러 시작");
            Long exhibitionId = exhibitionService.createExhibitionRequest(request, images, thumbnailIndex);
            log.info("✅ [CHECK] 공연/전시 등록 요청 컨트롤러 완료: exhibitionId={}", exhibitionId);

            redirectAttributes.addFlashAttribute("successMessage", "등록에 성공하였습니다. 빠른 검수 요청을 원할시에는 82grap@gmail.com 으로 메일보내주세요");
            return "redirect:/user/content/exhibitions";
        } catch (IOException e) {
            log.error("❌ [ERROR] 공연/전시 등록 요청 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "등록 요청 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/user/content/exhibitions/request";
        }
    }
}
