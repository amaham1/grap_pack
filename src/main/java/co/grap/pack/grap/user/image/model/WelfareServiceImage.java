package co.grap.pack.grap.user.image.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 복지서비스 이미지 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WelfareServiceImage {

    /** 이미지 ID */
    private Long imageId;

    /** 복지서비스 ID (welfare_services.id FK) */
    private Long welfareServiceId;

    /** 원본 파일명 */
    private String originalName;

    /** 저장된 파일명 */
    private String savedName;

    /** 파일 경로 */
    private String filePath;

    /** 파일 크기 (bytes) */
    private Long fileSize;

    /** 썸네일 여부 */
    private Boolean isThumbnail;

    /** 표시 순서 */
    private Integer displayOrder;

    /** 생성일시 */
    private LocalDateTime createdAt;
}
