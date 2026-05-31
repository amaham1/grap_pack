package co.grap.pack.qrmanage.shopadmin.stats.mapper;

import co.grap.pack.qrmanage.shopadmin.stats.model.QrManageScanStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * QR 스캔 통계 Mapper
 */
@Mapper
public interface QrManageStatsMapper {

    /**
     * 상점의 일별 스캔 통계 조회
     */
    List<QrManageScanStats> getDailyStats(
            @Param("shopId") Long shopId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 상점의 QR 타입별 스캔 통계
     */
    List<QrManageScanStats> getStatsByQrType(@Param("shopId") Long shopId);

    /**
     * 상점의 총 스캔 횟수
     */
    Integer getTotalScanCount(@Param("shopId") Long shopId);

    /**
     * 상점의 오늘 스캔 횟수
     */
    Integer getTodayScanCount(@Param("shopId") Long shopId);

    /**
     * 상점의 이번 주 스캔 횟수
     */
    Integer getWeekScanCount(@Param("shopId") Long shopId);

    /**
     * 상점의 이번 달 스캔 횟수
     */
    Integer getMonthScanCount(@Param("shopId") Long shopId);

    /**
     * 전체 시스템 일별 스캔 통계 (최고관리자용)
     */
    List<QrManageScanStats> getSystemDailyStats(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 전체 시스템 총 스캔 횟수 (최고관리자용)
     */
    Integer getSystemTotalScanCount();

    /**
     * 전체 시스템 오늘 스캔 횟수 (최고관리자용)
     */
    Integer getSystemTodayScanCount();

    /**
     * 상점별 스캔 랭킹 (최고관리자용)
     */
    List<QrManageScanStats> getShopScanRanking(@Param("limit") int limit);
}
