package co.grap.pack.user.image.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 축제/행사 이미지 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FestivalImage {

    /** 이미지 ID */
    private Long imageId;

    /** 축제/행사 ID (festivals.id FK) */
    private Long festivalId;

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
