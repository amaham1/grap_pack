package co.grap.pack.qrmanage.superadmin.shop.model;

import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShopStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 상점 검색 파라미터
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageShopSearchParam {

    /** 상태 필터 */
    private QrManageShopStatus status;

    /** 노출 여부 필터 */
    private Boolean isVisible;

    /** 검색어 (상점명, 주소) */
    private String keyword;

    /** 페이지 번호 */
    @Builder.Default
    private int page = 1;

    /** 페이지당 항목 수 */
    @Builder.Default
    private int size = 20;

    public int getOffset() {
        return (page - 1) * size;
    }
}
