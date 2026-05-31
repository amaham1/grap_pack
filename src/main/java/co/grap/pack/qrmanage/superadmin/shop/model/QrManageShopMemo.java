package co.grap.pack.qrmanage.superadmin.shop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 상점 메모 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageShopMemo {

    /** 메모 ID */
    private Long id;

    /** 상점 ID */
    private Long shopId;

    /** 작성자 (최고관리자) ID */
    private Long adminId;

    /** 메모 내용 */
    private String content;

    /** 생성일시 */
    private LocalDateTime createdAt;

    /** 작성자 이름 (조인용) */
    private String adminName;
}
