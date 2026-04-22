package co.grap.pack.qrmanage.superadmin.visitorstats.mapper;

import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorDashboardStats;
import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorDailyStats;
import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorDeviceStats;
import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorMenuStats;
import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorRouteStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 슈퍼 관리자 방문자 통계 Mapper 다.
 */
@Mapper
public interface QrManageSuperVisitorStatsMapper {

    /**
     * 기간 기준 요약 통계를 조회한다.
     */
    QrManageSuperVisitorDashboardStats selectDashboardStats(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("serviceCode") String serviceCode,
            @Param("menuCode") String menuCode
    );

    /**
     * 오늘 기준 통계를 조회한다.
     */
    QrManageSuperVisitorDashboardStats selectTodayStats(
            @Param("targetDate") String targetDate,
            @Param("serviceCode") String serviceCode,
            @Param("menuCode") String menuCode
    );

    /**
     * 일별 추이 통계를 조회한다.
     */
    List<QrManageSuperVisitorDailyStats> selectDailyStats(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("serviceCode") String serviceCode,
            @Param("menuCode") String menuCode
    );

    /**
     * 서비스/메뉴별 요약 통계를 조회한다.
     */
    List<QrManageSuperVisitorMenuStats> selectMenuStats(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("serviceCode") String serviceCode,
            @Param("menuCode") String menuCode
    );

    /**
     * 라우트 상세 통계를 조회한다.
     */
    List<QrManageSuperVisitorRouteStats> selectRouteStats(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("serviceCode") String serviceCode,
            @Param("menuCode") String menuCode
    );

    /**
     * 디바이스 유형별 통계를 조회한다.
     */
    List<QrManageSuperVisitorDeviceStats> selectDeviceStats(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("serviceCode") String serviceCode,
            @Param("menuCode") String menuCode
    );
}
