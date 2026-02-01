package co.grap.pack.qrgen.generator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * QR 코드 생성 이력 모델 (DB 저장용)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrGenHistory {

    /** 이력 ID */
    private Long qrGenHistoryId;

    /** 사용자 ID */
    private Long qrGenHistoryUserId;

    /** QR 콘텐츠 타입 */
    private String qrGenHistoryContentType;

    /** QR 코드에 인코딩된 값 */
    private String qrGenHistoryContentValue;

    /** QR 크기 (px) */
    private Integer qrGenHistorySize;

    /** 에러 보정 레벨 */
    private String qrGenHistoryErrorCorrection;

    /** 전경색 */
    private String qrGenHistoryFgColor;

    /** 배경색 */
    private String qrGenHistoryBgColor;

    /** QR 이미지 상대경로 */
    private String qrGenHistoryImagePath;

    /** 제목 */
    private String qrGenHistoryTitle;

    /** 메모 */
    private String qrGenHistoryMemo;

    /** 생성일시 */
    private LocalDateTime qrGenHistoryCreatedAt;

    // === 조회용 조인 필드 ===
    /** 사용자명 (조회 시 조인) */
    private String qrGenUserUsername;

    /**
     * Request로부터 History 생성
     */
    public static QrGenHistory fromRequest(QrGenRequest request, Long userId) {
        return QrGenHistory.builder()
            .qrGenHistoryUserId(userId)
            .qrGenHistoryContentType(request.getContentType().name())
            .qrGenHistoryContentValue(request.getContentValue())
            .qrGenHistorySize(request.getSize())
            .qrGenHistoryErrorCorrection(request.getErrorCorrection())
            .qrGenHistoryFgColor(request.getForegroundColor())
            .qrGenHistoryBgColor(request.getBackgroundColor())
            .qrGenHistoryTitle(request.getTitle())
            .qrGenHistoryMemo(request.getMemo())
            .build();
    }
}
