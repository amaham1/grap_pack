package co.grap.pack.grap.user.content.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자 콘텐츠 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Alias("CmsUserContent")
public class CmsUserContent {

    /**
     * 콘텐츠 ID
     */
    private Long contentId;

    /**
     * 콘텐츠 종류 ID
     */
    private Long contentTypeId;

    /**
     * 콘텐츠 종류명
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
     * 조회수
     */
    private Integer viewCount;

    /**
     * 생성일시
     */
    private LocalDateTime createDt;

    /**
     * 수정일시
     */
    private LocalDateTime updateDt;

    /**
     * 이미지 목록
     */
    private List<String> imageList;
}
