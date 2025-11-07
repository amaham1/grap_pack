package com.example.cms.admin.content.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 콘텐츠 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content {

    /**
     * 콘텐츠 ID
     */
    private Long contentId;

    /**
     * 콘텐츠 종류 ID
     */
    private Long contentTypeId;

    /**
     * 콘텐츠 종류명 (조인용)
     */
    private String typeName;

    /**
     * 제목
     */
    private String title;

    /**
     * 내용
     */
    private String content;

    /**
     * 공개 여부
     */
    private Boolean isPublished;

    /**
     * 조회수
     */
    private Integer viewCount;

    /**
     * 작성자 ID
     */
    private Long createdBy;

    /**
     * 작성자명 (조인용)
     */
    private String creatorName;

    /**
     * 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    private LocalDateTime updatedAt;
}
