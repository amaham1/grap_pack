package com.example.cms.user.content.service;

import com.example.cms.common.util.PaginationUtil;
import com.example.cms.user.content.mapper.UserExhibitionMapper;
import com.example.cms.user.content.model.UserExhibitionRequest;
import com.example.cms.user.image.service.UserImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 사용자 공연/전시 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserExhibitionService {

    private final UserExhibitionMapper exhibitionMapper;
    private final UserImageService imageService;

    /**
     * 노출 설정된 공연/전시 목록 조회 (페이징, 탭별 필터링)
     * @param tab 탭 구분 (ongoing: 진행 중, upcoming: 다가올 공연, ended: 끝난 공연)
     */
    public Map<String, Object> getExhibitionList(String keyword, String tab, Integer page, Integer size) {
        // 유효한 탭 값 확인 (기본값: ongoing)
        if (tab == null || (!tab.equals("ongoing") && !tab.equals("upcoming") && !tab.equals("ended"))) {
            tab = "ongoing";
        }

        // 전체 데이터 개수 조회
        int totalCount = exhibitionMapper.selectVisibleExhibitionCount(keyword, tab);

        // 공통 페이징 정보 생성
        Map<String, Object> paginationResult = PaginationUtil.createPaginationResult(totalCount, page, size);
        int offset = (int) paginationResult.get("offset");
        int currentSize = (int) paginationResult.get("size");

        // 데이터 조회
        List<Map<String, Object>> exhibitionList = exhibitionMapper.selectVisibleExhibitionList(keyword, tab, offset, currentSize);

        // 각 공연/전시 데이터에 마감임박 계산 추가
        LocalDateTime now = LocalDateTime.now();
        for (Map<String, Object> exhibition : exhibitionList) {
            enrichExhibitionData(exhibition, now);
        }

        // 최종 결과 구성
        Map<String, Object> result = new HashMap<>();
        result.putAll(paginationResult);  // 페이징 정보 추가 (offset, currentPage, size, totalPages, totalCount, pageInfo)
        result.put("exhibitionList", exhibitionList);
        result.put("keyword", keyword);
        result.put("tab", tab);

        return result;
    }

    /**
     * 공연/전시 데이터 보강 (마감임박 계산)
     */
    private void enrichExhibitionData(Map<String, Object> exhibition, LocalDateTime now) {
        // 마감임박 계산 (종료일까지 남은 일수)
        Object endDateObj = exhibition.get("endDate");
        if (endDateObj instanceof LocalDateTime) {
            LocalDateTime endDate = (LocalDateTime) endDateObj;
            long daysUntilEnd = ChronoUnit.DAYS.between(now, endDate);
            exhibition.put("daysUntilEnd", daysUntilEnd);
        }
    }

    /**
     * 공연/전시 상세 조회
     */
    public Map<String, Object> getExhibitionDetail(Long id) {
        return exhibitionMapper.selectVisibleExhibitionById(id);
    }

    /**
     * 공연/전시 등록 요청 (사용자)
     * @param request 등록 요청 데이터
     * @param images 이미지 파일 목록
     * @param thumbnailIndex 썸네일로 설정할 이미지 인덱스 (null이면 썸네일 없음)
     * @return 생성된 공연/전시 ID
     */
    @Transactional
    public Long createExhibitionRequest(UserExhibitionRequest request, List<MultipartFile> images, Integer thumbnailIndex) throws IOException {
        log.info("✅ [CHECK] 공연/전시 등록 요청 시작: title={}", request.getTitle());

        // 1. 공연/전시 데이터 저장
        exhibitionMapper.insertExhibitionRequest(request);
        Long exhibitionId = exhibitionMapper.selectLastInsertId();
        log.info("✅ [CHECK] 공연/전시 데이터 저장 완료: exhibitionId={}", exhibitionId);

        // 2. 이미지 업로드 처리
        if (images != null && !images.isEmpty()) {
            List<MultipartFile> validImages = images.stream()
                .filter(file -> file != null && !file.isEmpty())
                .toList();

            if (!validImages.isEmpty()) {
                log.info("✅ [CHECK] 이미지 업로드 시작: 총 {}개", validImages.size());
                imageService.uploadExhibitionImages(exhibitionId, validImages, thumbnailIndex);
                log.info("✅ [CHECK] 이미지 업로드 완료");
            }
        }

        log.info("✅ [CHECK] 공연/전시 등록 요청 완료: exhibitionId={}", exhibitionId);
        return exhibitionId;
    }
}
