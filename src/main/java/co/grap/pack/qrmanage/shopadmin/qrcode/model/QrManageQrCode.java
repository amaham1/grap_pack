package co.grap.pack.qrmanage.shopadmin.qrcode.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * QR 코드 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageQrCode {

    /** QR 코드 ID */
    private Long id;

    /** 상점 ID */
    private Long shopId;

    /** QR 타입 (SHOP: 상점 대표, MENU: 메뉴) */
    private QrManageQrCodeType qrType;

    /** QR 코드 고유 식별자 (UUID) */
    private String qrCode;

    /** QR 이미지 파일 경로 */
    private String imagePath;

    /** 연결 URL */
    private String targetUrl;

    /** 만료일시 (null이면 무제한) */
    private LocalDateTime expiresAt;

    /** 활성화 여부 */
    private Boolean isActive;

    /** 생성일시 */
    private LocalDateTime createdAt;

    /** 수정일시 */
    private LocalDateTime updatedAt;

    /** 상점명 (조회용) */
    private String shopName;

    /** 스캔 횟수 (조회용) */
    private Integer scanCount;

    /**
     * 만료 여부 확인
     */
    public boolean isExpired() {
        if (expiresAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
