package co.grap.pack.admin.dashboard.controller;

import co.grap.pack.admin.dashboard.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 통합 대시보드 컨트롤러다.
 */
@Controller
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    /**
     * 대시보드를 조회한다.
     */
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("title", "통합 대시보드");
        model.addAttribute("dashboardSummary", adminDashboardService.getSummary());
        return "admin/dashboard/admin-dashboard";
    }
}
