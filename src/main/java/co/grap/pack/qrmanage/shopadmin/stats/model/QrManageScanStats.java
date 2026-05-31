package co.grap.pack.qrmanage.shopadmin.stats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QR 스캔 통계 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageScanStats {

    /** 날짜 (YYYY-MM-DD) */
    private String date;

    /** 스캔 횟수 */
    private Integer count;

    /** QR 코드 ID */
    private Long qrCodeId;

    /** QR 타입 */
    private String qrType;

    /** 상점명 (랭킹 조회용) */
    private String shopName;
}
