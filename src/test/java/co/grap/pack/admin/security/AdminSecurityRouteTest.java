package co.grap.pack.admin.security;

import co.grap.pack.admin.auth.controller.AdminAuthController;
import co.grap.pack.admin.auth.service.AdminOperatorService;
import co.grap.pack.admin.config.AdminSecurityConfig;
import co.grap.pack.admin.dashboard.controller.AdminDashboardController;
import co.grap.pack.admin.dashboard.service.AdminDashboardService;
import co.grap.pack.common.visitor.service.PackVisitorRouteClassifier;
import co.grap.pack.common.visitor.service.PackVisitorService;
import co.grap.pack.qrgen.auth.service.QrGenAuthService;
import co.grap.pack.qrmanage.common.notification.service.QrManageNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = {
        AdminAuthController.class,
        AdminDashboardController.class
})
@Import({
        AdminSecurityConfig.class,
        AdminSecurityRouteTest.TestBeans.class
})
class AdminSecurityRouteTest {

    private static final String GRAP_PREFIX = "/grap/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @MockBean
    private AdminOperatorService adminOperatorService;

    @MockBean
    private AdminDashboardService adminDashboardService;

    @MockBean
    private QrManageNotificationService qrManageNotificationService;

    @MockBean
    private PackVisitorService packVisitorService;

    @MockBean
    private PackVisitorRouteClassifier packVisitorRouteClassifier;

    @MockBean
    private QrGenAuthService qrGenAuthService;

    @Test
    void anonymousAdminDashboardRedirectsToAdminLogin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/admin/login"));
    }

    @Test
    void adminLoginIsAccessible() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/auth/admin-login"));
    }

    @Test
    void legacyGrapAdminUrlsAreNotMapped() throws Exception {
        mockMvc.perform(get(GRAP_PREFIX + "auth/login"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get(GRAP_PREFIX + "admin/sync"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get(GRAP_PREFIX + "admin/content/list"))
                .andExpect(status().isNotFound());
    }

    @Test
    void requestMappingsDoNotExposeLegacyGrapAdminSurface() {
        assertThat(handlerMapping.getHandlerMethods().keySet())
                .flatExtracting(this::patternValues)
                .noneMatch(pattern -> pattern.startsWith(GRAP_PREFIX + "admin")
                        || pattern.startsWith(GRAP_PREFIX + "auth"));
    }

    private Set<String> patternValues(RequestMappingInfo requestMappingInfo) {
        if (requestMappingInfo.getPathPatternsCondition() != null) {
            return requestMappingInfo.getPathPatternsCondition().getPatternValues();
        }
        if (requestMappingInfo.getPatternsCondition() != null) {
            return requestMappingInfo.getPatternsCondition().getPatterns();
        }
        return Set.of();
    }

    @TestConfiguration
    static class TestBeans {

        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
}
