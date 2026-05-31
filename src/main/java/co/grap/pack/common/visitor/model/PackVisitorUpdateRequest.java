package co.grap.pack.common.visitor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 방문자 체류시간 업데이트 요청 DTO 다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackVisitorUpdateRequest {

    /** 방문 ID */
    private Long visitorId;

    /** 체류시간(초) */
    private Integer durationSeconds;

    /** 화면이 실제로 보인 시간(초) */
    private Integer visibleDurationSeconds;

    /** 사용자 상호작용 횟수 */
    private Integer interactionCount;

    /** 페이지 진입 후 첫 사용자 상호작용까지 걸린 시간(초) */
    private Integer firstInteractionElapsedSeconds;

    /** 화면 해상도 */
    private String screenResolution;

    /** 브라우저 언어 */
    private String language;
}
