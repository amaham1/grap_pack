package co.grap.pack.qrmanage.shopadmin.menu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 메뉴 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageMenu {

    /** 메뉴 ID */
    private Long id;

    /** 상점 ID */
    private Long shopId;

    /** 카테고리 ID */
    private Long categoryId;

    /** 메뉴명 */
    private String name;

    /** 메뉴 설명 */
    private String description;

    /** 가격 */
    private Integer price;

    /** 대표 이미지 ID */
    private Long primaryImageId;

    /** 정렬 순서 */
    private Integer sortOrder;

    /** 공개 여부 */
    private Boolean isVisible;

    /** 품절 여부 */
    private Boolean isSoldOut;

    /** 생성일시 */
    private LocalDateTime createdAt;

    /** 수정일시 */
    private LocalDateTime updatedAt;

    /** 카테고리명 (조회용) */
    private String categoryName;

    /** 대표 이미지 URL (조회용) */
    private String primaryImageUrl;

    /** 옵션 그룹 목록 (조회용) */
    private List<QrManageMenuOptionGroup> optionGroups;
}
