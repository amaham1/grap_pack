package co.grap.pack.qrmanage.superadmin.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * QR 愿由??쒕퉬??理쒓퀬愿由ъ옄 ?몄쬆 而⑦듃濡ㅻ윭
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/super/auth")
public class QrManageSuperAuthController {

    /**
     * 旧 ?덊띁 愿由ъ옄 濡쒓렇???섏씠吏瑜??듯빀 ?대떎?댁젆?몃줈 ?좏솚?쒕떎.
     */
    @GetMapping("/login")
    public String loginPage() {
        log.info("✅ [CHECK] QR Manage super login path redirected to /admin/login");
        return "redirect:/admin/login";
    }
}
