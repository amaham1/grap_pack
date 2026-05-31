package co.grap.pack.qrmanage.common.notification.controller;

import co.grap.pack.qrmanage.common.notification.model.QrManageNotification;
import co.grap.pack.qrmanage.common.notification.service.QrManageNotificationService;
import co.grap.pack.qrmanage.shopadmin.auth.mapper.QrManageShopAdminMapper;
import co.grap.pack.qrmanage.shopadmin.auth.model.QrManageShopAdmin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 알림 컨트롤러 (상점관리자용)
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/shop/admin/notification")
@RequiredArgsConstructor
public class QrManageNotificationController {

    private final QrManageNotificationService notificationService;
    private final QrManageShopAdminMapper shopAdminMapper;

    /**
     * 알림 목록 페이지
     */
    @GetMapping
    public String notificationList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model,
            Principal principal) {
        QrManageShopAdmin shopAdmin = shopAdminMapper.findByEmail(principal.getName());
        if (shopAdmin == null) {
            return "redirect:/qr-manage/shop/auth/login";
        }

        List<QrManageNotification> notifications = notificationService.getShopAdminNotifications(shopAdmin.getId(), page, 20);
        model.addAttribute("notifications", notifications);
        model.addAttribute("currentPage", page);

        return "qrmanage/shop/notification/qr-manage-notification-list";
    }

    /**
     * 읽지 않은 알림 수 조회 (AJAX)
     */
    @GetMapping("/unread-count")
    @ResponseBody
    public Map<String, Integer> getUnreadCount(Principal principal) {
        Map<String, Integer> result = new HashMap<>();
        QrManageShopAdmin shopAdmin = shopAdminMapper.findByEmail(principal.getName());
        if (shopAdmin != null) {
            result.put("count", notificationService.getUnreadCountForShopAdmin(shopAdmin.getId()));
        } else {
            result.put("count", 0);
        }
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
    public Map<String, Boolean> markAllAsRead(Principal principal) {
        QrManageShopAdmin shopAdmin = shopAdminMapper.findByEmail(principal.getName());
        if (shopAdmin != null) {
            notificationService.markAllAsReadForShopAdmin(shopAdmin.getId());
        }
        Map<String, Boolean> result = new HashMap<>();
        result.put("success", true);
        return result;
    }
}
