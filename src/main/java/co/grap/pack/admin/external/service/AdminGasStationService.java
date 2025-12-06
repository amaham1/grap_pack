package co.grap.pack.admin.external.service;

import co.grap.pack.admin.external.mapper.AdminGasStationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자 주유소 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminGasStationService {

    private final AdminGasStationMapper gasStationMapper;

    /**
     * 주유소 목록 조회 (페이징)
     */
    public Map<String, Object> getGasStationList(String keyword, Integer page, Integer size) {
        // 기본값 설정
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        int offset = (page - 1) * size;

        List<Map<String, Object>> gasStationList = gasStationMapper.selectGasStationList(keyword, offset, size);
        int totalCount = gasStationMapper.selectGasStationCount(keyword);

        // 페이징 정보 계산
        int totalPages = (int) Math.ceil((double) totalCount / size);

        Map<String, Object> result = new HashMap<>();
        result.put("gasStationList", gasStationList);
        result.put("currentPage", page);
        result.put("totalPages", totalPages);
        result.put("totalCount", totalCount);
        result.put("size", size);
        result.put("keyword", keyword);

        return result;
    }

    /**
     * 주유소 상세 조회
     */
    public Map<String, Object> getGasStationDetail(Long id) {
        return gasStationMapper.selectGasStationById(id);
    }

    /**
     * 주유소 삭제
     */
    @Transactional
    public void deleteGasStation(Long id) {
        gasStationMapper.deleteGasStation(id);
    }
}
