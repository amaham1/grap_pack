package co.grap.pack.qrgen.generator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QR 코드 생성 요청 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrGenRequest {

    /** QR 콘텐츠 타입 */
    private QrGenContentType contentType;

    /** QR 코드에 인코딩할 값 */
    private String contentValue;

    /** QR 크기 (100-1000, 기본값 300) */
    @Builder.Default
    private Integer size = 300;

    /** 에러 보정 레벨 (L, M, Q, H) */
    @Builder.Default
    private String errorCorrection = "M";

    /** 전경색 (HEX) */
    @Builder.Default
    private String foregroundColor = "#000000";

    /** 배경색 (HEX) */
    @Builder.Default
    private String backgroundColor = "#FFFFFF";

    /** 제목 (히스토리 저장용) */
    private String title;

    /** 메모 */
    private String memo;

    /** 파일로 저장할지 여부 */
    @Builder.Default
    private Boolean saveToFile = false;
}
