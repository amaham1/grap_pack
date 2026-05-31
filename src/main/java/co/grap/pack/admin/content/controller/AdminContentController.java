package co.grap.pack.admin.content.controller;

import co.grap.pack.admin.auth.model.AdminSessionPrincipal;
import co.grap.pack.admin.common.service.AdminActionLogService;
import co.grap.pack.admin.content.model.AdminDatasetType;
import co.grap.pack.admin.content.service.AdminContentPortalService;
import co.grap.pack.grap.admin.content.model.CmsAdminContent;
import co.grap.pack.grap.admin.content.model.CmsAdminContentSearchParam;
import co.grap.pack.grap.admin.content.model.CmsAdminContentType;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

/**
 * 통합 운영 포털 콘텐츠 컨트롤러다.
 */
@Controller
@RequestMapping("/admin/content")
@RequiredArgsConstructor
@Slf4j
public class AdminContentController {

    private final AdminContentPortalService adminContentPortalService;
    private final AdminActionLogService adminActionLogService;

    /**
     * 수기 콘텐츠 목록을 조회한다.
     */
    @GetMapping("/articles")
    public String articleList(CmsAdminContentSearchParam searchParam, Model model) {
        Map<String, Object> result = adminContentPortalService.getArticleList(searchParam);
        model.addAttribute("title", "수기 콘텐츠");
        model.addAttribute("result", result);
        model.addAttribute("searchParam", searchParam);
        model.addAttribute("contentTypes", adminContentPortalService.getActiveContentTypes());
        return "admin/content/admin-article-list";
    }

    /**
     * 수기 콘텐츠 등록 페이지를 연다.
     */
    @GetMapping("/articles/new")
    public String newArticle(Model model) {
        model.addAttribute("title", "수기 콘텐츠 등록");
        model.addAttribute("contentTypes", adminContentPortalService.getActiveContentTypes());
        model.addAttribute("article", new CmsAdminContent());
        return "admin/content/admin-article-form";
    }

    /**
     * 수기 콘텐츠 수정 페이지를 연다.
     */
    @GetMapping("/articles/{contentId}/edit")
    public String editArticle(@PathVariable("contentId") Long contentId, Model model) {
        model.addAttribute("title", "수기 콘텐츠 수정");
        model.addAttribute("contentTypes", adminContentPortalService.getActiveContentTypes());
        model.addAttribute("article", adminContentPortalService.getArticle(contentId));
        return "admin/content/admin-article-form";
    }

