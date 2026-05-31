package co.grap.pack.qrmanage.common.log.service;

import co.grap.pack.qrmanage.common.log.mapper.QrManageActivityLogMapper;
import co.grap.pack.qrmanage.common.log.model.QrManageActivityLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * 활동 로그 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrManageActivityLogService {

    private final QrManageActivityLogMapper activityLogMapper;

    /**
     * 활동 로그 기록
     */
    @Transactional
    public void log(String userType, Long userId, String userEmail,
                    String activityType, String targetType, Long targetId, String description) {
        String ipAddress = getClientIp();

        QrManageActivityLog activityLog = QrManageActivityLog.builder()
                .userType(userType)
                .userId(userId)
                .userEmail(userEmail)
                .activityType(activityType)
                .targetType(targetType)
                .targetId(targetId)
                .description(description)
                .ipAddress(ipAddress)
                .build();

        activityLogMapper.insert(activityLog);
        log.debug("✅ [CHECK] 활동 로그 기록: type={}, target={}", activityType, targetType);
    }

    /**
     * 최고관리자 활동 로그 기록
     */
    @Transactional
    public void logSuperAdmin(String email, String activityType, String targetType, Long targetId, String description) {
        log("SUPER_ADMIN", null, email, activityType, targetType, targetId, description);
    }

    /**
     * 상점관리자 활동 로그 기록
     */
    @Transactional
    public void logShopAdmin(Long shopAdminId, String email, String activityType, String targetType, Long targetId, String description) {
        log("SHOP_ADMIN", shopAdminId, email, activityType, targetType, targetId, description);
    }

    /**
     * 전체 로그 조회 (최고관리자용)
     */
    public List<QrManageActivityLog> getLogs(String userType, String activityType, int page, int size) {
        int offset = page * size;
        return activityLogMapper.findAll(userType, activityType, offset, size);
    }

    /**
     * 전체 로그 수
     */
    public int getLogCount(String userType, String activityType) {
        return activityLogMapper.countAll(userType, activityType);
    }

    /**
     * 상점관리자별 로그 조회
     */
    public List<QrManageActivityLog> getLogsByUserId(Long userId, int page, int size) {
        int offset = page * size;
        return activityLogMapper.findByUserId(userId, offset, size);
    }

    /**
     * 오래된 로그 정리 (90일 이상)
     */
    @Transactional
    public void cleanupOldLogs() {
        activityLogMapper.deleteOldLogs();
        log.info("✅ [CHECK] 오래된 활동 로그 정리 완료");
    }

    /**
     * 클라이언트 IP 주소 획득
     */
    private String getClientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("X-Real-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            log.debug("클라이언트 IP 획득 실패: {}", e.getMessage());
        }
        return "unknown";
    }
}
