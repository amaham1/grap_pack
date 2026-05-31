package co.grap.pack.qrmanage.superadmin.notification.controller;

import co.grap.pack.qrmanage.common.notification.model.QrManageNotification;
import co.grap.pack.qrmanage.common.notification.service.QrManageNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 알림 컨트롤러 (최고관리자용)
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/super/admin/notification")
@RequiredArgsConstructor
public class QrManageSuperNotificationController {

    private final QrManageNotificationService notificationService;

    /**
     * 알림 목록 페이지
     */
    @GetMapping
    public String notificationList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {
        List<QrManageNotification> notifications = notificationService.getSuperAdminNotifications(page, 20);
        model.addAttribute("notifications", notifications);
        model.addAttribute("currentPage", page);

        return "qrmanage/super/notification/qr-manage-super-notification-list";
    }

    /**
     * 읽지 않은 알림 수 조회 (AJAX)
     */
    @GetMapping("/unread-count")
    @ResponseBody
    public Map<String, Integer> getUnreadCount() {
        Map<String, Integer> result = new HashMap<>();
        result.put("count", notificationService.getUnreadCountForSuperAdmin());
        return result;
    }

    /**
     * 알림 읽음 처리
     */
    @PostMapping("/read/{id}")
    @ResponseBody
    public Map<String, Boolean> markAsRead(@PathVariable("id") Long id) {
        notificationService.markAsRead(id);
        Map<String, Boolean> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    /**
     * 전체 알림 읽음 처리
     */
    @PostMapping("/read-all")
    @ResponseBody
    public Map<String, Boolean> markAllAsRead() {
        notificationService.markAllAsReadForSuperAdmin();
        Map<String, Boolean> result = new HashMap<>();
        result.put("success", true);
        return result;
    }
}
