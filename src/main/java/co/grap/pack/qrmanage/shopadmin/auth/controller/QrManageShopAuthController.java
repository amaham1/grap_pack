package co.grap.pack.qrmanage.shopadmin.auth.controller;

import co.grap.pack.qrmanage.shopadmin.auth.model.QrManageShopAdmin;
import co.grap.pack.qrmanage.shopadmin.auth.service.QrManagePasswordResetService;
import co.grap.pack.qrmanage.shopadmin.auth.service.QrManageShopAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * QR 관리 서비스 상점관리자 인증 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/shop/auth")
@RequiredArgsConstructor
public class QrManageShopAuthController {

    private final QrManageShopAuthService shopAuthService;
    private final QrManagePasswordResetService passwordResetService;

    /**
     * 상점관리자 로그인 페이지
     */
    @GetMapping("/login")
    public String loginPage() {
        log.info("✅ [CHECK] 상점관리자 로그인 페이지 요청");
        return "qrmanage/shop/auth/qr-manage-shop-login";
    }

    /**
     * 상점관리자 회원가입 페이지
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        log.info("✅ [CHECK] 상점관리자 회원가입 페이지 요청");
        model.addAttribute("shopAdmin", new QrManageShopAdmin());
        return "qrmanage/shop/auth/qr-manage-shop-register";
    }

    /**
     * 상점관리자 회원가입 처리
     */
    @PostMapping("/register")
    public String register(@ModelAttribute QrManageShopAdmin shopAdmin,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        log.info("✅ [CHECK] 상점관리자 회원가입 요청: {}", shopAdmin.getUsername());

        try {
            shopAuthService.register(shopAdmin);
            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 관리자 승인 후 로그인이 가능합니다.");
            return "redirect:/qr-manage/shop/auth/login?registered=true";
        } catch (IllegalArgumentException e) {
            log.warn("❌ [ERROR] 회원가입 실패: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("shopAdmin", shopAdmin);
            return "qrmanage/shop/auth/qr-manage-shop-register";
        }
    }

    /**
     * 비밀번호 찾기 페이지
     */
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        log.info("✅ [CHECK] 비밀번호 찾기 페이지 요청");
        return "qrmanage/shop/auth/qr-manage-shop-forgot-password";
    }

    /**
     * 비밀번호 찾기 처리
     */
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam("email") String email,
                                 RedirectAttributes redirectAttributes) {
        log.info("✅ [CHECK] 비밀번호 찾기 요청: {}", email);

        String token = passwordResetService.createResetToken(email);

        // 보안상 토큰 생성 여부와 관계없이 동일한 메시지 표시
        redirectAttributes.addFlashAttribute("message",
            "입력하신 이메일로 비밀번호 재설정 링크가 발송되었습니다. 이메일을 확인해주세요.");

        // 실제 운영 환경에서는 여기서 이메일 발송
        if (token != null) {
            log.info("✅ [CHECK] 비밀번호 재설정 토큰 생성됨 (개발용): /qr-manage/shop/auth/reset-password?token={}", token);
        }

        return "redirect:/qr-manage/shop/auth/forgot-password";
    }

    /**
     * 비밀번호 재설정 페이지
     */
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam("token") String token, Model model) {
        log.info("✅ [CHECK] 비밀번호 재설정 페이지 요청");

        if (!passwordResetService.validateToken(token)) {
            model.addAttribute("error", "유효하지 않거나 만료된 링크입니다.");
            return "qrmanage/shop/auth/qr-manage-shop-forgot-password";
        }

        model.addAttribute("token", token);
        return "qrmanage/shop/auth/qr-manage-shop-reset-password";
    }

    /**
     * 비밀번호 재설정 처리
     */
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token,
                               @RequestParam("password") String password,
                               @RequestParam("passwordConfirm") String passwordConfirm,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        log.info("✅ [CHECK] 비밀번호 재설정 처리 요청");

        // 비밀번호 확인
        if (!password.equals(passwordConfirm)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("token", token);
            return "qrmanage/shop/auth/qr-manage-shop-reset-password";
        }

        // 비밀번호 길이 검증
        if (password.length() < 8) {
            model.addAttribute("error", "비밀번호는 8자 이상이어야 합니다.");
            model.addAttribute("token", token);
            return "qrmanage/shop/auth/qr-manage-shop-reset-password";
        }

        boolean success = passwordResetService.resetPassword(token, password);
        if (!success) {
            model.addAttribute("error", "비밀번호 재설정에 실패했습니다. 다시 시도해주세요.");
            model.addAttribute("token", token);
            return "qrmanage/shop/auth/qr-manage-shop-reset-password";
        }

        redirectAttributes.addFlashAttribute("message", "비밀번호가 성공적으로 변경되었습니다. 새 비밀번호로 로그인해주세요.");
        return "redirect:/qr-manage/shop/auth/login";
    }
}
