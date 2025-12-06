package co.grap.pack.grap.user.content.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

/**
 * 사용자 콘텐츠 검색 파라미터
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Alias("CmsUserContentSearchParam")
public class CmsUserContentSearchParam {

    /**
     * 검색 키워드
     */
    private String keyword;

    /**
     * 콘텐츠 종류 ID
     */
    private Long contentTypeId;

    /**
     * 페이지 번호 (1부터 시작)
     */
    private Integer page;

    /**
     * 페이지 크기
     */
    private Integer size;

    /**
     * offset 계산
     */
    public Integer getOffset() {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        return (page - 1) * size;
    }
}
