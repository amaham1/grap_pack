package com.example.cms.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 페이지네이션 유틸리티
 */
public class PaginationUtil {

    private static final int WINDOW_SIZE = 2; // 현재 페이지 ±2

    /**
     * 스마트 페이지네이션 정보 계산
     *
     * 로직:
     * - 총 페이지 7개 이하: 모든 페이지 표시
     * - 총 페이지 7개 초과: [1] ... [current-2 ~ current+2] ... [last] 형식
     *
     * 예시:
     * - currentPage=1, totalPages=10 → [1] [2] [3] ... [10]
     * - currentPage=5, totalPages=10 → [1] ... [3] [4] [5] [6] [7] ... [10]
     * - currentPage=10, totalPages=10 → [1] ... [8] [9] [10]
     *
     * @param currentPage 현재 페이지 (1부터 시작)
     * @param totalPages 전체 페이지 수
     * @return 페이지네이션 정보
     */
    public static PageInfo calculatePageInfo(int currentPage, int totalPages) {
        List<PageNumber> pages = new ArrayList<>();

        // 엣지 케이스: 페이지가 없는 경우
        if (totalPages <= 0) {
            return PageInfo.builder()
                    .pages(pages)
                    .hasPrevious(false)
                    .hasNext(false)
                    .build();
        }

        // 케이스 1: 총 페이지가 7개 이하인 경우 - 모든 페이지 표시
        if (totalPages <= 7) {
            for (int i = 1; i <= totalPages; i++) {
                pages.add(PageNumber.builder()
                        .number(i)
                        .isEllipsis(false)
                        .isActive(i == currentPage)
                        .build());
            }
        } else {
            // 케이스 2: 총 페이지가 7개 초과 - 스마트 페이지네이션
            int startWindow = Math.max(1, currentPage - WINDOW_SIZE);
            int endWindow = Math.min(totalPages, currentPage + WINDOW_SIZE);

            // 현재 페이지가 시작 부근인 경우 (1-3페이지)
            if (currentPage <= 3) {
                startWindow = 1;
                endWindow = 5;
            }
            // 현재 페이지가 끝 부근인 경우 (마지막 3페이지)
            else if (currentPage >= totalPages - 2) {
                startWindow = totalPages - 4;
                endWindow = totalPages;
            }

            // 항상 첫 페이지 추가
            pages.add(PageNumber.builder()
                    .number(1)
                    .isEllipsis(false)
                    .isActive(currentPage == 1)
                    .build());

            // 왼쪽 생략 부호 추가 (필요한 경우)
            if (startWindow > 2) {
                pages.add(PageNumber.builder()
                        .number(-1)
                        .isEllipsis(true)
                        .isActive(false)
                        .build());
            }

            // 윈도우 범위의 페이지 추가
            for (int i = Math.max(2, startWindow); i <= Math.min(totalPages - 1, endWindow); i++) {
                pages.add(PageNumber.builder()
                        .number(i)
                        .isEllipsis(false)
                        .isActive(i == currentPage)
                        .build());
            }

            // 오른쪽 생략 부호 추가 (필요한 경우)
            if (endWindow < totalPages - 1) {
                pages.add(PageNumber.builder()
                        .number(-1)
                        .isEllipsis(true)
                        .isActive(false)
                        .build());
            }

            // 항상 마지막 페이지 추가 (총 페이지가 1보다 큰 경우)
            if (totalPages > 1) {
                pages.add(PageNumber.builder()
                        .number(totalPages)
                        .isEllipsis(false)
                        .isActive(currentPage == totalPages)
                        .build());
            }
        }

        return PageInfo.builder()
                .pages(pages)
                .hasPrevious(currentPage > 1)
                .hasNext(currentPage < totalPages)
                .build();
    }
}
