package co.grap.pack.grap.user.content.controller;

import co.grap.pack.grap.seo.CmsPublicSeoService;
import co.grap.pack.grap.user.content.model.CmsUserExhibitionRequest;
import co.grap.pack.grap.user.content.service.CmsUserExhibitionService;
import co.grap.pack.grap.user.content.support.CmsUserContentFallbacks;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
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
 * 사용자 공연/전시 컨트롤러다.
 */
@Slf4j
@Controller
@RequestMapping("/grap/user/content/exhibitions")
@RequiredArgsConstructor
public class CmsUserExhibitionController {

    private final CmsUserExhibitionService exhibitionService;
    private final CmsPublicSeoService cmsPublicSeoService;

    /**
     * 공연/전시 목록 페이지를 보여준다.
     *
     * @param keyword 검색어
     * @param tab 탭 값
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param model 화면 모델
     * @return 레이아웃 템플릿
     */
    @GetMapping
    public String exhibitionList(@RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "tab", required = false) String tab,
                                 @RequestParam(value = "page", required = false) Integer page,
                                 @RequestParam(value = "size", required = false) Integer size,
                                 Model model) {
        Map<String, Object> result;
        try {
            result = exhibitionService.getExhibitionList(keyword, tab, page, size);
        } catch (DataAccessException exception) {
            log.warn("Exhibition tables are not ready in the current CMS database. Showing fallback page.", exception);
            result = CmsUserContentFallbacks.unavailableList(
                    "exhibitionList",
                    keyword,
                    page,
                    size,
                    "공연/전시 데이터는 아직 이 서버에서 준비 중입니다.",
                    Map.of()
            );
        }

        model.addAllAttributes(result);
        cmsPublicSeoService.applyExhibitionListSeo(model, keyword, tab, page);
        model.addAttribute("content", "grap/user/content/cms-exhibition-list");
        return "grap/user/layout/cms-user-layout";
    }

    /**
     * 공연/전시 상세 페이지를 보여준다.
     *
     * @param id 전시 ID
     * @param model 화면 모델
     * @return 레이아웃 템플릿
     */
    @GetMapping("/{id}")
    public String exhibitionDetail(@PathVariable("id") Long id, Model model) {
        Map<String, Object> exhibition = exhibitionService.getExhibitionDetail(id);
        if (exhibition == null) {
            return "redirect:/grap/user/content/exhibitions";
        }

        model.addAttribute("exhibition", exhibition);
        cmsPublicSeoService.applyExhibitionDetailSeo(model, exhibition);
        model.addAttribute("content", "grap/user/content/cms-exhibition-detail");
        return "grap/user/layout/cms-user-layout";
    }

    /**
     * 공연/전시 등록 요청 폼을 보여준다.
     *
     * @param model 화면 모델
     * @return 레이아웃 템플릿
     */
    @GetMapping("/request")
    public String exhibitionRequestForm(Model model) {
        cmsPublicSeoService.applyRequestSeo(
                model,
                "/grap/user/content/exhibitions/request",
                "제주 공연 전시 등록 요청",
                "제주 공연과 전시 정보를 등록 요청하는 사용자 전용 폼입니다."
        );
        model.addAttribute("content", "grap/user/content/cms-exhibition-request");
        return "grap/user/layout/cms-user-layout";
    }

    /**
     * 공연/전시 등록 요청을 처리한다.
     *
     * @param request 요청 데이터
     * @param images 첨부 이미지
     * @param thumbnailIndex 대표 이미지 인덱스
     * @param redirectAttributes 리다이렉트 메시지 모델
     * @return 리다이렉트 경로
     */
    @PostMapping("/request")
    public String createExhibitionRequest(@ModelAttribute CmsUserExhibitionRequest request,
                                          @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                          @RequestParam(value = "thumbnailIndex", required = false) Integer thumbnailIndex,
                                          RedirectAttributes redirectAttributes) {
        try {
            log.info("✅ [CHECK] 공연/전시 등록 요청 컨트롤러 시작");
            Long exhibitionId = exhibitionService.createExhibitionRequest(request, images, thumbnailIndex);
            log.info("✅ [CHECK] 공연/전시 등록 요청 컨트롤러 완료: exhibitionId={}", exhibitionId);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "등록이 접수되었습니다. 빠른 검토가 필요하면 82grap@gmail.com 으로 메일을 보내주세요."
            );
            return "redirect:/grap/user/content/exhibitions";
        } catch (IOException exception) {
            log.error("❌ [ERROR] 공연/전시 등록 요청 실패: {}", exception.getMessage());
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "등록 요청 중 오류가 발생했습니다: " + exception.getMessage()
            );
            return "redirect:/grap/user/content/exhibitions/request";
        }
    }
}
