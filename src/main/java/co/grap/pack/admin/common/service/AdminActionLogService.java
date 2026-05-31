package co.grap.pack.admin.common.service;

import co.grap.pack.admin.auth.model.AdminSessionPrincipal;
import co.grap.pack.admin.common.mapper.AdminActionLogMapper;
import co.grap.pack.admin.common.model.AdminActionLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 통합 운영 액션 로그 서비스다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminActionLogService {

    private final AdminActionLogMapper adminActionLogMapper;

    /**
     * 운영 액션 로그를 남긴다.
     */
    @Transactional
    public void log(
            AdminSessionPrincipal principal,
            String domainCode,
            String actionCode,
            String targetType,
            Long targetId,
            String summary
    ) {
        if (principal == null) {
            return;
        }

        AdminActionLog adminActionLog = AdminActionLog.builder()
                .operatorId(principal.getId())
                .domainCode(domainCode)
                .actionCode(actionCode)
                .targetType(targetType)
                .targetId(targetId)
                .summary(summary)
                .ipAddress(resolveClientIp())
                .build();

        adminActionLogMapper.insert(adminActionLog);
        log.info("✅ [CHECK] 운영 액션 로그 저장: domain={}, action={}, targetType={}, targetId={}",
                domainCode, actionCode, targetType, targetId);
    }

    private String resolveClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return "unknown";
            }

            HttpServletRequest request = attributes.getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }
            if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            return ip;
        } catch (Exception exception) {
            return "unknown";
        }
    }
}
