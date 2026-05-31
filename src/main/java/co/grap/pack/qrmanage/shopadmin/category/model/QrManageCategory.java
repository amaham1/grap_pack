package co.grap.pack.qrmanage.shopadmin.category.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 메뉴 카테고리 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageCategory {

    /** 카테고리 ID */
    private Long id;

    /** 상점 ID */
    private Long shopId;

    /** 카테고리명 */
    private String name;

    /** 설명 */
    private String description;

    /** 정렬 순서 */
    private Integer sortOrder;

    /** 공개 여부 */
    private Boolean isVisible;

    /** 생성일시 */
    private LocalDateTime createdAt;

    /** 수정일시 */
    private LocalDateTime updatedAt;

    /** 해당 카테고리의 메뉴 수 (조회용) */
    private Integer menuCount;
}
