package co.grap.pack.grap.external.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공연/전시 정보 Entity
 * 외부 API: http://www.jeju.go.kr/rest/JejuExhibitionService/getJejucultureExhibitionList
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CmsExternalExhibition {
    /**
     * 내부 ID
     */
    private Long id;

    /**
     * 외부 API 원본 ID (seq)
     */
    private String originalApiId;

    /**
     * 공연/전시명
     */
    private String title;

    /**
     * 카테고리명 (전시, 공연, 행사 등)
     */
    private String categoryName;

    /**
     * 커버 이미지 URL
     */
    private String coverImageUrl;

    /**
     * 시작일
     */
    private LocalDateTime startDate;

    /**
     * 종료일
     */
    private LocalDateTime endDate;

    /**
     * 운영 시간 정보
     */
    private String timeInfo;

    /**
     * 장소명
     */
    private String locationName;

    /**
     * 주최/주관 정보
     */
    private String organizerInfo;

    /**
     * 문의 전화번호
     */
    private String telNumber;

    /**
     * 요금 정보
     */
    private String payInfo;

    /**
     * 상태 정보
     */
    private String statusInfo;

    /**
     * 구분명
     */
    private String divisionName;

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
