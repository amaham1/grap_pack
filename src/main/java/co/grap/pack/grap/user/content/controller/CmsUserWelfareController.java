package co.grap.pack.grap.user.content.controller;

import co.grap.pack.grap.user.content.model.CmsUserWelfareRequest;
import co.grap.pack.grap.user.content.service.CmsUserWelfareService;
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
 * 사용자 복지서비스 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/grap/user/content/welfare")
@RequiredArgsConstructor
public class CmsUserWelfareController {

    private final CmsUserWelfareService welfareService;

    /**
     * 복지서비스 목록 페이지
     */
    @GetMapping
    public String welfareList(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "page", required = false) Integer page,
                               @RequestParam(value = "size", required = false) Integer size,
                               Model model) {
        Map<String, Object> result = welfareService.getWelfareList(keyword, page, size);

        model.addAllAttributes(result);
        model.addAttribute("content", "grap/user/content/cms-welfare-list");
        return "grap/user/layout/cms-user-layout";
    }

    /**
     * 복지서비스 상세 페이지
     */
    @GetMapping("/{id}")
    public String welfareDetail(@PathVariable("id") Long id, Model model) {
        Map<String, Object> welfare = welfareService.getWelfareDetail(id);

        if (welfare == null) {
            return "redirect:/grap/user/content/welfare";
        }

        model.addAttribute("welfare", welfare);
        model.addAttribute("content", "grap/user/content/cms-welfare-detail");
        return "grap/user/layout/cms-user-layout";
    }

    /**
     * 복지서비스 등록 요청 페이지 (폼)
     */
    @GetMapping("/request")
    public String welfareRequestForm(Model model) {
        model.addAttribute("content", "grap/user/content/cms-welfare-request");
        return "grap/user/layout/cms-user-layout";
    }

    /**
     * 복지서비스 등록 요청 처리
     */
    @PostMapping("/request")
    public String createWelfareRequest(@ModelAttribute CmsUserWelfareRequest request,
                                       @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                       @RequestParam(value = "thumbnailIndex", required = false) Integer thumbnailIndex,
                                       RedirectAttributes redirectAttributes) {
        try {
            log.info("✅ [CHECK] 복지서비스 등록 요청 컨트롤러 시작");
            Long welfareServiceId = welfareService.createWelfareRequest(request, images, thumbnailIndex);
            log.info("✅ [CHECK] 복지서비스 등록 요청 컨트롤러 완료: welfareServiceId={}", welfareServiceId);

            redirectAttributes.addFlashAttribute("successMessage", "등록에 성공하였습니다. 빠른 검수 요청을 원할시에는 82grap@gmail.com 으로 메일보내주세요");
            return "redirect:/grap/user/content/welfare";
        } catch (IOException e) {
            log.error("❌ [ERROR] 복지서비스 등록 요청 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "등록 요청 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/grap/user/content/welfare/request";
        }
    }
}
