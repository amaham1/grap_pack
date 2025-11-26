package com.example.cms.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 페이지네이션 유틸리티
 */
public class PaginationUtil {

    private static final int WINDOW_SIZE = 2; // 현재 페이지 ±2
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 12;

    /**
     * 스마트 페이지네이션 정보 계산
     *
     * 로직:
     * - 총 페이지 7개 이하: 모든 페이지 표시
     * - 총 페이지 7개 초과: 항상 [1] ... [current-2 ~ current+2] ... [last] 형식
     *
     * 예시:
     * - currentPage=1, totalPages=10 → [1] [2] [3] ... [10]
     * - currentPage=2, totalPages=10 → [1] [2] [3] [4] ... [10]
     * - currentPage=3, totalPages=10 → [1] [2] [3] [4] [5] ... [10]
     * - currentPage=5, totalPages=10 → [1] ... [3] [4] [5] [6] [7] ... [10]
     * - currentPage=8, totalPages=10 → [1] ... [6] [7] [8] [9] [10]
     * - currentPage=9, totalPages=10 → [1] ... [7] [8] [9] [10]
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
            // 항상 현재 페이지 기준 ±2 윈도우 사용
            int startWindow = Math.max(1, currentPage - WINDOW_SIZE);
            int endWindow = Math.min(totalPages, currentPage + WINDOW_SIZE);

            // 항상 첫 페이지 추가
            pages.add(PageNumber.builder()
                    .number(1)
                    .isEllipsis(false)
                    .isActive(currentPage == 1)
                    .build());

            // 왼쪽 생략 부호 추가 (필요한 경우)
            // startWindow가 2보다 크면 1과 startWindow 사이에 생략된 페이지가 있음
            if (startWindow > 2) {
                pages.add(PageNumber.builder()
                        .number(-1)
                        .isEllipsis(true)
                        .isActive(false)
                        .build());
            }

            // 윈도우 범위의 페이지 추가 (1과 마지막 페이지 제외)
            for (int i = Math.max(2, startWindow); i <= Math.min(totalPages - 1, endWindow); i++) {
                pages.add(PageNumber.builder()
                        .number(i)
                        .isEllipsis(false)
                        .isActive(i == currentPage)
                        .build());
            }

            // 오른쪽 생략 부호 추가 (필요한 경우)
            // endWindow가 totalPages-1보다 작으면 endWindow와 마지막 페이지 사이에 생략된 페이지가 있음
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

    /**
     * 페이징 정보를 포함한 결과 Map 생성
     *
     * @param totalCount 전체 데이터 개수
     * @param requestedPage 요청된 페이지 번호 (null 가능)
     * @param requestedSize 요청된 페이지 크기 (null 가능)
     * @return 페이징 정보가 포함된 Map (offset, currentPage, size, totalPages, totalCount, pageInfo)
     */
    public static Map<String, Object> createPaginationResult(int totalCount, Integer requestedPage, Integer requestedSize) {
        // 기본값 설정
        int currentPage = (requestedPage == null || requestedPage < 1) ? DEFAULT_PAGE : requestedPage;
        int size = (requestedSize == null || requestedSize < 1) ? DEFAULT_SIZE : requestedSize;

        // 페이징 계산
        int offset = (currentPage - 1) * size;
        int totalPages = (int) Math.ceil((double) totalCount / size);

        // PageInfo 생성
        PageInfo pageInfo = calculatePageInfo(currentPage, totalPages);

        // 결과 Map 구성
        Map<String, Object> result = new HashMap<>();
        result.put("offset", offset);
        result.put("currentPage", currentPage);
        result.put("size", size);
        result.put("totalPages", totalPages);
        result.put("totalCount", totalCount);
        result.put("pageInfo", pageInfo);

        return result;
    }
}
