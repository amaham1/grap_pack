package com.example.cms.admin.external.controller;

import com.example.cms.admin.external.service.AdminWelfareService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

/**
 * 관리자 복지서비스 컨트롤러
 */
@Controller
@RequestMapping("/admin/external/welfare")
@RequiredArgsConstructor
public class AdminWelfareController {

    private final AdminWelfareService welfareService;

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
        model.addAttribute("title", "복지서비스 관리");
        model.addAttribute("content", "admin/external/welfare-list");
        return "admin/layout/admin-layout";
    }

    /**
     * 복지서비스 상세 페이지
     */
    @GetMapping("/{id}")
    public String welfareDetail(@PathVariable("id") Long id, Model model) {
        Map<String, Object> welfare = welfareService.getWelfareDetail(id);

        if (welfare == null) {
            return "redirect:/admin/external/welfare";
        }

        model.addAttribute("welfare", welfare);
        model.addAttribute("title", "복지서비스 상세");
        model.addAttribute("content", "admin/external/welfare-detail");
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
            welfareService.updateIsShow(id, isShow);
            redirectAttributes.addFlashAttribute("message", "노출 여부가 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "노출 여부 변경 실패: " + e.getMessage());
        }
        return "redirect:/admin/external/welfare";
    }

    /**
     * 관리자 메모 업데이트
     */
    @PostMapping("/{id}/memo")
    public String updateMemo(@PathVariable("id") Long id,
                              @RequestParam("adminMemo") String adminMemo,
                              RedirectAttributes redirectAttributes) {
        try {
            welfareService.updateAdminMemo(id, adminMemo);
            redirectAttributes.addFlashAttribute("message", "메모가 저장되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "메모 저장 실패: " + e.getMessage());
        }
        return "redirect:/admin/external/welfare/" + id;
    }

    /**
     * 검수 처리
     */
    @PostMapping("/{id}/confirm")
    public String confirmWelfare(@PathVariable("id") Long id,
                                 @RequestParam("confirmStatus") String confirmStatus,
                                 @RequestParam(value = "confirmMemo", required = false) String confirmMemo,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            String confirmedBy = authentication.getName();
            welfareService.updateConfirmStatus(id, confirmStatus, confirmedBy, confirmMemo);

            String statusMessage = confirmStatus.equals("APPROVED") ? "승인" : "반려";
            redirectAttributes.addFlashAttribute("message", "검수가 " + statusMessage + " 처리되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "검수 처리 실패: " + e.getMessage());
        }
        return "redirect:/admin/external/welfare/" + id;
    }

    /**
     * 복지서비스 삭제
     */
    @PostMapping("/{id}/delete")
    public String deleteWelfare(@PathVariable("id") Long id,
                                  RedirectAttributes redirectAttributes) {
        try {
            welfareService.deleteWelfare(id);
            redirectAttributes.addFlashAttribute("message", "복지서비스가 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "삭제 실패: " + e.getMessage());
        }
        return "redirect:/admin/external/welfare";
    }
}
