package com.example.cms.user.content.service;

import com.example.cms.common.util.PaginationUtil;
import com.example.cms.user.content.mapper.UserFestivalMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 사용자 축제/행사 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFestivalService {

    private final UserFestivalMapper festivalMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 노출 설정된 축제/행사 목록 조회 (페이징)
     */
    public Map<String, Object> getFestivalList(String keyword, Integer page, Integer size) {
        // 전체 데이터 개수 조회
        int totalCount = festivalMapper.selectVisibleFestivalCount(keyword);

        // 공통 페이징 정보 생성
        Map<String, Object> paginationResult = PaginationUtil.createPaginationResult(totalCount, page, size);
        int offset = (int) paginationResult.get("offset");
        int currentSize = (int) paginationResult.get("size");

        // 데이터 조회
        List<Map<String, Object>> festivalList = festivalMapper.selectVisibleFestivalList(keyword, offset, currentSize);

        // 각 축제 데이터에 추가 정보 생성
        LocalDateTime now = LocalDateTime.now();
        for (Map<String, Object> festival : festivalList) {
            enrichFestivalData(festival, now);
        }

        // 최종 결과 구성
        Map<String, Object> result = new HashMap<>();
        result.putAll(paginationResult);  // 페이징 정보 추가 (offset, currentPage, size, totalPages, totalCount, pageInfo)
        result.put("festivalList", festivalList);
        result.put("keyword", keyword);

        return result;
    }

    /**
     * 축제 데이터 보강 (썸네일, 미리보기, 마감임박 계산)
     */
    private void enrichFestivalData(Map<String, Object> festival, LocalDateTime now) {
        // 1. 썸네일 URL 추출 (filesInfo JSON에서 첫 번째 이미지)
        String filesInfo = (String) festival.get("filesInfo");
        if (filesInfo != null && !filesInfo.isEmpty() && !filesInfo.equals("null")) {
            try {
                List<Map<String, Object>> filesList = objectMapper.readValue(
                    filesInfo,
                    new TypeReference<List<Map<String, Object>>>() {}
                );
                if (!filesList.isEmpty()) {
                    Map<String, Object> firstFile = filesList.get(0);
                    String thumbnailUrl = (String) firstFile.get("url");
                    if (thumbnailUrl != null) {
                        festival.put("thumbnailUrl", thumbnailUrl);
                    }
                }
            } catch (Exception e) {
                log.debug("파일 정보 파싱 실패: {}", e.getMessage());
            }
        }

        // 2. 내용 미리보기 생성 (HTML 태그 제거, 150자 제한)
        String contentHtml = (String) festival.get("contentHtml");
        if (contentHtml != null && !contentHtml.isEmpty()) {
            String preview = contentHtml
                .replaceAll("<[^>]*>", "")  // HTML 태그 제거
                .replaceAll("&nbsp;", " ")   // &nbsp; 제거
                .replaceAll("\\s+", " ")     // 연속 공백 제거
                .trim();

            if (preview.length() > 150) {
                preview = preview.substring(0, 150) + "...";
            }

            if (!preview.isEmpty()) {
                festival.put("contentPreview", preview);
            }
        }

        // 3. 마감임박 계산 (작성일로부터 남은 일수)
        Object writtenDateObj = festival.get("writtenDate");
        if (writtenDateObj instanceof LocalDateTime) {
            LocalDateTime writtenDate = (LocalDateTime) writtenDateObj;
            long daysUntil = ChronoUnit.DAYS.between(now, writtenDate);
            festival.put("daysUntil", daysUntil);
        }
    }

    /**
     * 축제/행사 상세 조회
     */
    public Map<String, Object> getFestivalDetail(Long id) {
        return festivalMapper.selectVisibleFestivalById(id);
    }
}
