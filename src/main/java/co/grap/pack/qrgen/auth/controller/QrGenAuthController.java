package co.grap.pack.qrgen.auth.controller;

import co.grap.pack.qrgen.auth.service.QrGenAuthService;
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
        return "qrgen/auth/qrgen-login";
    }

    /**
     * 회원가입 페이지
     */
    @GetMapping("/register")
    public String registerPage() {
        return "qrgen/auth/qrgen-register";
    }

    /**
     * 회원가입 처리
     */
    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
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
            if (username.length() < 4 || username.length() > 20) {
                redirectAttributes.addFlashAttribute("errorMessage", "아이디는 4~20자여야 합니다.");
                return "redirect:/qrgen/auth/register";
            }

            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("errorMessage", "비밀번호는 6자 이상이어야 합니다.");
                return "redirect:/qrgen/auth/register";
            }

            // 회원가입
            authService.register(username, password, nickname);
            redirectAttributes.addFlashAttribute("successMessage", "회원가입이 완료되었습니다. 로그인해주세요.");

            return "redirect:/qrgen/auth/login";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/qrgen/auth/register";
        }
    }
}
