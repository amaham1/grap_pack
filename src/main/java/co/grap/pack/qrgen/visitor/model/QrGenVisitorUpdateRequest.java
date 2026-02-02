package co.grap.pack.qrgen.visitor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 방문자 체류시간/해상도 업데이트 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrGenVisitorUpdateRequest {

    /** 방문자 ID */
    private Long visitorId;

    /** 체류 시간 (초) */
    private Integer durationSeconds;

    /** 화면 해상도 (예: 1920x1080) */
    private String screenResolution;

    /** 브라우저 언어 (예: ko-KR) */
    private String language;
}
