package co.grap.pack.qrmanage.superadmin.visitorstats.controller;

import co.grap.pack.common.visitor.model.PackVisitorMenuCode;
import co.grap.pack.common.visitor.model.PackVisitorServiceCode;
import co.grap.pack.qrmanage.superadmin.visitorstats.service.QrManageSuperVisitorStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * 슈퍼 관리자 방문자 통계 컨트롤러다.
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/super/admin/visitor-stats")
@RequiredArgsConstructor
public class QrManageSuperVisitorStatsController {

    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final QrManageSuperVisitorStatsService visitorStatsService;

    /**
     * 방문자 통계 화면을 조회한다.
     */
    @GetMapping
    public String visitorStatsPage(
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

        model.addAttribute("title", "방문자 통계");
        model.addAttribute("selectedStartDate", selectedStartDate);
        model.addAttribute("selectedEndDate", selectedEndDate);
        model.addAttribute("selectedServiceCode", selectedServiceCode);
        model.addAttribute("selectedMenuCode", selectedMenuCode);
        model.addAttribute("serviceCodes", PackVisitorServiceCode.values());
        model.addAttribute("availableMenuCodes", availableMenuCodes);
        model.addAttribute("dashboardStats", visitorStatsService.getDashboardStats(
                selectedStartDate,
                selectedEndDate,
                selectedServiceCode,
                selectedMenuCode
        ));
        model.addAttribute("dailyStats", visitorStatsService.getDailyStats(
                selectedStartDate,
                selectedEndDate,
                selectedServiceCode,
                selectedMenuCode
        ));
        model.addAttribute("menuStats", visitorStatsService.getMenuStats(
                selectedStartDate,
                selectedEndDate,
                selectedServiceCode,
                selectedMenuCode
        ));
        model.addAttribute("routeStats", visitorStatsService.getRouteStats(
                selectedStartDate,
                selectedEndDate,
                selectedServiceCode,
                selectedMenuCode
        ));
        model.addAttribute("deviceStats", visitorStatsService.getDeviceStats(
                selectedStartDate,
                selectedEndDate,
                selectedServiceCode,
                selectedMenuCode
        ));
        model.addAttribute("showRouteBreakdown", selectedMenuCode != null);

        log.info(
                "✅ [CHECK] 슈퍼 관리자 방문자 통계 화면 조회: startDate={}, endDate={}, serviceCode={}, menuCode={}",
                selectedStartDate,
                selectedEndDate,
                selectedServiceCode,
                selectedMenuCode
        );

        return "qrmanage/super/visitorstats/qr-manage-super-visitor-stats";
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
