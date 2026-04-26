package co.grap.pack.common.visitor.service;

import co.grap.pack.common.visitor.mapper.PackVisitorMapper;
import co.grap.pack.common.visitor.model.PackVisitor;
import co.grap.pack.common.visitor.model.PackVisitorAuthScope;
import co.grap.pack.common.visitor.model.PackVisitorClassification;
import co.grap.pack.common.visitor.model.PackVisitorMenuCode;
import co.grap.pack.common.visitor.model.PackVisitorServiceCode;
import co.grap.pack.common.visitor.model.PackVisitorUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PackVisitorServiceTest {

    @Mock
    private PackVisitorMapper packVisitorMapper;

    private PackVisitorService packVisitorService;

    @BeforeEach
    void setUp() {
        packVisitorService = new PackVisitorService(packVisitorMapper);
    }

    @Test
    void recordPackVisitorStoresVisitorUid() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/grap/user/content/list");
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0");
        request.setRemoteAddr("127.0.0.1");
        PackVisitorClassification classification = PackVisitorClassification.builder()
                .serviceCode(PackVisitorServiceCode.GRAP)
                .menuCode(PackVisitorMenuCode.GRAP_CONTENT_LIST)
                .routeKey("/grap/user/content/list")
                .build();

        packVisitorService.recordPackVisitor(
                request,
                "session-1",
                "visitor-uid-1",
                classification,
                PackVisitorAuthScope.ANONYMOUS,
                null
        );

        ArgumentCaptor<PackVisitor> visitorCaptor = ArgumentCaptor.forClass(PackVisitor.class);
        verify(packVisitorMapper).insertPackVisitor(visitorCaptor.capture());
        assertThat(visitorCaptor.getValue().getVisitorUid()).isEqualTo("visitor-uid-1");
        assertThat(visitorCaptor.getValue().getSessionId()).isEqualTo("session-1");
    }

    @Test
    void updatePackVisitorDurationNormalizesHumanSignalValues() {
        PackVisitorUpdateRequest request = PackVisitorUpdateRequest.builder()
                .visitorId(10L)
                .durationSeconds(8)
                .visibleDurationSeconds(12)
                .interactionCount(1200)
                .firstInteractionElapsedSeconds(20)
                .screenResolution("1920x1080")
                .language("ko-KR")
                .build();

        packVisitorService.updatePackVisitorDuration(request);

        verify(packVisitorMapper).updatePackVisitorDuration(
                10L,
                8,
                8,
                999,
                8,
                "1920x1080",
                "ko-KR"
        );
    }

    @Test
    void updatePackVisitorDurationIgnoresFirstInteractionWhenNoInteractionExists() {
        PackVisitorUpdateRequest request = PackVisitorUpdateRequest.builder()
                .visitorId(11L)
                .durationSeconds(5)
                .visibleDurationSeconds(4)
                .interactionCount(0)
                .firstInteractionElapsedSeconds(2)
                .screenResolution(" ")
                .language("")
                .build();

        packVisitorService.updatePackVisitorDuration(request);

        verify(packVisitorMapper).updatePackVisitorDuration(
                11L,
                5,
                4,
                0,
                null,
                null,
                null
        );
    }
}
