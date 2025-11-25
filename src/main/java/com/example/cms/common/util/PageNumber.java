package com.example.cms.common.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 페이지 번호 정보
 */
@Data
@Builder
@AllArgsConstructor
public class PageNumber {
    /**
     * 페이지 번호 (ellipsis의 경우 -1)
     */
    private int number;

    /**
     * 생략 부호(...) 여부
     */
    private boolean isEllipsis;

    /**
     * 현재 페이지 여부
     */
    private boolean isActive;
}
