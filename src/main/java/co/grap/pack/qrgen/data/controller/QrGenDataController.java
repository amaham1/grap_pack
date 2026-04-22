package co.grap.pack.qrgen.data.controller;

import co.grap.pack.qrgen.auth.model.QrGenUser;
import co.grap.pack.qrgen.auth.service.QrGenAuthService;
import co.grap.pack.qrgen.generator.model.QrGenHistory;
import co.grap.pack.qrgen.generator.service.QrGenGeneratorService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * QR Generator 데이터 열람 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/qrgen/data")
@RequiredArgsConstructor
public class QrGenDataController {

    private final QrGenGeneratorService qrGenGeneratorService;
    private final QrGenAuthService qrGenAuthService;
    private final PasswordEncoder passwordEncoder;

    private static final int PAGE_SIZE = 20;
    private static final String SESSION_KEY = "qrGenDataAccess";
    private static final String AUTH_LOGIN_ID = "akapwhd";

    /**
     * 데이터 열람 페이지
     * 방문자 통계는 공통 통계 화면으로 이관되어 히스토리만 제공한다.
     */
    @GetMapping
    public String showQrGenData(@RequestParam(value = "page", defaultValue = "1") int page,
                                HttpSession session,
                                Model model) {
        if (!isAuthenticated(session)) {
            return "qrgen/data/qrgen-data-password";
        }

        List<QrGenHistory> historyList = qrGenGeneratorService.findAllQrGenHistory(page, PAGE_SIZE);
        int totalCount = qrGenGeneratorService.countAllQrGenHistory();
        int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);

        model.addAttribute("historyList", historyList);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "qrgen/data/qrgen-data-viewer";
    }

    /**
     * 비밀번호 검증
     */
    @PostMapping("/verify")
    public String verifyQrGenDataPassword(@RequestParam("password") String password,
                                          HttpSession session,
                                          Model model) {
        QrGenUser user = qrGenAuthService.findQrGenUserByLoginId(AUTH_LOGIN_ID);

        if (user != null && passwordEncoder.matches(password, user.getQrGenUserPassword())) {
            session.setAttribute(SESSION_KEY, true);
            log.info("✅ [CHECK] QRgen 데이터 열람 인증 성공");
            return "redirect:/qrgen/data";
        }

        log.warn("❌ [ERROR] QRgen 데이터 열람 인증 실패");
        model.addAttribute("errorMessage", "비밀번호가 올바르지 않습니다.");
        return "qrgen/data/qrgen-data-password";
    }

    /**
     * 세션 인증 여부 확인
     */
    private boolean isAuthenticated(HttpSession session) {
        Boolean access = (Boolean) session.getAttribute(SESSION_KEY);
        return Boolean.TRUE.equals(access);
    }
}
