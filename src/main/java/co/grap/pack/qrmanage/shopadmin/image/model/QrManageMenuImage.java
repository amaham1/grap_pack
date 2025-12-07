package co.grap.pack.qrmanage.shopadmin.image.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 메뉴 이미지 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageMenuImage {

    /** 이미지 ID */
    private Long id;

    /** 메뉴 ID */
    private Long menuId;

    /** 원본 파일명 */
    private String originalFileName;

    /** 저장된 파일명 */
    private String storedFileName;

    /** 파일 경로 */
    private String filePath;

    /** 파일 크기 (bytes) */
    private Long fileSize;

    /** 파일 타입 (MIME type) */
    private String fileType;

    /** 정렬 순서 */
    private Integer sortOrder;

    /** 생성일시 */
    private LocalDateTime createdAt;
}
