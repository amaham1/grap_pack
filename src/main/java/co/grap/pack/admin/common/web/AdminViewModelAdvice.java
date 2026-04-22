package co.grap.pack.admin.common.web;

import co.grap.pack.admin.auth.model.AdminSessionPrincipal;
import co.grap.pack.qrmanage.common.notification.service.QrManageNotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 통합 운영 포털 공통 뷰 모델 속성을 주입한다.
 */
@ControllerAdvice(basePackages = "co.grap.pack.admin")
public class AdminViewModelAdvice {

    private final QrManageNotificationService qrManageNotificationService;

    public AdminViewModelAdvice(QrManageNotificationService qrManageNotificationService) {
        this.qrManageNotificationService = qrManageNotificationService;
    }

    /**
     * 운영자 이름을 공통 노출한다.
     */
    @ModelAttribute("adminOperatorName")
    public String adminOperatorName(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AdminSessionPrincipal principal)) {
            return null;
        }
        return principal.getName();
    }

    /**
     * 미확인 알림 개수를 공통 노출한다.
     */
    @ModelAttribute("adminUnreadNotificationCount")
    public int adminUnreadNotificationCount(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AdminSessionPrincipal)) {
            return 0;
        }
        return qrManageNotificationService.getUnreadCountForSuperAdmin();
    }
}
