package co.grap.pack.common.web;

import co.grap.pack.common.visitor.interceptor.PackVisitorInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 전역 뷰 모델 속성을 주입한다.
 */
@ControllerAdvice
public class GlobalViewModelAdvice {

    /**
     * 현재 요청 경로를 템플릿에 노출한다.
     */
    @ModelAttribute("currentPath")
    public String currentPath(HttpServletRequest request) {
        return request != null ? request.getRequestURI() : "";
    }

    /**
     * 공통 방문자 ID를 템플릿에 노출한다.
     */
    @ModelAttribute("packVisitorId")
    public Long packVisitorId(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        Object visitorId = request.getAttribute(PackVisitorInterceptor.VISITOR_ID_ATTR);
        if (visitorId instanceof Long value) {
            return value;
        }

        return null;
    }
}
