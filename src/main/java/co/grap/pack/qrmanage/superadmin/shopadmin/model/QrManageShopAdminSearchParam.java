package co.grap.pack.qrmanage.superadmin.shopadmin.model;

import co.grap.pack.qrmanage.shopadmin.auth.model.QrManageShopAdminStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 상점관리자 검색 파라미터
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageShopAdminSearchParam {

    /** 상태 필터 */
    private QrManageShopAdminStatus status;

    /** 검색어 (이름, 이메일, 아이디) */
    private String keyword;

    /** 페이지 번호 (1부터 시작) */
    @Builder.Default
    private int page = 1;

    /** 페이지당 항목 수 */
    @Builder.Default
    private int size = 20;

    /**
     * offset 계산
     */
    public int getOffset() {
        return (page - 1) * size;
    }
}
