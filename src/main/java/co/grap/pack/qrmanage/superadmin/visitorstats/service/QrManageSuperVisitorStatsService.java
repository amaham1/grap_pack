package co.grap.pack.qrmanage.superadmin.visitorstats.service;

import co.grap.pack.common.visitor.model.PackVisitorDeviceType;
import co.grap.pack.common.visitor.model.PackVisitorMenuCode;
import co.grap.pack.common.visitor.model.PackVisitorServiceCode;
import co.grap.pack.qrmanage.superadmin.visitorstats.mapper.QrManageSuperVisitorStatsMapper;
import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorDashboardStats;
import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorDailyStats;
import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorDeviceStats;
import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorMenuStats;
import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorRouteStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 슈퍼 관리자 방문자 통계 서비스다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class QrManageSuperVisitorStatsService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final QrManageSuperVisitorStatsMapper visitorStatsMapper;

    /**
     * 요약 통계를 조회한다.
     */
    public QrManageSuperVisitorDashboardStats getDashboardStats(
            LocalDate startDate,
            LocalDate endDate,
            PackVisitorServiceCode serviceCode,
            PackVisitorMenuCode menuCode
    ) {
        QrManageSuperVisitorDashboardStats periodStats = visitorStatsMapper.selectDashboardStats(
                formatDate(startDate),
                formatDate(endDate),
                codeOf(serviceCode),
                codeOf(menuCode)
        );

        QrManageSuperVisitorDashboardStats todayStats = visitorStatsMapper.selectTodayStats(
                formatDate(LocalDate.now(KOREA_ZONE_ID)),
                codeOf(serviceCode),
                codeOf(menuCode)
        );

        return QrManageSuperVisitorDashboardStats.builder()
                .totalPv(orZero(periodStats != null ? periodStats.getTotalPv() : null))
                .totalUv(orZero(periodStats != null ? periodStats.getTotalUv() : null))
                .todayPv(orZero(todayStats != null ? todayStats.getTodayPv() : null))
                .todayUv(orZero(todayStats != null ? todayStats.getTodayUv() : null))
                .averageDurationSeconds(orZero(periodStats != null ? periodStats.getAverageDurationSeconds() : null))
                .build();
    }

    /**
     * 일별 추이를 조회한다.
     */
    public List<QrManageSuperVisitorDailyStats> getDailyStats(
            LocalDate startDate,
            LocalDate endDate,
            PackVisitorServiceCode serviceCode,
            PackVisitorMenuCode menuCode
    ) {
        List<QrManageSuperVisitorDailyStats> rawStats = visitorStatsMapper.selectDailyStats(
                formatDate(startDate),
                formatDate(endDate),
                codeOf(serviceCode),
                codeOf(menuCode)
        );

        Map<String, QrManageSuperVisitorDailyStats> statsByDate = rawStats.stream()
                .collect(Collectors.toMap(QrManageSuperVisitorDailyStats::getDate, Function.identity()));

        List<QrManageSuperVisitorDailyStats> result = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            String date = formatDate(currentDate);
            QrManageSuperVisitorDailyStats stat = statsByDate.get(date);
            if (stat == null) {
                stat = QrManageSuperVisitorDailyStats.builder()
                        .date(date)
                        .pv(0L)
                        .uv(0L)
                        .build();
            } else {
                stat.setPv(orZero(stat.getPv()));
                stat.setUv(orZero(stat.getUv()));
            }

            result.add(stat);
            currentDate = currentDate.plusDays(1);
        }

        return result;
    }

    /**
     * 서비스/메뉴 요약 통계를 조회한다.
     */
    public List<QrManageSuperVisitorMenuStats> getMenuStats(
            LocalDate startDate,
            LocalDate endDate,
            PackVisitorServiceCode serviceCode,
            PackVisitorMenuCode menuCode
    ) {
        return visitorStatsMapper.selectMenuStats(
                        formatDate(startDate),
                        formatDate(endDate),
                        codeOf(serviceCode),
                        codeOf(menuCode)
                ).stream()
                .peek(stat -> {
                    stat.setServiceDisplayName(resolveServiceDisplayName(stat.getServiceCode()));
                    stat.setMenuDisplayName(resolveMenuDisplayName(stat.getMenuCode()));
                    stat.setPv(orZero(stat.getPv()));
                    stat.setUv(orZero(stat.getUv()));
                    stat.setAverageDurationSeconds(orZero(stat.getAverageDurationSeconds()));
                    stat.setMobileRatio(orZero(stat.getMobileRatio()));
                })
                .toList();
    }

    /**
     * 라우트 상세 통계를 조회한다.
     */
    public List<QrManageSuperVisitorRouteStats> getRouteStats(
            LocalDate startDate,
            LocalDate endDate,
            PackVisitorServiceCode serviceCode,
            PackVisitorMenuCode menuCode
    ) {
        if (menuCode == null) {
            return List.of();
        }

        return visitorStatsMapper.selectRouteStats(
                        formatDate(startDate),
                        formatDate(endDate),
                        codeOf(serviceCode),
                        codeOf(menuCode)
                ).stream()
                .peek(stat -> {
                    stat.setPv(orZero(stat.getPv()));
                    stat.setUv(orZero(stat.getUv()));
                    stat.setAverageDurationSeconds(orZero(stat.getAverageDurationSeconds()));
                })
                .toList();
    }

    /**
     * 디바이스별 통계를 조회한다.
     */
    public List<QrManageSuperVisitorDeviceStats> getDeviceStats(
            LocalDate startDate,
            LocalDate endDate,
            PackVisitorServiceCode serviceCode,
            PackVisitorMenuCode menuCode
    ) {
        List<QrManageSuperVisitorDeviceStats> rawStats = visitorStatsMapper.selectDeviceStats(
                formatDate(startDate),
                formatDate(endDate),
                codeOf(serviceCode),
                codeOf(menuCode)
        );

        Map<PackVisitorDeviceType, QrManageSuperVisitorDeviceStats> statsByType = new EnumMap<>(PackVisitorDeviceType.class);
        for (QrManageSuperVisitorDeviceStats rawStat : rawStats) {
            PackVisitorDeviceType deviceType = parseDeviceType(rawStat.getDeviceType());
            statsByType.put(deviceType, rawStat);
        }

        long totalPv = rawStats.stream()
                .map(QrManageSuperVisitorDeviceStats::getPv)
                .filter(value -> value != null)
                .mapToLong(Long::longValue)
                .sum();

        List<QrManageSuperVisitorDeviceStats> result = new ArrayList<>();
        for (PackVisitorDeviceType deviceType : PackVisitorDeviceType.values()) {
            QrManageSuperVisitorDeviceStats rawStat = statsByType.get(deviceType);
            long pv = rawStat != null ? orZero(rawStat.getPv()) : 0L;
            double ratio = totalPv > 0 ? Math.round((pv * 1000.0) / totalPv) / 10.0 : 0.0;

            result.add(QrManageSuperVisitorDeviceStats.builder()
                    .deviceType(deviceType.name())
                    .deviceDisplayName(deviceType.getDisplayName())
                    .pv(pv)
                    .ratio(ratio)
                    .build());
        }

        return result;
    }

    private String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    private String codeOf(Enum<?> value) {
        return value != null ? value.name() : null;
    }

    private long orZero(Long value) {
        return value != null ? value : 0L;
    }

    private double orZero(Double value) {
        return value != null ? value : 0.0;
    }

    private String resolveServiceDisplayName(String serviceCode) {
        PackVisitorServiceCode parsed = PackVisitorServiceCode.fromCode(serviceCode);
        return parsed != null ? parsed.getDisplayName() : serviceCode;
    }

    private String resolveMenuDisplayName(String menuCode) {
        PackVisitorMenuCode parsed = PackVisitorMenuCode.fromCode(menuCode);
        return parsed != null ? parsed.getDisplayName() : menuCode;
    }

    private PackVisitorDeviceType parseDeviceType(String deviceType) {
        try {
            return PackVisitorDeviceType.valueOf(deviceType);
        } catch (Exception exception) {
            return PackVisitorDeviceType.UNKNOWN;
        }
    }
}
