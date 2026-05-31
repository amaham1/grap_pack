package co.grap.pack.qrmanage.shopadmin.menu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 메뉴 옵션 항목 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageMenuOption {

    /** 옵션 ID */
    private Long id;

    /** 옵션 그룹 ID */
    private Long optionGroupId;

    /** 옵션명 (예: 라지, 레귤러) */
    private String name;

    /** 정렬 순서 */
    private Integer sortOrder;

    /** 생성일시 */
    private LocalDateTime createdAt;

    /** 수정일시 */
    private LocalDateTime updatedAt;
}
