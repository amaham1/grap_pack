package co.grap.pack.qrmanage.superadmin.log.controller;

import co.grap.pack.qrmanage.common.log.model.QrManageActivityLog;
import co.grap.pack.qrmanage.common.log.service.QrManageActivityLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 활동 로그 컨트롤러 (최고관리자용)
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/super/admin/log")
@RequiredArgsConstructor
public class QrManageActivityLogController {

    private final QrManageActivityLogService activityLogService;

    /**
     * 활동 로그 목록 페이지
     */
    @GetMapping
    public String logList(
            @RequestParam(value = "userType", required = false) String userType,
            @RequestParam(value = "activityType", required = false) String activityType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        int size = 50;
        List<QrManageActivityLog> logs = activityLogService.getLogs(userType, activityType, page, size);
        int totalCount = activityLogService.getLogCount(userType, activityType);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        model.addAttribute("title", "활동 로그");
        model.addAttribute("logs", logs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("userType", userType);
        model.addAttribute("activityType", activityType);

        return "qrmanage/super/log/qr-manage-activity-log";
    }
}
