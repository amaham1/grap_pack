package co.grap.pack.admin.qr.service;

import co.grap.pack.admin.qr.mapper.AdminQrQueryMapper;
import co.grap.pack.common.util.PaginationUtil;
import co.grap.pack.qrmanage.common.log.model.QrManageActivityLog;
import co.grap.pack.qrmanage.common.log.service.QrManageActivityLogService;
import co.grap.pack.qrmanage.common.notification.model.QrManageNotification;
import co.grap.pack.qrmanage.common.notification.service.QrManageNotificationService;
import co.grap.pack.qrmanage.shopadmin.auth.model.QrManageShopAdmin;
import co.grap.pack.qrmanage.shopadmin.auth.model.QrManageShopAdminStatus;
import co.grap.pack.qrmanage.shopadmin.menu.service.QrManageMenuService;
import co.grap.pack.qrmanage.shopadmin.qrcode.model.QrManageQrCode;
import co.grap.pack.qrmanage.shopadmin.qrcode.service.QrManageQrCodeService;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShop;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShopStatus;
import co.grap.pack.qrmanage.shopadmin.stats.model.QrManageScanStats;
import co.grap.pack.qrmanage.shopadmin.stats.service.QrManageStatsService;
import co.grap.pack.qrmanage.superadmin.auth.model.QrManageSuperAdmin;
import co.grap.pack.qrmanage.superadmin.auth.service.QrManageSuperAuthService;
import co.grap.pack.qrmanage.superadmin.shop.model.QrManageShopMemo;
import co.grap.pack.qrmanage.superadmin.shop.model.QrManageShopReviewHistory;
import co.grap.pack.qrmanage.superadmin.shop.model.QrManageShopSearchParam;
import co.grap.pack.qrmanage.superadmin.shop.service.QrManageSuperShopService;
import co.grap.pack.qrmanage.superadmin.shopadmin.model.QrManageShopAdminSearchParam;
import co.grap.pack.qrmanage.superadmin.shopadmin.service.QrManageSuperShopAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 통합 운영 포털 QR 서비스다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminQrPortalService {

    private final QrManageSuperShopService qrManageSuperShopService;
    private final QrManageSuperShopAdminService qrManageSuperShopAdminService;
    private final QrManageQrCodeService qrManageQrCodeService;
    private final QrManageNotificationService qrManageNotificationService;
    private final QrManageActivityLogService qrManageActivityLogService;
    private final QrManageStatsService qrManageStatsService;
    private final QrManageMenuService qrManageMenuService;
    private final QrManageSuperAuthService qrManageSuperAuthService;
    private final AdminQrQueryMapper adminQrQueryMapper;

    /**
     * 상점 목록을 조회한다.
     */
    public Map<String, Object> getShopList(QrManageShopStatus status, Boolean isVisible, String keyword, int page, int size) {
        QrManageShopSearchParam param = QrManageShopSearchParam.builder()
                .status(status)
                .isVisible(isVisible)
                .keyword(keyword)
                .page(page)
                .size(size)
                .build();

        List<QrManageShop> shops = qrManageSuperShopService.findAll(param);
        int totalCount = qrManageSuperShopService.countAll(param);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        Map<String, Object> result = new HashMap<>();
        result.put("items", shops);
        result.put("currentPage", page);
        result.put("totalPages", totalPages);
        result.put("totalCount", totalCount);
        result.put("pageInfo", PaginationUtil.calculatePageInfo(page, totalPages));
        return result;
    }

    /**
     * 상점 상세를 조회한다.
     */
    public Map<String, Object> getShopDetail(Long shopId) {
        Map<String, Object> result = new HashMap<>();
        result.put("shop", qrManageSuperShopService.findById(shopId));
        result.put("reviewHistory", qrManageSuperShopService.findReviewHistory(shopId));
        result.put("memos", qrManageSuperShopService.findMemos(shopId));
        result.put("qrCodes", qrManageQrCodeService.getQrCodes(shopId));
        result.put("menus", qrManageMenuService.getMenus(shopId));
        result.put("scanSummary", qrManageStatsService.getDashboardStats(shopId));
        result.put("dailyStats", qrManageStatsService.getDailyStats(shopId));
        return result;
    }

    /**
     * 상점을 승인한다.
     */
    @Transactional
    public void approveShop(Long shopId, String loginId, String comment) {
        qrManageSuperShopService.approve(shopId, resolveLegacySuperAdminId(loginId), comment);
    }

    /**
     * 상점을 반려한다.
     */
    @Transactional
    public void rejectShop(Long shopId, String loginId, String comment) {
        qrManageSuperShopService.reject(shopId, resolveLegacySuperAdminId(loginId), comment);
    }

    /**
     * 상점 노출 여부를 변경한다.
     */
    @Transactional
    public void updateShopVisibility(Long shopId, Boolean isVisible) {
        qrManageSuperShopService.setVisibility(shopId, isVisible);
    }

    /**
     * 상점 메모를 추가한다.
     */
    @Transactional
    public void addShopMemo(Long shopId, String loginId, String content) {
        qrManageSuperShopService.addMemo(shopId, resolveLegacySuperAdminId(loginId), content);
    }

    /**
     * 상점 메모를 삭제한다.
     */
    @Transactional
    public void deleteShopMemo(Long memoId) {
        qrManageSuperShopService.deleteMemo(memoId);
    }

    /**
     * 점주 계정 목록을 조회한다.
     */
    public Map<String, Object> getShopAdminList(QrManageShopAdminStatus status, String keyword, int page, int size) {
        QrManageShopAdminSearchParam param = QrManageShopAdminSearchParam.builder()
                .status(status)
                .keyword(keyword)
                .page(page)
                .size(size)
                .build();

        List<QrManageShopAdmin> shopAdmins = qrManageSuperShopAdminService.findAll(param);
        int totalCount = qrManageSuperShopAdminService.countAll(param);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        Map<String, Object> result = new HashMap<>();
        result.put("items", shopAdmins);
        result.put("currentPage", page);
        result.put("totalPages", totalPages);
        result.put("totalCount", totalCount);
        result.put("pageInfo", PaginationUtil.calculatePageInfo(page, totalPages));
        return result;
    }

    /**
     * 점주 상세를 조회한다.
     */
    public Map<String, Object> getShopAdminDetail(Long shopAdminId) {
        Map<String, Object> result = new HashMap<>();
        result.put("shopAdmin", qrManageSuperShopAdminService.findById(shopAdminId));
        result.put("shops", adminQrQueryMapper.findShopsByShopAdminId(shopAdminId));
        return result;
    }

    /**
     * 점주 계정을 생성한다.
     */
    @Transactional
    public QrManageShopAdmin createShopAdmin(QrManageShopAdmin shopAdmin) {
        return qrManageSuperShopAdminService.create(shopAdmin);
    }

    /**
     * 점주 상태를 변경한다.
     */
    @Transactional
    public void updateShopAdminStatus(Long shopAdminId, String action) {
        switch (action) {
            case "APPROVE" -> qrManageSuperShopAdminService.approve(shopAdminId);
            case "SUSPEND" -> qrManageSuperShopAdminService.suspend(shopAdminId);
            case "ACTIVATE" -> qrManageSuperShopAdminService.activate(shopAdminId);
            case "DEACTIVATE" -> qrManageSuperShopAdminService.deactivate(shopAdminId);
            default -> throw new IllegalArgumentException("지원하지 않는 점주 상태 액션입니다: " + action);
        }
    }

    /**
     * QR 코드 목록을 조회한다.
     */
    public Map<String, Object> getQrCodeList(Long shopId, String qrType, Boolean isActive, int page, int size) {
        List<QrManageQrCode> qrCodes = qrManageQrCodeService.getAllQrCodes(shopId, qrType, isActive, page, size);
        int totalCount = qrManageQrCodeService.countAllQrCodes(shopId, qrType, isActive);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        Map<String, Object> result = new HashMap<>();
        result.put("items", qrCodes);
        result.put("currentPage", page);
        result.put("totalPages", totalPages);
        result.put("totalCount", totalCount);
        result.put("pageInfo", PaginationUtil.calculatePageInfo(page, totalPages));
        return result;
    }

    /**
     * QR 코드 활성 상태를 바꾼다.
     */
    @Transactional
    public void updateQrCodeActive(Long qrCodeId, Boolean isActive) {
        qrManageQrCodeService.updateActive(qrCodeId, isActive);
    }

    /**
     * 알림 목록을 조회한다.
     */
    public Map<String, Object> getNotificationList(int page, int size) {
        int pageIndex = Math.max(page - 1, 0);
        int totalCount = adminQrQueryMapper.countSuperAdminNotifications();
        int totalPages = (int) Math.ceil((double) totalCount / size);

        Map<String, Object> result = new HashMap<>();
        result.put("items", qrManageNotificationService.getSuperAdminNotifications(pageIndex, size));
        result.put("currentPage", page);
        result.put("totalPages", totalPages);
        result.put("totalCount", totalCount);
        result.put("pageInfo", PaginationUtil.calculatePageInfo(page, totalPages));
        return result;
    }

    /**
     * 알림을 읽음 처리한다.
     */
    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        qrManageNotificationService.markAsRead(notificationId);
    }

    /**
     * 모든 알림을 읽음 처리한다.
     */
    @Transactional
    public void markAllNotificationsAsRead() {
        qrManageNotificationService.markAllAsReadForSuperAdmin();
    }

    /**
     * 알림을 삭제한다.
     */
    @Transactional
    public void deleteNotification(Long notificationId) {
        qrManageNotificationService.delete(notificationId);
    }

    /**
     * 활동 로그 목록을 조회한다.
     */
    public Map<String, Object> getActivityLogList(String userType, String activityType, int page, int size) {
        int pageIndex = Math.max(page - 1, 0);
        int totalCount = qrManageActivityLogService.getLogCount(userType, activityType);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        Map<String, Object> result = new HashMap<>();
        result.put("items", qrManageActivityLogService.getLogs(userType, activityType, pageIndex, size));
        result.put("currentPage", page);
        result.put("totalPages", totalPages);
        result.put("totalCount", totalCount);
        result.put("pageInfo", PaginationUtil.calculatePageInfo(page, totalPages));
        return result;
    }

    /**
     * 시스템 스캔 통계를 조회한다.
     */
    public Map<String, Object> getScanStats() {
        Map<String, Object> result = new HashMap<>();
        result.put("summary", qrManageStatsService.getSystemDashboardStats());
        result.put("dailyStats", qrManageStatsService.getSystemDailyStats());
        result.put("shopRanking", qrManageStatsService.getShopScanRanking(10));
        return result;
    }

    /**
     * 상점 상태 목록을 제공한다.
     */
    public QrManageShopStatus[] getShopStatuses() {
        return QrManageShopStatus.values();
    }

    /**
     * 점주 상태 목록을 제공한다.
     */
    public QrManageShopAdminStatus[] getShopAdminStatuses() {
        return QrManageShopAdminStatus.values();
    }

    private Long resolveLegacySuperAdminId(String loginId) {
        QrManageSuperAdmin superAdmin = qrManageSuperAuthService.findByUsername(loginId);
        if (superAdmin == null) {
            throw new IllegalStateException("기존 슈퍼 관리자 계정을 찾을 수 없습니다: " + loginId);
        }
        return superAdmin.getId();
    }
}
