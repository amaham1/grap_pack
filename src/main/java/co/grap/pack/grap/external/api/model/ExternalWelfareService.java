package co.grap.pack.grap.external.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 복지 서비스 정보 Entity
 * 외부 API: http://www.jeju.go.kr/rest/JejuWelfareServiceInfo/getJejuWelfareServiceInfoList
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalWelfareService {
    /**
     * 내부 ID
     */
    private Long id;

    /**
     * 외부 API 원본 ID (seq)
     */
    private String originalApiId;

    /**
     * 복지 서비스명
     */
    private String serviceName;

    /**
     * 전체 지역 해당 여부
     */
    private Boolean isAllLocation;

    /**
     * 제주시 해당 여부
     */
    private Boolean isJejuLocation;

    /**
     * 서귀포시 해당 여부
     */
    private Boolean isSeogwipoLocation;

    /**
     * 지원 대상 정보 (HTML)
     */
    private String supportTargetHtml;

    /**
     * 지원 내용 정보 (HTML)
     */
    private String supportContentHtml;

    /**
     * 신청 방법 정보 (HTML)
     */
    private String applicationInfoHtml;

    /**
     * API 원본 데이터 (JSON 문자열)
     */
    private String apiRawData;

    /**
     * 노출 여부
     */
    private Boolean isShow;

    /**
     * 관리자 메모
     */
    private String adminMemo;

    /**
     * 검수 완료 여부
     */
    private Boolean isConfirmed;

    /**
     * 검수 상태 (PENDING/APPROVED/REJECTED)
     */
    private String confirmStatus;

    /**
     * 검수자명
     */
    private String confirmedBy;

    /**
     * 검수일시
     */
    private LocalDateTime confirmedAt;

    /**
     * 검수 메모
     */
    private String confirmMemo;

    /**
     * API 데이터 수집 시간
     */
    private LocalDateTime fetchedAt;

    /**
     * 생성 시간
     */
    private LocalDateTime createdAt;

    /**
     * 수정 시간
     */
    private LocalDateTime updatedAt;
}
