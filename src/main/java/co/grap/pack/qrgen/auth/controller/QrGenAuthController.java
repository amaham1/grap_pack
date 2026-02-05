package co.grap.pack.qrgen.auth.controller;

import co.grap.pack.qrgen.auth.service.QrGenAuthService;
import co.grap.pack.qrgen.seo.QrGenSeoHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * QR Generator 인증 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/qrgen/auth")
@RequiredArgsConstructor
public class QrGenAuthController {

    private final QrGenAuthService authService;

    /**
     * 로그인 페이지
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "로그아웃 되었습니다.");
        }
        QrGenSeoHelper.setQrGenPublicPageSeo(model, "/qrgen/auth/login",
                "로그인",
                "Grap QR 코드 생성기 로그인. 로그인하면 QR 코드 생성 기록을 저장하고 관리할 수 있습니다.");
        return "qrgen/auth/qrgen-login";
    }

    /**
     * 회원가입 페이지
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        QrGenSeoHelper.setQrGenPublicPageSeo(model, "/qrgen/auth/register",
                "무료 회원가입",
                "Grap QR 코드 생성기 무료 회원가입. 가입하면 QR 코드 생성 기록 저장, 재생성 등 편리한 기능을 이용할 수 있습니다.");
        return "qrgen/auth/qrgen-register";
    }

    /**
     * 회원가입 처리
     */
    @PostMapping("/register")
    public String register(@RequestParam("loginId") String loginId,
                           @RequestParam("password") String password,
                           @RequestParam("passwordConfirm") String passwordConfirm,
                           @RequestParam(value = "nickname", required = false) String nickname,
                           RedirectAttributes redirectAttributes) {
        try {
            // 비밀번호 확인
            if (!password.equals(passwordConfirm)) {
                redirectAttributes.addFlashAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
                return "redirect:/qrgen/auth/register";
            }

            // 유효성 검사
            if (loginId.length() < 4 || loginId.length() > 20) {
                redirectAttributes.addFlashAttribute("errorMessage", "아이디는 4~20자여야 합니다.");
                return "redirect:/qrgen/auth/register";
            }

            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("errorMessage", "비밀번호는 6자 이상이어야 합니다.");
                return "redirect:/qrgen/auth/register";
            }

            // 회원가입
            authService.registerQrGenUser(loginId, password, nickname);
            redirectAttributes.addFlashAttribute("successMessage", "회원가입이 완료되었습니다. 로그인해주세요.");

            return "redirect:/qrgen/auth/login";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/qrgen/auth/register";
        }
    }
}
