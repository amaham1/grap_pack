package co.grap.pack.grap.user.content.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자 복지서비스 등록 요청 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CmsUserWelfareRequest {

    /** 서비스명 (필수) */
    private String serviceName;

    /** 전체 지역 해당 여부 */
    private Boolean isAllLocation;

    /** 제주시 해당 여부 */
    private Boolean isJejuLocation;

    /** 서귀포시 해당 여부 */
    private Boolean isSeogwipoLocation;

    /** 지원 대상 (필수) - HTML 허용 */
    private String supportTargetHtml;

    /** 지원 내용 (필수) - HTML 허용 */
    private String supportContentHtml;

    /** 신청 방법 (선택) - HTML 허용 */
    private String applicationInfoHtml;
}
