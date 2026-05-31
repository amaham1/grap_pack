package co.grap.pack.admin.analytics.controller;

import co.grap.pack.common.visitor.model.PackVisitorMenuCode;
import co.grap.pack.common.visitor.model.PackVisitorServiceCode;
import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorIpAccessLogPage;
import co.grap.pack.qrmanage.superadmin.visitorstats.service.QrManageSuperVisitorStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * 관리자 방문자 통계 컨트롤러다.
 */
@Controller
@RequestMapping("/admin/analytics")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final QrManageSuperVisitorStatsService qrManageSuperVisitorStatsService;

    /**
     * 방문자 통계 페이지를 보여준다.
     */
    @GetMapping("/visitors")
    public String visitorStats(@RequestParam(value = "startDate", required = false) String startDate,
                               @RequestParam(value = "endDate", required = false) String endDate,
                               @RequestParam(value = "serviceCode", required = false) String serviceCode,
                               @RequestParam(value = "menuCode", required = false) String menuCode,
                               @RequestParam(value = "logPage", required = false) Integer logPage,
                               @RequestParam(value = "logSize", required = false) Integer logSize,
                               @RequestParam(value = "includeBots", required = false) Boolean includeBots,
                               Model model) {
        LocalDate today = LocalDate.now(KOREA_ZONE_ID);
        LocalDate selectedEndDate = parseDate(endDate, today);
        LocalDate selectedStartDate = parseDate(startDate, selectedEndDate.minusDays(29));

        if (selectedStartDate.isAfter(selectedEndDate)) {
            LocalDate tempDate = selectedStartDate;
            selectedStartDate = selectedEndDate;
            selectedEndDate = tempDate;
        }

        PackVisitorServiceCode selectedServiceCode = PackVisitorServiceCode.fromCode(serviceCode);
        PackVisitorMenuCode selectedMenuCode = PackVisitorMenuCode.fromCode(menuCode);
        boolean shouldIncludeBots = Boolean.TRUE.equals(includeBots);

        if (selectedMenuCode != null && selectedServiceCode == null) {
            selectedServiceCode = selectedMenuCode.getServiceCode();
        }

        if (selectedMenuCode != null && selectedServiceCode != selectedMenuCode.getServiceCode()) {
            selectedMenuCode = null;
        }

        List<PackVisitorMenuCode> availableMenuCodes = PackVisitorMenuCode.findByServiceCode(selectedServiceCode);
        QrManageSuperVisitorIpAccessLogPage ipAccessLogPage = qrManageSuperVisitorStatsService.getIpAccessLogPage(
                selectedStartDate,
                selectedEndDate,
                selectedServiceCode,
                selectedMenuCode,
                logPage,
                logSize,
                shouldIncludeBots
        );

        model.addAttribute("title", "방문자 통계");
        model.addAttribute("selectedStartDate", selectedStartDate);
        model.addAttribute("selectedEndDate", selectedEndDate);
        model.addAttribute("selectedServiceCode", selectedServiceCode);
        model.addAttribute("selectedMenuCode", selectedMenuCode);
        model.addAttribute("selectedLogSize", ipAccessLogPage.getPageSize());
        model.addAttribute("includeBots", shouldIncludeBots);
        model.addAttribute("serviceCodes", PackVisitorServiceCode.values());
        model.addAttribute("availableMenuCodes", availableMenuCodes);
        model.addAttribute("dashboardStats", qrManageSuperVisitorStatsService.getDashboardStats(
                selectedStartDate, selectedEndDate, selectedServiceCode, selectedMenuCode));
        model.addAttribute("dailyStats", qrManageSuperVisitorStatsService.getDailyStats(
                selectedStartDate, selectedEndDate, selectedServiceCode, selectedMenuCode));
        model.addAttribute("menuStats", qrManageSuperVisitorStatsService.getMenuStats(
                selectedStartDate, selectedEndDate, selectedServiceCode, selectedMenuCode));
        model.addAttribute("routeStats", qrManageSuperVisitorStatsService.getRouteStats(
                selectedStartDate, selectedEndDate, selectedServiceCode, selectedMenuCode));
        model.addAttribute("deviceStats", qrManageSuperVisitorStatsService.getDeviceStats(
                selectedStartDate, selectedEndDate, selectedServiceCode, selectedMenuCode));
        model.addAttribute("ipAccessLogPage", ipAccessLogPage);
        model.addAttribute("showRouteBreakdown", selectedMenuCode != null);
        return "admin/analytics/admin-visitor-stats";
    }

    private LocalDate parseDate(String rawDate, LocalDate fallbackDate) {
        if (rawDate == null || rawDate.isBlank()) {
            return fallbackDate;
        }
        try {
            return LocalDate.parse(rawDate);
        } catch (Exception exception) {
            return fallbackDate;
        }
    }
}
