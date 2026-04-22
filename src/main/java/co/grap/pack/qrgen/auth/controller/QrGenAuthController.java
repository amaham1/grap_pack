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
 * QR Generator 인증 컨트롤러다.
 */
@Slf4j
@Controller
@RequestMapping("/qrgen/auth")
@RequiredArgsConstructor
public class QrGenAuthController {

    private final QrGenAuthService authService;

    /**
     * 로그인 페이지를 보여준다.
     *
     * @param error 에러 여부
     * @param logout 로그아웃 여부
     * @param model 화면 모델
     * @return 템플릿 경로
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "로그아웃되었습니다.");
        }
        QrGenSeoHelper.setQrGenLoginSeo(model);
        return "qrgen/auth/qrgen-login";
    }

    /**
     * 회원가입 페이지를 보여준다.
     *
     * @param model 화면 모델
     * @return 템플릿 경로
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        QrGenSeoHelper.setQrGenRegisterSeo(model);
        return "qrgen/auth/qrgen-register";
    }

    /**
     * 회원가입을 처리한다.
     *
     * @param loginId 로그인 아이디
     * @param password 비밀번호
     * @param passwordConfirm 비밀번호 확인
     * @param nickname 닉네임
     * @param redirectAttributes 리다이렉트 메시지 모델
     * @return 리다이렉트 경로
     */
    @PostMapping("/register")
    public String register(@RequestParam("loginId") String loginId,
                           @RequestParam("password") String password,
                           @RequestParam("passwordConfirm") String passwordConfirm,
                           @RequestParam(value = "nickname", required = false) String nickname,
                           RedirectAttributes redirectAttributes) {
        try {
            if (!password.equals(passwordConfirm)) {
                redirectAttributes.addFlashAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
                return "redirect:/qrgen/auth/register";
            }

            if (loginId.length() < 4 || loginId.length() > 20) {
                redirectAttributes.addFlashAttribute("errorMessage", "아이디는 4~20자여야 합니다.");
                return "redirect:/qrgen/auth/register";
            }

            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("errorMessage", "비밀번호는 6자 이상이어야 합니다.");
                return "redirect:/qrgen/auth/register";
            }

            authService.registerQrGenUser(loginId, password, nickname);
            redirectAttributes.addFlashAttribute("successMessage", "회원가입이 완료되었습니다. 로그인해 주세요.");
            return "redirect:/qrgen/auth/login";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/qrgen/auth/register";
        }
    }
}
