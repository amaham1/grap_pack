package co.grap.pack.qrmanage.shopadmin.menu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 메뉴 옵션 그룹 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageMenuOptionGroup {

    /** 옵션 그룹 ID */
    private Long id;

    /** 메뉴 ID */
    private Long menuId;

    /** 그룹명 (예: 사이즈, 추가 토핑) */
    private String name;

    /** 필수 선택 여부 */
    private Boolean isRequired;

    /** 정렬 순서 */
    private Integer sortOrder;

    /** 생성일시 */
    private LocalDateTime createdAt;

    /** 수정일시 */
    private LocalDateTime updatedAt;

    /** 옵션 항목 목록 (조회용) */
    private List<QrManageMenuOption> options;
}
