package co.grap.pack.admin.qr.controller;

import co.grap.pack.admin.auth.model.AdminSessionPrincipal;
import co.grap.pack.admin.common.service.AdminActionLogService;
import co.grap.pack.admin.qr.service.AdminQrPortalService;
import co.grap.pack.qrmanage.shopadmin.auth.model.QrManageShopAdmin;
import co.grap.pack.qrmanage.shopadmin.auth.model.QrManageShopAdminStatus;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShopStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 통합 운영 포털 QR 컨트롤러다.
 */
@Controller
@RequestMapping("/admin/qr")
@RequiredArgsConstructor
@Slf4j
public class AdminQrController {

    private final AdminQrPortalService adminQrPortalService;
    private final AdminActionLogService adminActionLogService;

    @GetMapping("/shops")
    public String shopList(
            @RequestParam(value = "status", required = false) QrManageShopStatus status,
            @RequestParam(value = "isVisible", required = false) Boolean isVisible,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model
    ) {
        model.addAttribute("title", "상점 운영");
        model.addAttribute("result", adminQrPortalService.getShopList(status, isVisible, keyword, page, 20));
        model.addAttribute("statuses", adminQrPortalService.getShopStatuses());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedVisible", isVisible);
        model.addAttribute("keyword", keyword);
        return "admin/qr/admin-shop-list";
    }

    @GetMapping("/shops/{shopId}")
    public String shopDetail(@PathVariable("shopId") Long shopId, Model model) {
        model.addAttribute("title", "상점 상세");
        model.addAttribute("detail", adminQrPortalService.getShopDetail(shopId));
        return "admin/qr/admin-shop-detail";
    }

    @PostMapping("/shops/{shopId}/approve")
    public String approveShop(
            @PathVariable("shopId") Long shopId,
            @RequestParam(value = "comment", required = false) String comment,
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        adminQrPortalService.approveShop(shopId, principal.getLoginId(), comment);
        adminActionLogService.log(principal, "QR", "SHOP_APPROVE", "SHOP", shopId, "상점을 승인했습니다.");
        redirectAttributes.addFlashAttribute("message", "상점을 승인했습니다.");
        return "redirect:/admin/qr/shops/" + shopId;
    }

    @PostMapping("/shops/{shopId}/reject")
    public String rejectShop(
            @PathVariable("shopId") Long shopId,
            @RequestParam(value = "comment", required = false) String comment,
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        adminQrPortalService.rejectShop(shopId, principal.getLoginId(), comment);
        adminActionLogService.log(principal, "QR", "SHOP_REJECT", "SHOP", shopId, "상점을 반려했습니다.");
        redirectAttributes.addFlashAttribute("message", "상점을 반려했습니다.");
        return "redirect:/admin/qr/shops/" + shopId;
    }

    @PostMapping("/shops/{shopId}/visibility")
    public String updateShopVisibility(
            @PathVariable("shopId") Long shopId,
            @RequestParam("isVisible") Boolean isVisible,
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        adminQrPortalService.updateShopVisibility(shopId, isVisible);
        adminActionLogService.log(principal, "QR", "SHOP_VISIBILITY", "SHOP", shopId,
                isVisible ? "상점을 노출 처리했습니다." : "상점을 비노출 처리했습니다.");
        redirectAttributes.addFlashAttribute("message", "상점 노출 상태를 변경했습니다.");
        return "redirect:/admin/qr/shops/" + shopId;
    }

    @PostMapping("/shops/{shopId}/memo")
    public String addShopMemo(
            @PathVariable("shopId") Long shopId,
            @RequestParam("content") String content,
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        adminQrPortalService.addShopMemo(shopId, principal.getLoginId(), content);
        adminActionLogService.log(principal, "QR", "SHOP_MEMO_CREATE", "SHOP", shopId, "상점 메모를 등록했습니다.");
        redirectAttributes.addFlashAttribute("message", "상점 메모를 등록했습니다.");
        return "redirect:/admin/qr/shops/" + shopId;
    }

    @PostMapping("/shops/{shopId}/memo/{memoId}/delete")
    public String deleteShopMemo(
            @PathVariable("shopId") Long shopId,
            @PathVariable("memoId") Long memoId,
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        adminQrPortalService.deleteShopMemo(memoId);
        adminActionLogService.log(principal, "QR", "SHOP_MEMO_DELETE", "SHOP_MEMO", memoId, "상점 메모를 삭제했습니다.");
        redirectAttributes.addFlashAttribute("message", "상점 메모를 삭제했습니다.");
        return "redirect:/admin/qr/shops/" + shopId;
    }

    @GetMapping("/shop-admins")
    public String shopAdminList(
            @RequestParam(value = "status", required = false) QrManageShopAdminStatus status,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model
    ) {
        model.addAttribute("title", "점주 계정");
        model.addAttribute("result", adminQrPortalService.getShopAdminList(status, keyword, page, 20));
        model.addAttribute("statuses", adminQrPortalService.getShopAdminStatuses());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("keyword", keyword);
        return "admin/qr/admin-shop-admin-list";
    }

    @GetMapping("/shop-admins/new")
    public String newShopAdmin(Model model) {
        model.addAttribute("title", "점주 계정 생성");
        model.addAttribute("shopAdmin", QrManageShopAdmin.builder().build());
        return "admin/qr/admin-shop-admin-form";
    }

