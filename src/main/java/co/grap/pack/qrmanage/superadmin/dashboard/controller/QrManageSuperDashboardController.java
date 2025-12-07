package co.grap.pack.qrmanage.superadmin.dashboard.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * QR 관리 서비스 최고관리자 대시보드 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/super/admin")
@RequiredArgsConstructor
public class QrManageSuperDashboardController {

    /**
     * 대시보드 페이지
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        log.info("✅ [CHECK] 최고관리자 대시보드 페이지 요청");

        model.addAttribute("title", "대시보드");

        // TODO: 통계 데이터 추가
        model.addAttribute("totalShopAdmins", 0);
        model.addAttribute("pendingShopAdmins", 0);
        model.addAttribute("totalShops", 0);
        model.addAttribute("pendingShops", 0);
        model.addAttribute("totalQrCodes", 0);
        model.addAttribute("todayScans", 0);

        return "qrmanage/super/dashboard/qr-manage-super-dashboard";
    }
}
