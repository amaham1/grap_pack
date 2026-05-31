package co.grap.pack.qrmanage.shopadmin.stats.controller;

import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShop;
import co.grap.pack.qrmanage.shopadmin.shop.service.QrManageShopService;
import co.grap.pack.qrmanage.shopadmin.stats.model.QrManageScanStats;
import co.grap.pack.qrmanage.shopadmin.stats.service.QrManageStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * 상점관리자용 통계 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/shop/admin/stats")
@RequiredArgsConstructor
public class QrManageStatsController {

    private final QrManageStatsService statsService;
    private final QrManageShopService shopService;

    /**
     * 통계 대시보드 페이지
     */
    @GetMapping
    public String statsPage(Model model, Principal principal) {
        QrManageShop shop = shopService.getShopByAdminUsername(principal.getName());

        if (shop == null) {
            model.addAttribute("noShop", true);
            model.addAttribute("error", "상점 정보를 먼저 등록해주세요.");
            return "qrmanage/shop/stats/qr-manage-stats";
        }

        // 상점 상태 정보 전달
        model.addAttribute("shop", shop);

        // 대시보드 통계
        Map<String, Integer> dashboardStats = statsService.getDashboardStats(shop.getId());
        model.addAttribute("stats", dashboardStats);

        // 일별 스캔 통계 (최근 30일)
        List<QrManageScanStats> dailyStats = statsService.getDailyStats(shop.getId());
        model.addAttribute("dailyStats", dailyStats);

        // QR 타입별 통계
        List<QrManageScanStats> typeStats = statsService.getStatsByQrType(shop.getId());
        model.addAttribute("typeStats", typeStats);

        return "qrmanage/shop/stats/qr-manage-stats";
    }

    /**
     * 일별 통계 데이터 조회 (AJAX)
     */
    @GetMapping("/daily")
    @ResponseBody
    public List<QrManageScanStats> getDailyStats(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            Principal principal) {
        Long shopId = shopService.getShopIdByAdminUsername(principal.getName());

        if (shopId == null) {
            return List.of();
        }

        if (startDate != null && endDate != null) {
            return statsService.getDailyStats(shopId, startDate, endDate);
        }
        return statsService.getDailyStats(shopId);
    }
}
