package co.grap.pack.grap.admin.content.controller;

import co.grap.pack.grap.admin.content.model.CmsAdminContent;
import co.grap.pack.grap.admin.content.model.CmsAdminContentSearchParam;
import co.grap.pack.grap.admin.content.model.CmsAdminContentType;
import co.grap.pack.grap.admin.content.service.CmsAdminContentService;
import co.grap.pack.grap.admin.content.service.CmsAdminContentTypeService;
import co.grap.pack.grap.auth.model.CmsAdmin;
import co.grap.pack.grap.auth.service.CmsAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

/**
 * 관리자 콘텐츠 컨트롤러
 */
@Controller
@RequestMapping("/grap/admin")
@RequiredArgsConstructor
public class CmsAdminContentController {

    private final CmsAdminContentService adminContentService;
    private final CmsAdminContentTypeService adminContentTypeService;
    private final CmsAuthService authService;

    /**
     * 콘텐츠 종류 목록 페이지
     */
    @GetMapping("/content-type/list")
    public String contentTypeList(Model model) {
        List<CmsAdminContentType> contentTypeList = adminContentTypeService.getAllContentTypeList();
        model.addAttribute("contentTypeList", contentTypeList);
        model.addAttribute("title", "콘텐츠 종류 관리");
        model.addAttribute("content", "admin/content/type-list");
        return "admin/layout/admin-layout";
    }

    /**
     * 콘텐츠 종류 등록/수정 페이지
     */
    @GetMapping("/content-type/form")
    public String contentTypeForm(
            @RequestParam(value = "contentTypeId", required = false) Long contentTypeId,
            Model model) {

        if (contentTypeId != null) {
            CmsAdminContentType contentType = adminContentTypeService.getContentType(contentTypeId);
            model.addAttribute("contentType", contentType);
            model.addAttribute("title", "콘텐츠 종류 수정");
        } else {
            model.addAttribute("title", "콘텐츠 종류 등록");
        }

        model.addAttribute("content", "admin/content/type-form");
        return "admin/layout/admin-layout";
    }

    /**
     * 콘텐츠 종류 등록
     */
    @PostMapping("/content-type/create")
    public String createContentType(CmsAdminContentType contentType, RedirectAttributes redirectAttributes) {
        adminContentTypeService.createContentType(contentType);
        redirectAttributes.addFlashAttribute("message", "콘텐츠 종류가 등록되었습니다.");
        return "redirect:/grap/admin/content-type/list";
    }

    /**
     * 콘텐츠 종류 수정
     */
    @PostMapping("/content-type/update")
    public String updateContentType(CmsAdminContentType contentType, RedirectAttributes redirectAttributes) {
        adminContentTypeService.updateContentType(contentType);
        redirectAttributes.addFlashAttribute("message", "콘텐츠 종류가 수정되었습니다.");
        return "redirect:/grap/admin/content-type/list";
    }

    /**
     * 콘텐츠 종류 삭제
     */
    @PostMapping("/content-type/delete/{contentTypeId}")
    public String deleteContentType(
            @PathVariable("contentTypeId") Long contentTypeId,
            RedirectAttributes redirectAttributes) {

        adminContentTypeService.deleteContentType(contentTypeId);
        redirectAttributes.addFlashAttribute("message", "콘텐츠 종류가 삭제되었습니다.");
        return "redirect:/grap/admin/content-type/list";
    }

    /**
     * 콘텐츠 목록 페이지
     */
    @GetMapping("/content/list")
    public String contentList(CmsAdminContentSearchParam searchParam, Model model) {
        Map<String, Object> result = adminContentService.getContentList(searchParam);
        List<CmsAdminContentType> contentTypeList = adminContentTypeService.getActiveContentTypeList();

        model.addAttribute("contentList", result.get("contentList"));
        model.addAttribute("currentPage", result.get("currentPage"));
        model.addAttribute("totalPages", result.get("totalPages"));
        model.addAttribute("totalCount", result.get("totalCount"));
        model.addAttribute("size", result.get("size"));
        model.addAttribute("searchParam", searchParam);
        model.addAttribute("contentTypeList", contentTypeList);
        model.addAttribute("title", "콘텐츠 관리");
        model.addAttribute("content", "admin/content/content-list");

        return "admin/layout/admin-layout";
    }

    /**
     * 콘텐츠 등록/수정 페이지
     */
    @GetMapping("/content/form")
    public String contentForm(
            @RequestParam(value = "contentId", required = false) Long contentId,
            Model model) {

        List<CmsAdminContentType> contentTypeList = adminContentTypeService.getActiveContentTypeList();
        model.addAttribute("contentTypeList", contentTypeList);

        if (contentId != null) {
            CmsAdminContent contentData = adminContentService.getContent(contentId);
            model.addAttribute("contentData", contentData);
            model.addAttribute("title", "콘텐츠 수정");
        } else {
            model.addAttribute("title", "콘텐츠 등록");
        }

        model.addAttribute("content", "admin/content/content-form");
        return "admin/layout/admin-layout";
    }

    /**
     * 콘텐츠 등록
     */
    @PostMapping("/content/create")
    public String createContent(
            CmsAdminContent content,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        // 작성자 정보 설정
        CmsAdmin admin = authService.getAdminByUsername(authentication.getName());
        content.setCreateName(admin.getName());

        adminContentService.createContent(content);
        redirectAttributes.addFlashAttribute("message", "콘텐츠가 등록되었습니다.");
        return "redirect:/grap/admin/content/list";
    }

    /**
     * 콘텐츠 수정
     */
    @PostMapping("/content/update")
    public String updateContent(
            CmsAdminContent content,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        // 수정자 정보 설정
        CmsAdmin admin = authService.getAdminByUsername(authentication.getName());
        content.setUpdateName(admin.getName());

        adminContentService.updateContent(content);
        redirectAttributes.addFlashAttribute("message", "콘텐츠가 수정되었습니다.");
        return "redirect:/grap/admin/content/list";
    }

    /**
     * 콘텐츠 삭제
     */
    @PostMapping("/content/delete/{contentId}")
    public String deleteContent(
            @PathVariable("contentId") Long contentId,
            RedirectAttributes redirectAttributes) {

        try {
            adminContentService.deleteContent(contentId);
            redirectAttributes.addFlashAttribute("message", "콘텐츠가 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "콘텐츠 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/grap/admin/content/list";
    }

    /**
     * 콘텐츠 공개/비공개 설정
     */
    @PostMapping("/content/publish/{contentId}")
    public String updatePublishStatus(
            @PathVariable("contentId") Long contentId,
            @RequestParam("isPublished") Boolean isPublished,
            RedirectAttributes redirectAttributes) {

        adminContentService.updatePublishStatus(contentId, isPublished);
        redirectAttributes.addFlashAttribute("message", "콘텐츠 공개 상태가 변경되었습니다.");
        return "redirect:/grap/admin/content/list";
    }
}