    /**
     * 수기 콘텐츠를 저장한다.
     */
    @PostMapping("/articles/save")
    public String saveArticle(
            CmsAdminContent article,
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            boolean isCreate = article.getContentId() == null;
            adminContentPortalService.saveArticle(article, principal.getName());
            adminActionLogService.log(
                    principal,
                    "CONTENT",
                    isCreate ? "ARTICLE_CREATE" : "ARTICLE_UPDATE",
                    "ARTICLE",
                    article.getContentId(),
                    isCreate ? "수기 콘텐츠를 등록했습니다." : "수기 콘텐츠를 수정했습니다."
            );
            redirectAttributes.addFlashAttribute("message", isCreate ? "수기 콘텐츠를 등록했습니다." : "수기 콘텐츠를 수정했습니다.");
        } catch (Exception exception) {
            log.error("❌ [ERROR] 수기 콘텐츠 저장 실패: {}", exception.getMessage(), exception);
            redirectAttributes.addFlashAttribute("error", "수기 콘텐츠 저장에 실패했습니다: " + exception.getMessage());
        }
        return "redirect:/admin/content/articles";
    }

    /**
     * 수기 콘텐츠를 삭제한다.
     */
    @PostMapping("/articles/{contentId}/delete")
    public String deleteArticle(
            @PathVariable("contentId") Long contentId,
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            adminContentPortalService.deleteArticle(contentId);
            adminActionLogService.log(principal, "CONTENT", "ARTICLE_DELETE", "ARTICLE", contentId, "수기 콘텐츠를 삭제했습니다.");
            redirectAttributes.addFlashAttribute("message", "수기 콘텐츠를 삭제했습니다.");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", "수기 콘텐츠 삭제에 실패했습니다: " + exception.getMessage());
        }
        return "redirect:/admin/content/articles";
    }

    /**
     * 수기 콘텐츠 공개 상태를 변경한다.
     */
    @PostMapping("/articles/{contentId}/publish")
    public String updateArticlePublishStatus(
            @PathVariable("contentId") Long contentId,
            @RequestParam("isPublished") Boolean isPublished,
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        adminContentPortalService.updateArticlePublishStatus(contentId, isPublished);
        adminActionLogService.log(
                principal,
                "CONTENT",
                "ARTICLE_PUBLISH",
                "ARTICLE",
                contentId,
                isPublished ? "수기 콘텐츠를 공개했습니다." : "수기 콘텐츠를 비공개로 전환했습니다."
        );
        redirectAttributes.addFlashAttribute("message", isPublished ? "수기 콘텐츠를 공개했습니다." : "수기 콘텐츠를 비공개로 전환했습니다.");
        return "redirect:/admin/content/articles";
    }

    /**
     * 콘텐츠 타입 목록을 조회한다.
     */
    @GetMapping("/types")
    public String contentTypeList(Model model) {
        model.addAttribute("title", "콘텐츠 타입");
        model.addAttribute("contentTypes", adminContentPortalService.getContentTypes());
        return "admin/content/admin-type-list";
    }

    /**
     * 콘텐츠 타입 등록 페이지를 연다.
     */
    @GetMapping("/types/new")
    public String newContentType(Model model) {
        model.addAttribute("title", "콘텐츠 타입 등록");
        model.addAttribute("contentType", new CmsAdminContentType());
        return "admin/content/admin-type-form";
    }

    /**
     * 콘텐츠 타입 수정 페이지를 연다.
     */
    @GetMapping("/types/{contentTypeId}/edit")
    public String editContentType(@PathVariable("contentTypeId") Long contentTypeId, Model model) {
        model.addAttribute("title", "콘텐츠 타입 수정");
        model.addAttribute("contentType", adminContentPortalService.getContentType(contentTypeId));
        return "admin/content/admin-type-form";
    }

    /**
     * 콘텐츠 타입을 저장한다.
     */
    @PostMapping("/types/save")
    public String saveContentType(
            CmsAdminContentType contentType,
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        boolean isCreate = contentType.getContentTypeId() == null;
        adminContentPortalService.saveContentType(contentType);
        adminActionLogService.log(
                principal,
                "CONTENT",
                isCreate ? "CONTENT_TYPE_CREATE" : "CONTENT_TYPE_UPDATE",
                "CONTENT_TYPE",
                contentType.getContentTypeId(),
                isCreate ? "콘텐츠 타입을 등록했습니다." : "콘텐츠 타입을 수정했습니다."
        );
        redirectAttributes.addFlashAttribute("message", isCreate ? "콘텐츠 타입을 등록했습니다." : "콘텐츠 타입을 수정했습니다.");
        return "redirect:/admin/content/types";
    }

    /**
     * 콘텐츠 타입을 삭제한다.
     */
    @PostMapping("/types/{contentTypeId}/delete")
    public String deleteContentType(
            @PathVariable("contentTypeId") Long contentTypeId,
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        adminContentPortalService.deleteContentType(contentTypeId);
        adminActionLogService.log(principal, "CONTENT", "CONTENT_TYPE_DELETE", "CONTENT_TYPE", contentTypeId, "콘텐츠 타입을 삭제했습니다.");
        redirectAttributes.addFlashAttribute("message", "콘텐츠 타입을 삭제했습니다.");
        return "redirect:/admin/content/types";
    }

    /**
     * 외부 데이터셋 목록을 조회한다.
     */
    @GetMapping("/datasets/{datasetTypePath}")
    public String datasetList(
            @PathVariable("datasetTypePath") String datasetTypePath,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "dealYearMonth", required = false) String dealYearMonth,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", required = false) Integer page,
            Model model
    ) {
        AdminDatasetType datasetType = resolveDatasetType(datasetTypePath);
        Map<String, Object> datasetResult = adminContentPortalService.getDatasetList(datasetType, keyword, dealYearMonth, sort, page);

        model.addAttribute("title", datasetType.getDisplayName());
        model.addAttribute("datasetType", datasetType);
        model.addAttribute("datasetTypes", AdminDatasetType.values());
        model.addAttribute("datasetResult", datasetResult);
        model.addAttribute("paginationBaseUrl", "/admin/content/datasets/" + datasetType.getPathSegment());
        return "admin/content/admin-dataset-list";
    }

    /**
     * 외부 데이터셋 상세를 조회한다.
     */
    @GetMapping("/datasets/{datasetTypePath}/{id}")
    public String datasetDetail(
            @PathVariable("datasetTypePath") String datasetTypePath,
            @PathVariable("id") Long id,
            @RequestParam(value = "year", required = false) Integer year,
            Model model
    ) {
        AdminDatasetType datasetType = resolveDatasetType(datasetTypePath);
        model.addAttribute("title", datasetType.getDisplayName() + " 상세");
        model.addAttribute("datasetType", datasetType);
        model.addAttribute("datasetTypes", AdminDatasetType.values());
        model.addAttribute("detailData", adminContentPortalService.getDatasetDetail(datasetType, id, year));
        return "admin/content/admin-dataset-detail";
    }

    /**
     * 데이터셋 노출 상태를 변경한다.
     */
    @PostMapping("/datasets/{datasetTypePath}/{id}/visibility")
    public String updateDatasetVisibility(
            @PathVariable("datasetTypePath") String datasetTypePath,
            @PathVariable("id") Long id,
            @RequestParam("isVisible") Boolean isVisible,
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        AdminDatasetType datasetType = resolveDatasetType(datasetTypePath);
        adminContentPortalService.updateDatasetVisibility(datasetType, id, isVisible);
        adminActionLogService.log(principal, "CONTENT", "DATASET_VISIBILITY", datasetType.name(), id,
                isVisible ? datasetType.getDisplayName() + " 노출을 활성화했습니다." : datasetType.getDisplayName() + " 노출을 비활성화했습니다.");
        redirectAttributes.addFlashAttribute("message", "노출 상태를 변경했습니다.");
        return "redirect:/admin/content/datasets/" + datasetType.getPathSegment() + "/" + id;
    }

    /**
     * 데이터셋 메모를 저장한다.
     */
    @PostMapping("/datasets/{datasetTypePath}/{id}/memo")
    public String updateDatasetMemo(
            @PathVariable("datasetTypePath") String datasetTypePath,
            @PathVariable("id") Long id,
            @RequestParam("adminMemo") String adminMemo,
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        AdminDatasetType datasetType = resolveDatasetType(datasetTypePath);
        adminContentPortalService.updateDatasetMemo(datasetType, id, adminMemo);
        adminActionLogService.log(principal, "CONTENT", "DATASET_MEMO", datasetType.name(), id, datasetType.getDisplayName() + " 메모를 수정했습니다.");
        redirectAttributes.addFlashAttribute("message", "메모를 저장했습니다.");
        return "redirect:/admin/content/datasets/" + datasetType.getPathSegment() + "/" + id;
    }

    /**
     * 데이터셋 검수 상태를 바꾼다.
     */
    @PostMapping("/datasets/{datasetTypePath}/{id}/confirm")
    public String updateDatasetConfirmStatus(
            @PathVariable("datasetTypePath") String datasetTypePath,
            @PathVariable("id") Long id,
            @RequestParam("confirmStatus") String confirmStatus,
            @RequestParam(value = "confirmMemo", required = false) String confirmMemo,
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        AdminDatasetType datasetType = resolveDatasetType(datasetTypePath);
        adminContentPortalService.updateDatasetConfirmStatus(datasetType, id, confirmStatus, principal.getName(), confirmMemo);
        adminActionLogService.log(principal, "CONTENT", "DATASET_CONFIRM", datasetType.name(), id, datasetType.getDisplayName() + " 검수 상태를 변경했습니다.");
        redirectAttributes.addFlashAttribute("message", "검수 상태를 변경했습니다.");
        return "redirect:/admin/content/datasets/" + datasetType.getPathSegment() + "/" + id;
    }

    /**
     * 데이터셋을 삭제한다.
     */
    @PostMapping("/datasets/{datasetTypePath}/{id}/delete")
    public String deleteDataset(
            @PathVariable("datasetTypePath") String datasetTypePath,
            @PathVariable("id") Long id,
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        AdminDatasetType datasetType = resolveDatasetType(datasetTypePath);
        adminContentPortalService.deleteDataset(datasetType, id);
        adminActionLogService.log(principal, "CONTENT", "DATASET_DELETE", datasetType.name(), id, datasetType.getDisplayName() + " 데이터를 삭제했습니다.");
        redirectAttributes.addFlashAttribute("message", "데이터를 삭제했습니다.");
        return "redirect:/admin/content/datasets/" + datasetType.getPathSegment();
    }

    /**
     * 동기화 작업 페이지를 조회한다.
     */
    @GetMapping("/sync-jobs")
    public String syncJobs(Model model) {
        model.addAttribute("title", "동기화 작업");
        model.addAttribute("syncJobs", adminContentPortalService.getSyncJobSummaries());
        return "admin/content/admin-sync-jobs";
    }

    /**
     * 동기화 작업을 실행한다.
     */
    @PostMapping("/sync-jobs/{jobKey}")
    public String runSyncJob(
            @PathVariable("jobKey") String jobKey,
            HttpSession session,
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            adminContentPortalService.runSyncJob(jobKey, session);
            adminActionLogService.log(principal, "CONTENT", "SYNC_JOB_RUN", "SYNC_JOB", null, "동기화 작업을 실행했습니다: " + jobKey);
            redirectAttributes.addFlashAttribute("message", "동기화 작업을 실행했습니다: " + jobKey);
        } catch (Exception exception) {
            log.error("❌ [ERROR] 동기화 작업 실패: {}", exception.getMessage(), exception);
            redirectAttributes.addFlashAttribute("error", "동기화 작업 실행에 실패했습니다: " + exception.getMessage());
        }
        return "redirect:/admin/content/sync-jobs";
    }

    private AdminDatasetType resolveDatasetType(String datasetTypePath) {
        AdminDatasetType datasetType = AdminDatasetType.fromPath(datasetTypePath);
        if (datasetType == null) {
            throw new IllegalArgumentException("지원하지 않는 데이터셋입니다: " + datasetTypePath);
        }
        return datasetType;
    }
}
