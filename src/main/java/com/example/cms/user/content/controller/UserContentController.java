package com.example.cms.user.content.controller;

import com.example.cms.admin.content.model.AdminContentType;
import com.example.cms.admin.content.service.AdminContentTypeService;
import com.example.cms.user.content.model.UserContent;
import com.example.cms.user.content.model.UserContentSearchParam;
import com.example.cms.user.content.service.UserContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * 사용자 콘텐츠 컨트롤러
 */
@Controller
@RequestMapping("/user/content")
@RequiredArgsConstructor
public class UserContentController {

    private final UserContentService userContentService;
    private final AdminContentTypeService adminContentTypeService;

    /**
     * 콘텐츠 목록 페이지
     */
    @GetMapping("/list")
    public String contentList(UserContentSearchParam searchParam, Model model) {
        Map<String, Object> result = userContentService.getContentList(searchParam);
        List<AdminContentType> contentTypeList = adminContentTypeService.getActiveContentTypeList();

        model.addAttribute("contentList", result.get("contentList"));
        model.addAttribute("currentPage", result.get("currentPage"));
        model.addAttribute("totalPages", result.get("totalPages"));
        model.addAttribute("totalCount", result.get("totalCount"));
        model.addAttribute("size", result.get("size"));
        model.addAttribute("searchParam", searchParam);
        model.addAttribute("contentTypeList", contentTypeList);
        model.addAttribute("content", "user/content/content-list");

        return "user/layout/user-layout";
    }

    /**
     * 콘텐츠 상세 페이지
     */
    @GetMapping("/detail/{contentId}")
    public String contentDetail(@PathVariable("contentId") Long contentId, Model model) {
        UserContent contentData = userContentService.getContent(contentId);

        if (contentData == null) {
            return "redirect:/user/content/festivals";
        }

        model.addAttribute("contentData", contentData);
        model.addAttribute("content", "user/content/detail");
        return "user/layout/user-layout";
    }
}
