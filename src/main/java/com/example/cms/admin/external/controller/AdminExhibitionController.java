package com.example.cms.admin.external.controller;

import com.example.cms.admin.external.service.AdminExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

/**
 * 관리자 공연/전시 컨트롤러
 */
@Controller
@RequestMapping("/admin/external/exhibitions")
@RequiredArgsConstructor
public class AdminExhibitionController {

    private final AdminExhibitionService exhibitionService;

    /**
     * 공연/전시 목록 페이지
     */
    @GetMapping
    public String exhibitionList(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "page", required = false) Integer page,
                                  @RequestParam(value = "size", required = false) Integer size,
                                  Model model) {
        Map<String, Object> result = exhibitionService.getExhibitionList(keyword, page, size);

        model.addAllAttributes(result);
        model.addAttribute("title", "공연/전시 관리");
        model.addAttribute("content", "admin/external/exhibition-list");
        return "admin/layout/admin-layout";
    }

    /**
     * 공연/전시 상세 페이지
     */
    @GetMapping("/{id}")
    public String exhibitionDetail(@PathVariable("id") Long id, Model model) {
        Map<String, Object> exhibition = exhibitionService.getExhibitionDetail(id);

        if (exhibition == null) {
            return "redirect:/admin/external/exhibitions";
        }

        model.addAttribute("exhibition", exhibition);
        model.addAttribute("title", "공연/전시 상세");
        model.addAttribute("content", "admin/external/exhibition-detail");
        return "admin/layout/admin-layout";
    }

    /**
     * 노출 여부 토글
     */
    @PostMapping("/{id}/toggle-show")
    public String toggleShow(@PathVariable("id") Long id,
                              @RequestParam("isShow") Boolean isShow,
                              RedirectAttributes redirectAttributes) {
        try {
            exhibitionService.updateIsShow(id, isShow);
            redirectAttributes.addFlashAttribute("message", "노출 여부가 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "노출 여부 변경 실패: " + e.getMessage());
        }
        return "redirect:/admin/external/exhibitions";
    }

    /**
     * 관리자 메모 업데이트
     */
    @PostMapping("/{id}/memo")
    public String updateMemo(@PathVariable("id") Long id,
                              @RequestParam("adminMemo") String adminMemo,
                              RedirectAttributes redirectAttributes) {
        try {
            exhibitionService.updateAdminMemo(id, adminMemo);
            redirectAttributes.addFlashAttribute("message", "메모가 저장되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "메모 저장 실패: " + e.getMessage());
        }
        return "redirect:/admin/external/exhibitions/" + id;
    }

    /**
     * 검수 처리
     */
    @PostMapping("/{id}/confirm")
    public String confirmExhibition(@PathVariable("id") Long id,
                                    @RequestParam("confirmStatus") String confirmStatus,
                                    @RequestParam(value = "confirmMemo", required = false) String confirmMemo,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            String confirmedBy = authentication.getName();
            exhibitionService.updateConfirmStatus(id, confirmStatus, confirmedBy, confirmMemo);

            String statusMessage = confirmStatus.equals("APPROVED") ? "승인" : "반려";
            redirectAttributes.addFlashAttribute("message", "검수가 " + statusMessage + " 처리되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "검수 처리 실패: " + e.getMessage());
        }
        return "redirect:/admin/external/exhibitions/" + id;
    }

    /**
     * 공연/전시 삭제
     */
    @PostMapping("/{id}/delete")
    public String deleteExhibition(@PathVariable("id") Long id,
                                     RedirectAttributes redirectAttributes) {
        try {
            exhibitionService.deleteExhibition(id);
            redirectAttributes.addFlashAttribute("message", "공연/전시가 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "삭제 실패: " + e.getMessage());
        }
        return "redirect:/admin/external/exhibitions";
    }
}
