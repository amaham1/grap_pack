package co.grap.pack.qrgen.visitor.interceptor;

import co.grap.pack.qrgen.visitor.service.QrGenVisitorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * QRgen 페이지 방문 시 방문자 정보를 자동 기록하는 인터셉터
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class QrGenVisitorInterceptor implements HandlerInterceptor {

    private final QrGenVisitorService qrGenVisitorService;

    /** 요청 속성 키: 현재 방문 기록 ID */
    public static final String VISITOR_ID_ATTR = "qrGenVisitorId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            // 정적 리소스 및 API 요청 제외
            String uri = request.getRequestURI();
            if (isExcludedPath(uri)) {
                return true;
            }

            // 세션 ID 획득 (없으면 생성)
            HttpSession session = request.getSession(true);
            String sessionId = session.getId();

            // 로그인 사용자 ID 획득
            Long userId = getAuthenticatedUserId();

            // 방문자 기록
            Long visitorId = qrGenVisitorService.recordQrGenVisitor(request, sessionId, userId);

            // 요청 속성에 방문자 ID 저장 (뷰에서 사용)
            request.setAttribute(VISITOR_ID_ATTR, visitorId);

        } catch (Exception e) {
            // 방문자 추적 실패해도 요청은 계속 처리
            log.error("❌ [ERROR] 방문자 기록 실패: {}", e.getMessage(), e);
        }

        return true;
    }

    /**
     * 제외 경로 확인
     */
    private boolean isExcludedPath(String uri) {
        return uri.startsWith("/qrgen/css/") ||
               uri.startsWith("/qrgen/js/") ||
               uri.startsWith("/qrgen/images/") ||
               uri.startsWith("/qrgen/api/") ||
               uri.equals("/qrgen/generate") ||
               uri.equals("/qrgen/download") ||
               uri.startsWith("/qrgen/visitor/");
    }

    /**
     * 현재 인증된 사용자의 ID 획득
     * @return 로그인 사용자 ID, 비로그인 시 null
     */
    private Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Object principal = auth.getPrincipal();
            if (principal instanceof co.grap.pack.qrgen.auth.model.QrGenUser user) {
                return user.getQrGenUserId();
            }
        }
        return null;
    }
}
