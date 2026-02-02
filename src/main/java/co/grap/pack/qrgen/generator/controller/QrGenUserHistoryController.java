package co.grap.pack.qrgen.generator.controller;

import co.grap.pack.qrgen.auth.model.QrGenUser;
import co.grap.pack.qrgen.auth.service.QrGenAuthService;
import co.grap.pack.qrgen.generator.model.QrGenContentType;
import co.grap.pack.qrgen.generator.model.QrGenHistory;
import co.grap.pack.qrgen.generator.service.QrGenGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * QR Generator 사용자 히스토리 컨트롤러
 * 로그인 사용자만 접근 가능 (/qrgen/user/**)
 */
@Slf4j
@Controller
@RequestMapping("/qrgen/user")
@RequiredArgsConstructor
public class QrGenUserHistoryController {

    private final QrGenGeneratorService generatorService;
    private final QrGenAuthService authService;

    private static final int PAGE_SIZE = 20;

    /**
     * 히스토리 목록 페이지
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

        return "qrgen/user/qrgen-history-list";
    }

    /**
     * 히스토리 상세 페이지
     */
    @GetMapping("/history/{id}")
    public String historyDetail(@PathVariable("id") Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        QrGenUser user = authService.findQrGenUserByLoginId(userDetails.getUsername());
        QrGenHistory history = generatorService.findQrGenHistoryById(id);

        // 권한 확인
        if (history == null || !history.getQrGenHistoryUserId().equals(user.getQrGenUserId())) {
            return "redirect:/qrgen/user/history";
        }

        model.addAttribute("history", history);
        model.addAttribute("user", user);

        return "qrgen/user/qrgen-history-detail";
    }

    /**
     * 히스토리 삭제
     */
    @PostMapping("/history/{id}/delete")
    public String deleteHistory(@PathVariable("id") Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        try {
            QrGenUser user = authService.findQrGenUserByLoginId(userDetails.getUsername());
            generatorService.deleteQrGenHistory(id, user.getQrGenUserId());
            redirectAttributes.addFlashAttribute("successMessage", "삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            log.error("❌ [ERROR] 히스토리 삭제 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "삭제 중 오류가 발생했습니다.");
        }
        return "redirect:/qrgen/user/history";
    }

    /**
     * 히스토리에서 QR 재생성 (폼에 값 미리 채우기)
     */
    @GetMapping("/history/{id}/regenerate")
    public String regenerateFromHistory(@PathVariable("id") Long id,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        Model model) {
        QrGenUser user = authService.findQrGenUserByLoginId(userDetails.getUsername());
        QrGenHistory history = generatorService.findQrGenHistoryById(id);

        // 권한 확인
        if (history == null || !history.getQrGenHistoryUserId().equals(user.getQrGenUserId())) {
            return "redirect:/qrgen/user/history";
        }

        model.addAttribute("history", history);
        model.addAttribute("contentTypes", QrGenContentType.values());
        model.addAttribute("isAuthenticated", true);

        return "qrgen/qrgen-home";
    }
}
