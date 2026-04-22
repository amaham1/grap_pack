package co.grap.pack.admin.analytics.controller;

import co.grap.pack.common.visitor.model.PackVisitorMenuCode;
import co.grap.pack.common.visitor.model.PackVisitorServiceCode;
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
 * ?듯빀 ?댁쁺 ?ы꽭 ?듦퀎 而⑦듃濡ㅻ윭??
 */
@Controller
@RequestMapping("/admin/analytics")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final QrManageSuperVisitorStatsService qrManageSuperVisitorStatsService;

    @GetMapping("/visitors")
    public String visitorStats(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "serviceCode", required = false) String serviceCode,
            @RequestParam(value = "menuCode", required = false) String menuCode,
            Model model
    ) {
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

        if (selectedMenuCode != null && selectedServiceCode == null) {
            selectedServiceCode = selectedMenuCode.getServiceCode();
        }

        if (selectedMenuCode != null && selectedServiceCode != selectedMenuCode.getServiceCode()) {
            selectedMenuCode = null;
        }

        List<PackVisitorMenuCode> availableMenuCodes = PackVisitorMenuCode.findByServiceCode(selectedServiceCode);

        model.addAttribute("title", "諛⑸Ц???듦퀎");
        model.addAttribute("selectedStartDate", selectedStartDate);
        model.addAttribute("selectedEndDate", selectedEndDate);
        model.addAttribute("selectedServiceCode", selectedServiceCode);
        model.addAttribute("selectedMenuCode", selectedMenuCode);
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
