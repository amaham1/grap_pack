package co.grap.pack.qrmanage.superadmin.dashboard.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * QR 愿由?理쒓퀬愿由ъ옄 ??쒕낫???ш린 ?몄엯???듯빀 ?ы꽭濡?諛붽퓭以묐떎.
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/super/admin")
public class QrManageSuperDashboardController {

    @GetMapping("/dashboard")
    public String dashboard() {
        log.info("✅ [CHECK] QR Manage super dashboard redirected to /admin/dashboard");
        return "redirect:/admin/dashboard";
    }
}
