package co.grap.pack.qrmanage.superadmin.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * QR 관리 서비스 최고관리자 인증 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/super/auth")
@RequiredArgsConstructor
public class QrManageSuperAuthController {

    /**
     * 최고관리자 로그인 페이지
     */
    @GetMapping("/login")
    public String loginPage() {
        log.info("✅ [CHECK] 최고관리자 로그인 페이지 요청");
        return "qrmanage/super/auth/qr-manage-super-login";
    }
}
