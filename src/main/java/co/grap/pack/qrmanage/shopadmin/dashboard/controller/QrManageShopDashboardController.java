package co.grap.pack.qrmanage.shopadmin.dashboard.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * QR 관리 서비스 상점관리자 대시보드 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/shop/admin")
@RequiredArgsConstructor
public class QrManageShopDashboardController {

    /**
     * 대시보드 페이지
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        log.info("✅ [CHECK] 상점관리자 대시보드 페이지 요청");

        // TODO: 통계 데이터 추가
        model.addAttribute("totalMenus", 0);
        model.addAttribute("totalCategories", 0);
        model.addAttribute("totalQrCodes", 0);
        model.addAttribute("todayScans", 0);
        model.addAttribute("weeklyScans", 0);

        return "qrmanage/shop/dashboard/qr-manage-shop-dashboard";
    }
}
