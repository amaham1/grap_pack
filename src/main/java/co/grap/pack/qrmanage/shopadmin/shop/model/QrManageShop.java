package co.grap.pack.qrmanage.shopadmin.shop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 상점 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageShop {

    /** 상점 ID */
    private Long id;

    /** 상점관리자 ID */
    private Long shopAdminId;

    /** 상점명 */
    private String name;

    /** 상점 설명 */
    private String description;

    /** 주소 */
    private String address;

    /** 연락처 */
    private String phone;

    /** 노출 여부 (최고관리자 관리) */
    private Boolean isVisible;

    /** 상태 (PENDING, APPROVED, REJECTED) */
    private QrManageShopStatus status;

    /** 생성일시 */
    private LocalDateTime createdAt;

    /** 수정일시 */
    private LocalDateTime updatedAt;

    /** 상점관리자 이름 (조인용) */
    private String shopAdminName;

    /** 상점관리자 이메일 (조인용) */
    private String shopAdminEmail;
}
