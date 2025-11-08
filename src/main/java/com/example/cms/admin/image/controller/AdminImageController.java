package com.example.cms.admin.image.controller;

import com.example.cms.admin.image.model.Image;
import com.example.cms.admin.image.service.AdminImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

/**
 * 관리자 이미지 컨트롤러
 */
@Controller
@RequestMapping("/admin/image")
@RequiredArgsConstructor
public class AdminImageController {

    private final AdminImageService adminImageService;

    /**
     * 이미지 목록 조회 (AJAX용)
     */
    @GetMapping("/list/{contentId}")
    @ResponseBody
    public List<Image> getImageList(@PathVariable("contentId") Long contentId) {
        return adminImageService.getImageList(contentId);
    }

    /**
     * 이미지 업로드
     */
    @PostMapping("/upload")
    public String uploadImage(
            @RequestParam("contentId") Long contentId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "displayOrder", required = false) Integer displayOrder,
            RedirectAttributes redirectAttributes) {

        try {
            adminImageService.uploadImage(contentId, file, displayOrder);
            redirectAttributes.addFlashAttribute("message", "이미지가 업로드되었습니다.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "이미지 업로드 중 오류가 발생했습니다.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/content/form?contentId=" + contentId;
    }

    /**
     * 이미지 삭제
     */
    @PostMapping("/delete/{imageId}")
    public String deleteImage(
            @PathVariable("imageId") Long imageId,
            @RequestParam("contentId") Long contentId,
            RedirectAttributes redirectAttributes) {

        try {
            adminImageService.deleteImage(imageId);
            redirectAttributes.addFlashAttribute("message", "이미지가 삭제되었습니다.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "이미지 삭제 중 오류가 발생했습니다.");
        }

        return "redirect:/admin/content/form?contentId=" + contentId;
    }
}
