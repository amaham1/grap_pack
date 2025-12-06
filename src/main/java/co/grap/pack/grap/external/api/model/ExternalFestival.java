package co.grap.pack.grap.external.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 축제/행사 정보 Entity
 * 외부 API: https://www.jeju.go.kr/api/jejutoseoul/festival/
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalFestival {
    /**
     * 내부 ID
     */
    private Long id;

    /**
     * 외부 API 원본 ID (seq)
     */
    private String originalApiId;

    /**
     * 축제/행사명
     */
    private String title;

    /**
     * HTML 형식 내용
     */
    private String contentHtml;

    /**
     * 원본 페이지 URL
     */
    private String sourceUrl;

    /**
     * 작성자명
     */
    private String writerName;

    /**
     * 작성일
     */
    private LocalDateTime writtenDate;

    /**
     * 첨부 파일 정보 (JSON 문자열)
     */
    private String filesInfo;

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
