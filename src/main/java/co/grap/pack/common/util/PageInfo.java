package co.grap.pack.common.util;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * 페이지네이션 정보
 */
@Data
@Builder
public class PageInfo {
    /**
     * 표시할 페이지 번호 목록
     */
    private List<PageNumber> pages;

    /**
     * 이전 페이지 존재 여부
     */
    private boolean hasPrevious;

    /**
     * 다음 페이지 존재 여부
     */
    private boolean hasNext;
}
