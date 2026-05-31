package co.grap.pack.admin.auth.controller;

import co.grap.pack.admin.auth.model.AdminSessionPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 통합 운영 포털 인증 컨트롤러다.
 */
@Controller
public class AdminAuthController {

    /**
     * 로그인 페이지를 노출한다.
     */
    @GetMapping("/admin/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Authentication authentication,
            Model model
    ) {
        if (authentication != null && authentication.getPrincipal() instanceof AdminSessionPrincipal) {
            return "redirect:/admin/dashboard";
        }

        if (error != null) {
            model.addAttribute("errorMessage", "로그인 ID 또는 비밀번호가 올바르지 않습니다.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "로그아웃되었습니다.");
        }

        model.addAttribute("title", "통합 운영자 로그인");
        return "admin/auth/admin-login";
    }
}
