package co.grap.pack.common.visitor.service;

import co.grap.pack.common.visitor.mapper.PackVisitorMapper;
import co.grap.pack.common.visitor.model.PackVisitorUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
