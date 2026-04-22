package co.grap.pack.common.visitor.interceptor;

import co.grap.pack.common.visitor.model.PackVisitorAuthScope;
import co.grap.pack.common.visitor.model.PackVisitorClassification;
import co.grap.pack.common.visitor.model.PackVisitorMenuCode;
import co.grap.pack.common.visitor.model.PackVisitorServiceCode;
import co.grap.pack.common.visitor.service.PackVisitorRouteClassifier;
import co.grap.pack.common.visitor.service.PackVisitorService;
import co.grap.pack.qrgen.auth.model.QrGenUser;
import co.grap.pack.qrgen.auth.service.QrGenAuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PackVisitorInterceptorTest {

    @Mock
    private PackVisitorService packVisitorService;

    @Mock
    private PackVisitorRouteClassifier packVisitorRouteClassifier;

    @Mock
    private QrGenAuthService qrGenAuthService;

    private PackVisitorInterceptor interceptor;
    private HandlerMethod handlerMethod;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        interceptor = new PackVisitorInterceptor(packVisitorService, packVisitorRouteClassifier, qrGenAuthService);
        Method handleMethod = DummyController.class.getDeclaredMethod("handle");
        handlerMethod = new HandlerMethod(new DummyController(), handleMethod);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void tracksAnonymousPublicPage() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/grap/user/content/list");
        MockHttpServletResponse response = new MockHttpServletResponse();
        PackVisitorClassification classification = PackVisitorClassification.builder()
                .serviceCode(PackVisitorServiceCode.GRAP)
                .menuCode(PackVisitorMenuCode.GRAP_CONTENT_LIST)
                .routeKey("/grap/user/content/list")
                .build();

        when(packVisitorRouteClassifier.classify(request)).thenReturn(Optional.of(classification));
        when(packVisitorService.recordPackVisitor(any(), anyString(), eq(classification), eq(PackVisitorAuthScope.ANONYMOUS), isNull()))
                .thenReturn(101L);

        boolean result = interceptor.preHandle(request, response, handlerMethod);

        assertThat(result).isTrue();
        assertThat(request.getAttribute(PackVisitorInterceptor.VISITOR_ID_ATTR)).isEqualTo(101L);
        verify(packVisitorService).recordPackVisitor(any(), anyString(), eq(classification), eq(PackVisitorAuthScope.ANONYMOUS), isNull());
        verify(qrGenAuthService, never()).findQrGenUserByLoginId(anyString());
    }

    @Test
    void skipsHtmxRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/grap/user/content/list");
        request.addHeader("HX-Request", "true");

        boolean result = interceptor.preHandle(request, new MockHttpServletResponse(), handlerMethod);

        assertThat(result).isTrue();
        verify(packVisitorRouteClassifier, never()).classify(any(MockHttpServletRequest.class));
        verify(packVisitorService, never()).recordPackVisitor(any(), anyString(), any(), any(), any());
    }

    @Test
    void skipsNonGetRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/grap/user/content/list");

        boolean result = interceptor.preHandle(request, new MockHttpServletResponse(), handlerMethod);

        assertThat(result).isTrue();
        verify(packVisitorRouteClassifier, never()).classify(any(MockHttpServletRequest.class));
        verify(packVisitorService, never()).recordPackVisitor(any(), anyString(), any(), any(), any());
    }

    @Test
    void resolvesQrGenAuthenticatedUser() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/qrgen/");
        MockHttpServletResponse response = new MockHttpServletResponse();
        PackVisitorClassification classification = PackVisitorClassification.builder()
                .serviceCode(PackVisitorServiceCode.QRGEN)
                .menuCode(PackVisitorMenuCode.QRGEN_HOME)
                .routeKey("/qrgen/")
                .build();
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("qr-user", "password", "ROLE_QRGEN_USER");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(packVisitorRouteClassifier.classify(request)).thenReturn(Optional.of(classification));
        when(qrGenAuthService.findQrGenUserByLoginId("qr-user"))
                .thenReturn(QrGenUser.builder().qrGenUserId(7L).qrGenUserLoginId("qr-user").build());
        when(packVisitorService.recordPackVisitor(any(), anyString(), eq(classification), eq(PackVisitorAuthScope.QRGEN_USER), eq(7L)))
                .thenReturn(202L);

        boolean result = interceptor.preHandle(request, response, handlerMethod);

        assertThat(result).isTrue();
        assertThat(request.getAttribute(PackVisitorInterceptor.VISITOR_ID_ATTR)).isEqualTo(202L);
        verify(qrGenAuthService).findQrGenUserByLoginId("qr-user");
        verify(packVisitorService).recordPackVisitor(any(), anyString(), eq(classification), eq(PackVisitorAuthScope.QRGEN_USER), eq(7L));
    }

    @SuppressWarnings("unused")
    private static class DummyController {
        public void handle() {
        }
    }
}
