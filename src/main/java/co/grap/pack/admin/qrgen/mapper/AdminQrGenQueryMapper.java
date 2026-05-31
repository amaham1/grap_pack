package co.grap.pack.admin.qrgen.mapper;

import co.grap.pack.admin.dashboard.model.AdminDashboardDailyCount;
import co.grap.pack.admin.qrgen.model.AdminQrGenTypeStat;
import co.grap.pack.qrgen.auth.model.QrGenUser;
import co.grap.pack.qrgen.generator.model.QrGenHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 통합 운영 포털 QRgen 조회 Mapper다.
 */
@Mapper
public interface AdminQrGenQueryMapper {

    /**
     * QRgen 사용자 수를 조회한다.
     */
    Long countUsers(@Param("keyword") String keyword, @Param("isActive") Boolean isActive);

    /**
     * QR 생성 이력 수를 조회한다.
     */
    Long countHistories(@Param("userId") Long userId, @Param("contentType") String contentType);

    /**
     * 오늘 QR 생성 수를 조회한다.
     */
    Long countTodayHistories();

    /**
     * QRgen 사용자 목록을 조회한다.
     */
    List<QrGenUser> selectUsers(
            @Param("keyword") String keyword,
            @Param("isActive") Boolean isActive,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    /**
     * QRgen 사용자 상세를 조회한다.
     */
    QrGenUser selectUserById(@Param("userId") Long userId);

    /**
     * QR 생성 이력 목록을 조회한다.
     */
    List<QrGenHistory> selectHistories(
            @Param("userId") Long userId,
            @Param("contentType") String contentType,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    /**
     * QR 생성 이력 상세를 조회한다.
     */
    QrGenHistory selectHistoryById(@Param("historyId") Long historyId);

    /**
     * 최근 일별 QR 생성 수를 조회한다.
     */
    List<AdminDashboardDailyCount> selectDailyHistoryCounts(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 콘텐츠 타입별 집계를 조회한다.
     */
    List<AdminQrGenTypeStat> selectTypeStats(@Param("userId") Long userId);
}
