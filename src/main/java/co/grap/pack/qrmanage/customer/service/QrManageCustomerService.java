package co.grap.pack.qrmanage.customer.service;

import co.grap.pack.qrmanage.customer.mapper.QrManageCustomerMapper;
import co.grap.pack.qrmanage.customer.model.QrManageQrScanLog;
import co.grap.pack.qrmanage.shopadmin.category.model.QrManageCategory;
import co.grap.pack.qrmanage.shopadmin.menu.model.QrManageMenu;
import co.grap.pack.qrmanage.shopadmin.menu.model.QrManageMenuOptionGroup;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageBusinessHours;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShop;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 고객용 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrManageCustomerService {

    private final QrManageCustomerMapper customerMapper;

    /**
     * 상점 정보 조회
     */
    public QrManageShop getShopInfo(Long shopId) {
        return customerMapper.findShopById(shopId);
    }

    /**
     * 영업시간 조회
     */
    public List<QrManageBusinessHours> getBusinessHours(Long shopId) {
        return customerMapper.findBusinessHoursByShopId(shopId);
    }

    /**
     * 카테고리별 메뉴 조회 (공개된 것만)
     */
    public Map<QrManageCategory, List<QrManageMenu>> getMenusByCategory(Long shopId) {
        Map<QrManageCategory, List<QrManageMenu>> result = new LinkedHashMap<>();

        List<QrManageCategory> categories = customerMapper.findVisibleCategoriesByShopId(shopId);
        for (QrManageCategory category : categories) {
            List<QrManageMenu> menus = customerMapper.findVisibleMenusByCategoryId(category.getId());
            if (!menus.isEmpty()) {
                result.put(category, menus);
            }
        }

        return result;
    }

    /**
     * 메뉴 상세 조회 (옵션 포함)
     */
    public QrManageMenu getMenuDetail(Long menuId) {
        QrManageMenu menu = customerMapper.findMenuById(menuId);
        if (menu != null) {
            List<QrManageMenuOptionGroup> optionGroups = customerMapper.findOptionGroupsByMenuId(menuId);
            for (QrManageMenuOptionGroup group : optionGroups) {
                group.setOptions(customerMapper.findOptionsByGroupId(group.getId()));
            }
            menu.setOptionGroups(optionGroups);
        }
        return menu;
    }

    /**
     * QR 스캔 로그 기록
     */
    @Transactional
    public void logQrScan(Long qrCodeId, HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        QrManageQrScanLog scanLog = QrManageQrScanLog.builder()
                .qrCodeId(qrCodeId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        customerMapper.insertScanLog(scanLog);
        log.info("✅ [CHECK] QR 스캔 로그 기록: qrCodeId={}, ip={}", qrCodeId, ipAddress);
    }

    /**
     * 클라이언트 IP 주소 추출
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }
}
