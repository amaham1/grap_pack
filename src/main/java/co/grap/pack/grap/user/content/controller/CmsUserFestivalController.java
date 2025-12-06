package co.grap.pack.grap.user.content.controller;

import co.grap.pack.grap.user.content.model.CmsUserFestivalRequest;
import co.grap.pack.grap.user.content.service.CmsUserFestivalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * 사용자 축제/행사 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/grap/user/content/festivals")
@RequiredArgsConstructor
public class CmsUserFestivalController {

    private final CmsUserFestivalService festivalService;

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
        model.addAttribute("content", "grap/user/content/festival-list");
        return "grap/user/layout/user-layout";
    }

    /**
     * 축제/행사 상세 페이지
     */
    @GetMapping("/{id}")
    public String festivalDetail(@PathVariable("id") Long id, Model model) {
        Map<String, Object> festival = festivalService.getFestivalDetail(id);

        if (festival == null) {
            return "redirect:/grap/user/content/festivals";
        }

        model.addAttribute("festival", festival);
        model.addAttribute("content", "grap/user/content/festival-detail");
        return "grap/user/layout/user-layout";
    }

    /**
     * 축제/행사 등록 요청 페이지 (폼)
     */
    @GetMapping("/request")
    public String festivalRequestForm(Model model) {
        model.addAttribute("content", "grap/user/content/festival-request");
        return "grap/user/layout/user-layout";
    }

    /**
     * 축제/행사 등록 요청 처리
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

            redirectAttributes.addFlashAttribute("successMessage", "등록에 성공하였습니다. 빠른 검수 요청을 원할시에는 82grap@gmail.com 으로 메일보내주세요");
            return "redirect:/grap/user/content/festivals";
        } catch (IOException e) {
            log.error("❌ [ERROR] 축제/행사 등록 요청 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "등록 요청 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/grap/user/content/festivals/request";
        }
    }
}
