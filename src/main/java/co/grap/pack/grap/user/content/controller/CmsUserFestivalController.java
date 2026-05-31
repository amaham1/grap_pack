package co.grap.pack.grap.user.content.controller;

import co.grap.pack.grap.seo.CmsPublicSeoService;
import co.grap.pack.grap.user.content.model.CmsUserFestivalRequest;
import co.grap.pack.grap.user.content.service.CmsUserFestivalService;
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
 * 사용자 축제/행사 컨트롤러다.
 */
@Slf4j
@Controller
@RequestMapping("/grap/user/content/festivals")
@RequiredArgsConstructor
public class CmsUserFestivalController {

    private final CmsUserFestivalService festivalService;
    private final CmsPublicSeoService cmsPublicSeoService;

    /**
     * 축제/행사 목록 페이지를 보여준다.
     *
     * @param keyword 검색어
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param model 화면 모델
     * @return 레이아웃 템플릿
     */
    @GetMapping
    public String festivalList(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "page", required = false) Integer page,
                               @RequestParam(value = "size", required = false) Integer size,
                               Model model) {
        Map<String, Object> result;
        try {
            result = festivalService.getFestivalList(keyword, page, size);
        } catch (DataAccessException exception) {
            log.warn("Festival tables are not ready in the current CMS database. Showing fallback page.", exception);
            result = CmsUserContentFallbacks.unavailableList(
                    "festivalList",
                    keyword,
                    page,
                    size,
                    "축제/행사 데이터는 아직 이 서버에서 준비 중입니다."
            );
        }

        model.addAllAttributes(result);
        cmsPublicSeoService.applyFestivalListSeo(model, keyword, page);
        model.addAttribute("content", "grap/user/content/cms-festival-list");
        return "grap/user/layout/cms-user-layout";
    }

    /**
     * 축제/행사 상세 페이지를 보여준다.
     *
     * @param id 축제 ID
     * @param model 화면 모델
     * @return 레이아웃 템플릿
     */
    @GetMapping("/{id}")
    public String festivalDetail(@PathVariable("id") Long id, Model model) {
        Map<String, Object> festival = festivalService.getFestivalDetail(id);
        if (festival == null) {
            return "redirect:/grap/user/content/festivals";
        }

        model.addAttribute("festival", festival);
        cmsPublicSeoService.applyFestivalDetailSeo(model, festival);
        model.addAttribute("content", "grap/user/content/cms-festival-detail");
        return "grap/user/layout/cms-user-layout";
    }

    /**
     * 축제/행사 등록 요청 폼을 보여준다.
     *
     * @param model 화면 모델
     * @return 레이아웃 템플릿
     */
    @GetMapping("/request")
    public String festivalRequestForm(Model model) {
        cmsPublicSeoService.applyRequestSeo(
                model,
                "/grap/user/content/festivals/request",
                "제주 축제 행사 등록 요청",
                "제주 축제와 행사 정보를 등록 요청하는 사용자 전용 폼입니다."
        );
        model.addAttribute("content", "grap/user/content/cms-festival-request");
        return "grap/user/layout/cms-user-layout";
    }

    /**
     * 축제/행사 등록 요청을 처리한다.
     *
     * @param request 요청 데이터
     * @param images 첨부 이미지
     * @param thumbnailIndex 대표 이미지 인덱스
     * @param redirectAttributes 리다이렉트 메시지 모델
     * @return 리다이렉트 경로
     */
    @PostMapping("/request")
    public String createFestivalRequest(@ModelAttribute CmsUserFestivalRequest request,
                                        @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                        @RequestParam(value = "thumbnailIndex", required = false) Integer thumbnailIndex,
                                        RedirectAttributes redirectAttributes) {
        try {
            log.info("✅ [CHECK] 축제/행사 등록 요청 컨트롤러 시작");
            Long festivalId = festivalService.createFestivalRequest(request, images, thumbnailIndex);
            log.info("✅ [CHECK] 축제/행사 등록 요청 컨트롤러 완료: festivalId={}", festivalId);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "등록이 접수되었습니다. 빠른 검토가 필요하면 82grap@gmail.com 으로 메일을 보내주세요."
            );
            return "redirect:/grap/user/content/festivals";
        } catch (IOException exception) {
            log.error("❌ [ERROR] 축제/행사 등록 요청 실패: {}", exception.getMessage());
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "등록 요청 중 오류가 발생했습니다: " + exception.getMessage()
            );
            return "redirect:/grap/user/content/festivals/request";
        }
    }
}
