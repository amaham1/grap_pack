package co.grap.pack.qrmanage.superadmin.shop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 상점 검수 이력 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageShopReviewHistory {

    /** 검수 이력 ID */
    private Long id;

    /** 상점 ID */
    private Long shopId;

    /** 검수자 (최고관리자) ID */
    private Long reviewerId;

    /** 처리 (APPROVED, REJECTED) */
    private String action;

    /** 코멘트 */
    private String comment;

    /** 생성일시 */
    private LocalDateTime createdAt;

    /** 검수자 이름 (조인용) */
    private String reviewerName;
}
