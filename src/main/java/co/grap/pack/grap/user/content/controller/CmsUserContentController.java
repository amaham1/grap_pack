package co.grap.pack.grap.user.content.controller;

import co.grap.pack.grap.admin.content.model.CmsAdminContentType;
import co.grap.pack.grap.admin.content.service.CmsAdminContentTypeService;
import co.grap.pack.grap.seo.CmsPublicSeoService;
import co.grap.pack.grap.user.content.model.CmsUserContent;
import co.grap.pack.grap.user.content.model.CmsUserContentSearchParam;
import co.grap.pack.grap.user.content.service.CmsUserContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * 사용자 일반 콘텐츠 컨트롤러다.
 */
@Controller
@RequestMapping("/grap/user/content")
@RequiredArgsConstructor
public class CmsUserContentController {

    private final CmsUserContentService userContentService;
    private final CmsAdminContentTypeService adminContentTypeService;
    private final CmsPublicSeoService cmsPublicSeoService;

    /**
     * 일반 콘텐츠 목록 페이지를 보여준다.
     *
     * @param searchParam 검색 파라미터
     * @param model 화면 모델
     * @return 레이아웃 템플릿
     */
    @GetMapping("/list")
    public String contentList(CmsUserContentSearchParam searchParam, Model model) {
        Map<String, Object> result = userContentService.getContentList(searchParam);
        List<CmsAdminContentType> contentTypeList = adminContentTypeService.getActiveContentTypeList();

        model.addAttribute("contentList", result.get("contentList"));
        model.addAttribute("currentPage", result.get("currentPage"));
        model.addAttribute("totalPages", result.get("totalPages"));
        model.addAttribute("totalCount", result.get("totalCount"));
        model.addAttribute("size", result.get("size"));
        model.addAttribute("searchParam", searchParam);
        model.addAttribute("contentTypeList", contentTypeList);
        cmsPublicSeoService.applyContentListSeo(model, searchParam);
        model.addAttribute("content", "grap/user/content/cms-content-list");
        return "grap/user/layout/cms-user-layout";
    }

    /**
     * 일반 콘텐츠 상세 페이지를 보여준다.
     *
     * @param contentId 콘텐츠 ID
     * @param model 화면 모델
     * @return 레이아웃 템플릿
     */
    @GetMapping("/detail/{contentId}")
    public String contentDetail(@PathVariable("contentId") Long contentId, Model model) {
        CmsUserContent contentData = userContentService.getContent(contentId);
        if (contentData == null) {
            return "redirect:/grap/user/content/festivals";
        }

        model.addAttribute("contentData", contentData);
        cmsPublicSeoService.applyContentDetailSeo(model, contentData);
        model.addAttribute("content", "grap/user/content/cms-detail");
        return "grap/user/layout/cms-user-layout";
    }
}
