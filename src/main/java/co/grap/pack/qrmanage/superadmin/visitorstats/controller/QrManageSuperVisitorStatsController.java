package co.grap.pack.qrmanage.superadmin.visitorstats.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 旧 ?덊띁 愿由ъ옄 諛⑸Ц???듦퀎 ?붾㈃??듯빀 ?ы꽭濡??꾪솚?쒕떎.
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/super/admin/visitor-stats")
public class QrManageSuperVisitorStatsController {

    @GetMapping
    public String visitorStatsPage(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "serviceCode", required = false) String serviceCode,
            @RequestParam(value = "menuCode", required = false) String menuCode,
            RedirectAttributes redirectAttributes
    ) {
        if (startDate != null && !startDate.isBlank()) {
            redirectAttributes.addAttribute("startDate", startDate);
        }
        if (endDate != null && !endDate.isBlank()) {
            redirectAttributes.addAttribute("endDate", endDate);
        }
        if (serviceCode != null && !serviceCode.isBlank()) {
            redirectAttributes.addAttribute("serviceCode", serviceCode);
        }
        if (menuCode != null && !menuCode.isBlank()) {
            redirectAttributes.addAttribute("menuCode", menuCode);
        }

        log.info("✅ [CHECK] QR Manage visitor stats redirected to /admin/analytics/visitors");
        return "redirect:/admin/analytics/visitors";
    }
}
