package com.example.cms.admin.external.controller;

import com.example.cms.admin.external.service.AdminFestivalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

/**
 * 관리자 축제/행사 컨트롤러
 */
@Controller
@RequestMapping("/admin/external/festivals")
@RequiredArgsConstructor
public class AdminFestivalController {

    private final AdminFestivalService festivalService;

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
        model.addAttribute("title", "축제/행사 관리");
        model.addAttribute("content", "admin/external/festival-list");
        return "admin/layout/admin-layout";
    }

    /**
     * 축제/행사 상세 페이지
     */
    @GetMapping("/{id}")
    public String festivalDetail(@PathVariable("id") Long id, Model model) {
        Map<String, Object> festival = festivalService.getFestivalDetail(id);

        if (festival == null) {
            return "redirect:/admin/external/festivals";
        }

        model.addAttribute("festival", festival);
        model.addAttribute("title", "축제/행사 상세");
        model.addAttribute("content", "admin/external/festival-detail");
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
            festivalService.updateIsShow(id, isShow);
            redirectAttributes.addFlashAttribute("message", "노출 여부가 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "노출 여부 변경 실패: " + e.getMessage());
        }
        return "redirect:/admin/external/festivals";
    }

    /**
     * 관리자 메모 업데이트
     */
    @PostMapping("/{id}/memo")
    public String updateMemo(@PathVariable("id") Long id,
                              @RequestParam("adminMemo") String adminMemo,
                              RedirectAttributes redirectAttributes) {
        try {
            festivalService.updateAdminMemo(id, adminMemo);
            redirectAttributes.addFlashAttribute("message", "메모가 저장되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "메모 저장 실패: " + e.getMessage());
        }
        return "redirect:/admin/external/festivals/" + id;
    }

    /**
     * 축제/행사 삭제
     */
    @PostMapping("/{id}/delete")
    public String deleteFestival(@PathVariable("id") Long id,
                                   RedirectAttributes redirectAttributes) {
        try {
            festivalService.deleteFestival(id);
            redirectAttributes.addFlashAttribute("message", "축제/행사가 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "삭제 실패: " + e.getMessage());
        }
        return "redirect:/admin/external/festivals";
    }
}
