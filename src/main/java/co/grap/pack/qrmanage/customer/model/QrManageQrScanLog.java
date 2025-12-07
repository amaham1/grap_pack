package co.grap.pack.qrmanage.customer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * QR 스캔 로그 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageQrScanLog {

    /** 로그 ID */
    private Long id;

    /** QR 코드 ID */
    private Long qrCodeId;

    /** 클라이언트 IP 주소 */
    private String ipAddress;

    /** User-Agent */
    private String userAgent;

    /** 스캔 일시 */
    private LocalDateTime scannedAt;
}
