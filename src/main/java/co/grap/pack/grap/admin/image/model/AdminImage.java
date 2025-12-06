package co.grap.pack.grap.admin.image.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

/**
 * 이미지 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Alias("AdminImage")
public class AdminImage {

    /**
     * 이미지 ID
     */
    private Long imageId;

    /**
     * 콘텐츠 ID
     */
    private Long contentId;

    /**
     * 원본 파일명
     */
    private String originalName;

    /**
     * 저장된 파일명
     */
    private String savedName;

    /**
     * 파일 경로
     */
    private String filePath;

    /**
     * 파일 크기 (bytes)
     */
    private Long fileSize;

    /**
     * 표시 순서
     */
    private Integer displayOrder;

    /**
     * 생성일시
     */
    private LocalDateTime createDt;
}
