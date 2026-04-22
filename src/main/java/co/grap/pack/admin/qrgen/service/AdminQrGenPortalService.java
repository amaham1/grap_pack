package co.grap.pack.admin.qrgen.service;

import co.grap.pack.admin.qrgen.mapper.AdminQrGenQueryMapper;
import co.grap.pack.common.util.PaginationUtil;
import co.grap.pack.qrgen.generator.model.QrGenContentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 통합 운영 포털 QRgen 서비스다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminQrGenPortalService {

    private final AdminQrGenQueryMapper adminQrGenQueryMapper;

    /**
     * QRgen 회원 목록을 조회한다.
     */
    public Map<String, Object> getUserList(String keyword, Boolean isActive, int page, int size) {
        long totalCount = adminQrGenQueryMapper.countUsers(keyword, isActive);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        Map<String, Object> result = new HashMap<>();
        result.put("items", adminQrGenQueryMapper.selectUsers(keyword, isActive, size, (page - 1) * size));
        result.put("currentPage", page);
        result.put("totalPages", totalPages);
        result.put("totalCount", totalCount);
        result.put("pageInfo", PaginationUtil.calculatePageInfo(page, totalPages));
        return result;
    }

    /**
     * QRgen 회원 상세를 조회한다.
     */
    public Map<String, Object> getUserDetail(Long userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("user", adminQrGenQueryMapper.selectUserById(userId));
        result.put("recentHistories", adminQrGenQueryMapper.selectHistories(userId, null, 10, 0));
        result.put("historyCount", adminQrGenQueryMapper.countHistories(userId, null));
        result.put("typeStats", adminQrGenQueryMapper.selectTypeStats(userId));
        return result;
    }

    /**
     * QRgen 이력 목록을 조회한다.
     */
    public Map<String, Object> getHistoryList(Long userId, String contentType, int page, int size) {
        long totalCount = adminQrGenQueryMapper.countHistories(userId, contentType);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        Map<String, Object> result = new HashMap<>();
        result.put("items", adminQrGenQueryMapper.selectHistories(userId, contentType, size, (page - 1) * size));
        result.put("currentPage", page);
        result.put("totalPages", totalPages);
        result.put("totalCount", totalCount);
        result.put("pageInfo", PaginationUtil.calculatePageInfo(page, totalPages));
        result.put("typeStats", adminQrGenQueryMapper.selectTypeStats(userId));
        return result;
    }

    /**
     * QRgen 이력 상세를 조회한다.
     */
    public Map<String, Object> getHistoryDetail(Long historyId) {
        Map<String, Object> result = new HashMap<>();
        result.put("history", adminQrGenQueryMapper.selectHistoryById(historyId));
        return result;
    }

    /**
     * 콘텐츠 타입 목록을 제공한다.
     */
    public QrGenContentType[] getContentTypes() {
        return QrGenContentType.values();
    }
}
