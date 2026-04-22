package co.grap.pack.common.visitor.interceptor;

import co.grap.pack.common.visitor.model.PackVisitorAuthScope;
import co.grap.pack.common.visitor.model.PackVisitorClassification;
import co.grap.pack.common.visitor.service.PackVisitorRouteClassifier;
import co.grap.pack.common.visitor.service.PackVisitorService;
import co.grap.pack.qrgen.auth.model.QrGenUser;
import co.grap.pack.qrgen.auth.service.QrGenAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

/**
 * 공개 페이지 공통 방문자 인터셉터다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PackVisitorInterceptor implements HandlerInterceptor {

    /** 요청 속성 키 */
    public static final String VISITOR_ID_ATTR = "packVisitorId";

    private final PackVisitorService packVisitorService;
    private final PackVisitorRouteClassifier packVisitorRouteClassifier;
    private final QrGenAuthService qrGenAuthService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        try {
            if (!shouldTrack(request)) {
                return true;
            }

            Optional<PackVisitorClassification> classification = packVisitorRouteClassifier.classify(request);
            if (classification.isEmpty()) {
                return true;
            }

            HttpSession session = request.getSession(true);
            VisitorAuthInfo visitorAuthInfo = resolveVisitorAuthInfo(request);
            Long visitorId = packVisitorService.recordPackVisitor(
                    request,
                    session.getId(),
                    classification.get(),
                    visitorAuthInfo.authScope(),
                    visitorAuthInfo.authUserId()
            );

            request.setAttribute(VISITOR_ID_ATTR, visitorId);
        } catch (Exception exception) {
            log.error("❌ [ERROR] 공통 방문자 기록 실패: {}", exception.getMessage(), exception);
        }

        return true;
    }

    private boolean shouldTrack(HttpServletRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return false;
        }

        if ("true".equalsIgnoreCase(request.getHeader("HX-Request"))) {
            return false;
        }

        String uri = request.getRequestURI();
        if (uri == null || uri.isBlank()) {
            return false;
        }

        if (uri.startsWith("/common/")
                || uri.startsWith("/grap/css/")
                || uri.startsWith("/grap/js/")
                || uri.startsWith("/qrgen/css/")
                || uri.startsWith("/qrgen/js/")
                || uri.startsWith("/qrgen/images/")
                || uri.startsWith("/qrmanage/css/")
                || uri.startsWith("/qrmanage/js/")
                || uri.startsWith("/qrmanage/images/")
                || uri.startsWith("/uploads/")
                || uri.startsWith("/images/")
                || uri.equals("/favicon.ico")
                || uri.startsWith("/api/")) {
            return false;
        }

        if (uri.startsWith("/grap/admin/")
                || uri.startsWith("/grap/auth/")
                || uri.startsWith("/qrgen/auth/")
                || uri.startsWith("/qrgen/user/")
                || uri.startsWith("/qrgen/data/")
                || uri.startsWith("/qr-manage/shop/")
                || uri.startsWith("/qr-manage/super/")) {
            return false;
        }

        if (uri.equals("/qrgen/preview")
                || uri.equals("/qrgen/generate")
                || uri.equals("/qrgen/download")
                || uri.equals("/robots.txt")
                || uri.equals("/sitemap.xml")) {
            return false;
        }

        return uri.equals("/")
                || uri.startsWith("/grap/user/")
                || uri.equals("/qrgen")
                || uri.startsWith("/qrgen/")
                || uri.startsWith("/qr-manage/view/");
    }

    private VisitorAuthInfo resolveVisitorAuthInfo(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri == null || !uri.startsWith("/qrgen")) {
            return new VisitorAuthInfo(PackVisitorAuthScope.ANONYMOUS, null);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return new VisitorAuthInfo(PackVisitorAuthScope.ANONYMOUS, null);
        }

        QrGenUser qrGenUser = qrGenAuthService.findQrGenUserByLoginId(authentication.getName());
        if (qrGenUser == null) {
            return new VisitorAuthInfo(PackVisitorAuthScope.ANONYMOUS, null);
        }

        return new VisitorAuthInfo(PackVisitorAuthScope.QRGEN_USER, qrGenUser.getQrGenUserId());
    }

    private record VisitorAuthInfo(PackVisitorAuthScope authScope, Long authUserId) {
    }
}
