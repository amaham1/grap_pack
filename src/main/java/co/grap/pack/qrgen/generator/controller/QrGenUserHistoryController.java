package co.grap.pack.qrgen.generator.controller;

import co.grap.pack.qrgen.auth.model.QrGenUser;
import co.grap.pack.qrgen.auth.service.QrGenAuthService;
import co.grap.pack.qrgen.generator.model.QrGenContentType;
import co.grap.pack.qrgen.generator.model.QrGenHistory;
import co.grap.pack.qrgen.generator.service.QrGenGeneratorService;
import co.grap.pack.qrgen.generator.service.QrGenRateLimitService;
import co.grap.pack.qrgen.generator.service.QrGenRateLimitService.QrGenRateLimitCheckResult;
import co.grap.pack.qrgen.seo.QrGenSeoHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * QR Generator 사용자 히스토리 컨트롤러다.
 */
@Slf4j
@Controller
@RequestMapping("/qrgen/user")
@RequiredArgsConstructor
public class QrGenUserHistoryController {

    private static final int PAGE_SIZE = 20;

    private final QrGenGeneratorService generatorService;
    private final QrGenAuthService authService;
    private final QrGenRateLimitService rateLimitService;

    /**
     * 히스토리 목록 페이지를 보여준다.
     *
     * @param userDetails 로그인 사용자
     * @param page 페이지 번호
     * @param model 화면 모델
     * @return 템플릿 경로
     */
    @GetMapping("/history")
    public String historyList(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam(value = "page", defaultValue = "1") int page,
                              Model model) {
        QrGenUser user = authService.findQrGenUserByLoginId(userDetails.getUsername());
        List<QrGenHistory> historyList = generatorService.findQrGenHistoryByUserId(user.getQrGenUserId(), page, PAGE_SIZE);
        int totalCount = generatorService.countQrGenHistoryByUserId(user.getQrGenUserId());
        int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);

        model.addAttribute("historyList", historyList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("user", user);
        QrGenSeoHelper.setQrGenProtectedPageSeo(model, "QR 생성 기록");
        return "qrgen/user/qrgen-history-list";
    }

    /**
     * 히스토리 상세 페이지를 보여준다.
     *
     * @param id 히스토리 ID
     * @param userDetails 로그인 사용자
     * @param model 화면 모델
     * @return 템플릿 경로
     */
    @GetMapping("/history/{id}")
    public String historyDetail(@PathVariable("id") Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        QrGenUser user = authService.findQrGenUserByLoginId(userDetails.getUsername());
        QrGenHistory history = generatorService.findQrGenHistoryById(id);

        if (history == null || !history.getQrGenHistoryUserId().equals(user.getQrGenUserId())) {
            return "redirect:/qrgen/user/history";
        }

        model.addAttribute("history", history);
        model.addAttribute("user", user);
        QrGenSeoHelper.setQrGenProtectedPageSeo(model, "QR 생성 기록 상세");
        return "qrgen/user/qrgen-history-detail";
    }

    /**
     * 히스토리를 삭제한다.
     *
     * @param id 히스토리 ID
     * @param userDetails 로그인 사용자
     * @param redirectAttributes 리다이렉트 메시지 모델
     * @return 리다이렉트 경로
     */
    @PostMapping("/history/{id}/delete")
    public String deleteHistory(@PathVariable("id") Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        try {
            QrGenUser user = authService.findQrGenUserByLoginId(userDetails.getUsername());
            generatorService.deleteQrGenHistory(id, user.getQrGenUserId());
            redirectAttributes.addFlashAttribute("successMessage", "삭제되었습니다.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        } catch (Exception exception) {
            log.error("❌ [ERROR] 히스토리 삭제 실패: {}", exception.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "삭제 중 오류가 발생했습니다.");
        }
        return "redirect:/qrgen/user/history";
    }

    /**
     * 히스토리에서 QR을 다시 생성하는 페이지를 보여준다.
     *
     * @param id 히스토리 ID
     * @param userDetails 로그인 사용자
     * @param model 화면 모델
     * @return 템플릿 경로
     */
    @GetMapping("/history/{id}/regenerate")
    public String regenerateFromHistory(@PathVariable("id") Long id,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        Model model) {
        QrGenUser user = authService.findQrGenUserByLoginId(userDetails.getUsername());
        QrGenHistory history = generatorService.findQrGenHistoryById(id);

        if (history == null || !history.getQrGenHistoryUserId().equals(user.getQrGenUserId())) {
            return "redirect:/qrgen/user/history";
        }

        model.addAttribute("history", history);
        model.addAttribute("contentTypes", QrGenContentType.values());
        model.addAttribute("isAuthenticated", true);

        QrGenRateLimitCheckResult rateLimitInfo = rateLimitService.checkQrGenAuthenticatedRateLimit(user.getQrGenUserId());
        model.addAttribute("qrGenRemaining", rateLimitInfo.remaining());
        model.addAttribute("qrGenDailyLimit", rateLimitInfo.limit());
        QrGenSeoHelper.setQrGenProtectedPageSeo(model, "QR 다시 만들기");
        return "qrgen/qrgen-home";
    }
}