    @PostMapping("/shop-admins/create")
    public String createShopAdmin(
            QrManageShopAdmin shopAdmin,
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        QrManageShopAdmin created = adminQrPortalService.createShopAdmin(shopAdmin);
        adminActionLogService.log(principal, "QR", "SHOP_ADMIN_CREATE", "SHOP_ADMIN", created.getId(), "점주 계정을 생성했습니다.");
        redirectAttributes.addFlashAttribute("message", "점주 계정을 생성했습니다.");
        return "redirect:/admin/qr/shop-admins/" + created.getId();
    }

    @GetMapping("/shop-admins/{shopAdminId}")
    public String shopAdminDetail(@PathVariable("shopAdminId") Long shopAdminId, Model model) {
        model.addAttribute("title", "점주 계정 상세");
        model.addAttribute("detail", adminQrPortalService.getShopAdminDetail(shopAdminId));
        return "admin/qr/admin-shop-admin-detail";
    }

    @PostMapping("/shop-admins/{shopAdminId}/status")
    public String updateShopAdminStatus(
            @PathVariable("shopAdminId") Long shopAdminId,
            @RequestParam("action") String action,
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        adminQrPortalService.updateShopAdminStatus(shopAdminId, action);
        adminActionLogService.log(principal, "QR", "SHOP_ADMIN_STATUS", "SHOP_ADMIN", shopAdminId, "점주 계정 상태를 변경했습니다: " + action);
        redirectAttributes.addFlashAttribute("message", "점주 계정 상태를 변경했습니다.");
        return "redirect:/admin/qr/shop-admins/" + shopAdminId;
    }

    @GetMapping("/qrcodes")
    public String qrCodeList(
            @RequestParam(value = "shopId", required = false) Long shopId,
            @RequestParam(value = "qrType", required = false) String qrType,
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model
    ) {
        model.addAttribute("title", "QR 코드");
        model.addAttribute("result", adminQrPortalService.getQrCodeList(shopId, qrType, isActive, page, 20));
        model.addAttribute("shopId", shopId);
        model.addAttribute("qrType", qrType);
        model.addAttribute("isActive", isActive);
        return "admin/qr/admin-qrcode-list";
    }

    @PostMapping("/qrcodes/{qrCodeId}/toggle")
    public String toggleQrCode(
            @PathVariable("qrCodeId") Long qrCodeId,
            @RequestParam("isActive") Boolean isActive,
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        adminQrPortalService.updateQrCodeActive(qrCodeId, isActive);
        adminActionLogService.log(principal, "QR", "QRCODE_TOGGLE", "QR_CODE", qrCodeId, "QR 코드 활성 상태를 변경했습니다.");
        redirectAttributes.addFlashAttribute("message", "QR 코드 활성 상태를 변경했습니다.");
        return "redirect:/admin/qr/qrcodes";
    }

    @GetMapping("/notifications")
    public String notificationList(@RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        model.addAttribute("title", "알림");
        model.addAttribute("result", adminQrPortalService.getNotificationList(page, 20));
        return "admin/qr/admin-notification-list";
    }

    @PostMapping("/notifications/{notificationId}/read")
    public String readNotification(
            @PathVariable("notificationId") Long notificationId,
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        adminQrPortalService.markNotificationAsRead(notificationId);
        adminActionLogService.log(principal, "QR", "NOTIFICATION_READ", "NOTIFICATION", notificationId, "알림을 읽음 처리했습니다.");
        redirectAttributes.addFlashAttribute("message", "알림을 읽음 처리했습니다.");
        return "redirect:/admin/qr/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String readAllNotifications(
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        adminQrPortalService.markAllNotificationsAsRead();
        adminActionLogService.log(principal, "QR", "NOTIFICATION_READ_ALL", "NOTIFICATION", null, "모든 알림을 읽음 처리했습니다.");
        redirectAttributes.addFlashAttribute("message", "모든 알림을 읽음 처리했습니다.");
        return "redirect:/admin/qr/notifications";
    }

    @PostMapping("/notifications/{notificationId}/delete")
    public String deleteNotification(
            @PathVariable("notificationId") Long notificationId,
            @AuthenticationPrincipal AdminSessionPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        adminQrPortalService.deleteNotification(notificationId);
        adminActionLogService.log(principal, "QR", "NOTIFICATION_DELETE", "NOTIFICATION", notificationId, "알림을 삭제했습니다.");
        redirectAttributes.addFlashAttribute("message", "알림을 삭제했습니다.");
        return "redirect:/admin/qr/notifications";
    }

    @GetMapping("/activity-logs")
    public String activityLogList(
            @RequestParam(value = "userType", required = false) String userType,
            @RequestParam(value = "activityType", required = false) String activityType,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model
    ) {
        model.addAttribute("title", "활동 로그");
        model.addAttribute("result", adminQrPortalService.getActivityLogList(userType, activityType, page, 20));
        model.addAttribute("userType", userType);
        model.addAttribute("activityType", activityType);
        return "admin/qr/admin-activity-log-list";
    }

    @GetMapping("/scan-stats")
    public String scanStats(Model model) {
        model.addAttribute("title", "스캔 통계");
        model.addAttribute("stats", adminQrPortalService.getScanStats());
        return "admin/qr/admin-scan-stats";
    }
}
