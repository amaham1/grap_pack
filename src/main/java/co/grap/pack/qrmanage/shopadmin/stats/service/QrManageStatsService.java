package co.grap.pack.qrmanage.shopadmin.stats.service;

import co.grap.pack.qrmanage.shopadmin.stats.mapper.QrManageStatsMapper;
import co.grap.pack.qrmanage.shopadmin.stats.model.QrManageScanStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * QR 스캔 통계 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrManageStatsService {

    private final QrManageStatsMapper statsMapper;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 상점의 일별 스캔 통계 조회 (최근 30일)
     */
    public List<QrManageScanStats> getDailyStats(Long shopId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        return statsMapper.getDailyStats(shopId, startDate.format(DATE_FORMAT), endDate.format(DATE_FORMAT));
    }

    /**
     * 상점의 일별 스캔 통계 조회 (기간 지정)
     */
    public List<QrManageScanStats> getDailyStats(Long shopId, String startDate, String endDate) {
        return statsMapper.getDailyStats(shopId, startDate, endDate);
    }

    /**
     * 상점의 QR 타입별 스캔 통계
     */
    public List<QrManageScanStats> getStatsByQrType(Long shopId) {
        return statsMapper.getStatsByQrType(shopId);
    }

    /**
     * 상점의 대시보드 통계
     */
    public Map<String, Integer> getDashboardStats(Long shopId) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", statsMapper.getTotalScanCount(shopId));
        stats.put("today", statsMapper.getTodayScanCount(shopId));
        stats.put("week", statsMapper.getWeekScanCount(shopId));
        stats.put("month", statsMapper.getMonthScanCount(shopId));
        return stats;
    }

    /**
     * 전체 시스템 일별 스캔 통계 (최고관리자용)
     */
    public List<QrManageScanStats> getSystemDailyStats() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        return statsMapper.getSystemDailyStats(startDate.format(DATE_FORMAT), endDate.format(DATE_FORMAT));
    }

    /**
     * 전체 시스템 일별 스캔 통계 (기간 지정, 최고관리자용)
     */
    public List<QrManageScanStats> getSystemDailyStats(String startDate, String endDate) {
        return statsMapper.getSystemDailyStats(startDate, endDate);
    }

    /**
     * 전체 시스템 대시보드 통계 (최고관리자용)
     */
    public Map<String, Integer> getSystemDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", statsMapper.getSystemTotalScanCount());
        stats.put("today", statsMapper.getSystemTodayScanCount());
        return stats;
    }

    /**
     * 상점별 스캔 랭킹 (최고관리자용)
     */
    public List<QrManageScanStats> getShopScanRanking(int limit) {
        return statsMapper.getShopScanRanking(limit);
    }
}
