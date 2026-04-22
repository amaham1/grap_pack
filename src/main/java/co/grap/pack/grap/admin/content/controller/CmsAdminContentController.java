package co.grap.pack.grap.admin.content.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 旧 Grap 愿由ъ옄 肄섑뀗痢?媛濡?瑜??듯빀 /admin ?ы꽭濡??좏솚?쒕떎.
 */
@Controller
@RequestMapping("/grap/admin")
public class CmsAdminContentController {

    @GetMapping("/content-type/list")
    public String contentTypeList() {
        return "redirect:/admin/content/types";
    }

    @GetMapping("/content-type/form")
    public String contentTypeForm(@RequestParam(value = "contentTypeId", required = false) Long contentTypeId) {
        if (contentTypeId != null) {
            return "redirect:/admin/content/types/" + contentTypeId + "/edit";
        }
        return "redirect:/admin/content/types/new";
    }

    @PostMapping("/content-type/create")
    public String createContentType() {
        return "redirect:/admin/content/types";
    }

    @PostMapping("/content-type/update")
    public String updateContentType() {
        return "redirect:/admin/content/types";
    }

    @PostMapping("/content-type/delete/{contentTypeId}")
    public String deleteContentType(@PathVariable("contentTypeId") Long contentTypeId) {
        return "redirect:/admin/content/types";
    }

    @GetMapping("/content/list")
    public String contentList() {
        return "redirect:/admin/content/articles";
    }

    @GetMapping("/content/form")
    public String contentForm(@RequestParam(value = "contentId", required = false) Long contentId) {
        if (contentId != null) {
            return "redirect:/admin/content/articles/" + contentId + "/edit";
        }
        return "redirect:/admin/content/articles/new";
    }

    @PostMapping("/content/create")
    public String createContent() {
        return "redirect:/admin/content/articles";
    }

    @PostMapping("/content/update")
    public String updateContent() {
        return "redirect:/admin/content/articles";
    }

    @PostMapping("/content/delete/{contentId}")
    public String deleteContent(@PathVariable("contentId") Long contentId) {
        return "redirect:/admin/content/articles";
    }

    @PostMapping("/content/publish/{contentId}")
    public String updatePublishStatus(@PathVariable("contentId") Long contentId) {
        return "redirect:/admin/content/articles";
    }
}
