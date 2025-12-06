package co.grap.pack.grap.user.content.service;

import co.grap.pack.common.util.PaginationUtil;
import co.grap.pack.grap.user.content.mapper.UserWelfareMapper;
import co.grap.pack.grap.user.content.model.UserWelfareRequest;
import co.grap.pack.grap.user.image.service.UserImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 사용자 복지서비스 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserWelfareService {

    private final UserWelfareMapper welfareMapper;
    private final UserImageService imageService;

    /**
     * 노출 설정된 복지서비스 목록 조회 (페이징)
     */
    public Map<String, Object> getWelfareList(String keyword, Integer page, Integer size) {
        // 전체 데이터 개수 조회
        int totalCount = welfareMapper.selectVisibleWelfareCount(keyword);

        // 공통 페이징 정보 생성
        Map<String, Object> paginationResult = PaginationUtil.createPaginationResult(totalCount, page, size);
        int offset = (int) paginationResult.get("offset");
        int currentSize = (int) paginationResult.get("size");

        // 데이터 조회
        List<Map<String, Object>> welfareList = welfareMapper.selectVisibleWelfareList(keyword, offset, currentSize);

        // 각 복지서비스 데이터에 미리보기 추가
        for (Map<String, Object> welfare : welfareList) {
            enrichWelfareData(welfare);
        }

        // 최종 결과 구성
        Map<String, Object> result = new HashMap<>();
        result.putAll(paginationResult);  // 페이징 정보 추가 (offset, currentPage, size, totalPages, totalCount, pageInfo)
        result.put("welfareList", welfareList);
        result.put("keyword", keyword);

        return result;
    }

    /**
     * 복지서비스 데이터 보강 (지원 대상, 지원 내용 미리보기)
     */
    private void enrichWelfareData(Map<String, Object> welfare) {
        // 1. 지원 대상 미리보기 생성 (HTML 태그 제거, 100자 제한)
        String supportTargetHtml = (String) welfare.get("supportTargetHtml");
        if (supportTargetHtml != null && !supportTargetHtml.isEmpty()) {
            String preview = cleanHtmlAndLimit(supportTargetHtml, 100);
            if (!preview.isEmpty()) {
                welfare.put("supportTargetPreview", preview);
            }
        }

        // 2. 지원 내용 미리보기 생성 (HTML 태그 제거, 120자 제한)
        String supportContentHtml = (String) welfare.get("supportContentHtml");
        if (supportContentHtml != null && !supportContentHtml.isEmpty()) {
            String preview = cleanHtmlAndLimit(supportContentHtml, 120);
            if (!preview.isEmpty()) {
                welfare.put("supportContentPreview", preview);
            }
        }
    }

    /**
     * HTML 태그 제거 및 문자 제한
     */
    private String cleanHtmlAndLimit(String html, int maxLength) {
        String cleaned = html
            .replaceAll("<[^>]*>", "")  // HTML 태그 제거
            .replaceAll("&nbsp;", " ")   // &nbsp; 제거
            .replaceAll("&lt;", "<")     // &lt; 변환
            .replaceAll("&gt;", ">")     // &gt; 변환
            .replaceAll("&amp;", "&")    // &amp; 변환
            .replaceAll("\\s+", " ")     // 연속 공백 제거
            .trim();

        if (cleaned.length() > maxLength) {
            cleaned = cleaned.substring(0, maxLength) + "...";
        }

        return cleaned;
    }

    /**
     * 복지서비스 상세 조회
     */
    public Map<String, Object> getWelfareDetail(Long id) {
        return welfareMapper.selectVisibleWelfareById(id);
    }

    /**
     * 복지서비스 등록 요청 (사용자)
     * @param request 등록 요청 데이터
     * @param images 이미지 파일 목록
     * @param thumbnailIndex 썸네일로 설정할 이미지 인덱스 (null이면 썸네일 없음)
     * @return 생성된 복지서비스 ID
     */
    @Transactional
    public Long createWelfareRequest(UserWelfareRequest request, List<MultipartFile> images, Integer thumbnailIndex) throws IOException {
        log.info("✅ [CHECK] 복지서비스 등록 요청 시작: serviceName={}", request.getServiceName());

        // 1. 복지서비스 데이터 저장
        welfareMapper.insertWelfareRequest(request);
        Long welfareServiceId = welfareMapper.selectLastInsertId();
        log.info("✅ [CHECK] 복지서비스 데이터 저장 완료: welfareServiceId={}", welfareServiceId);

        // 2. 이미지 업로드 처리
        if (images != null && !images.isEmpty()) {
            List<MultipartFile> validImages = images.stream()
                .filter(file -> file != null && !file.isEmpty())
                .toList();

            if (!validImages.isEmpty()) {
                log.info("✅ [CHECK] 이미지 업로드 시작: 총 {}개", validImages.size());
                imageService.uploadWelfareServiceImages(welfareServiceId, validImages, thumbnailIndex);
                log.info("✅ [CHECK] 이미지 업로드 완료");
            }
        }

        log.info("✅ [CHECK] 복지서비스 등록 요청 완료: welfareServiceId={}", welfareServiceId);
        return welfareServiceId;
    }
}
