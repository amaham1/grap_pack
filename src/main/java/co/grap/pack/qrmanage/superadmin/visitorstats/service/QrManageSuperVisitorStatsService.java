package co.grap.pack.qrmanage.superadmin.visitorstats.service;

import co.grap.pack.common.visitor.model.PackVisitorDeviceType;
import co.grap.pack.common.visitor.model.PackVisitorMenuCode;
import co.grap.pack.common.visitor.model.PackVisitorServiceCode;
import co.grap.pack.qrmanage.superadmin.visitorstats.mapper.QrManageSuperVisitorStatsMapper;
import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorDashboardStats;
import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorDailyStats;
import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorDeviceStats;
import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorIpAccessLog;
import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorIpAccessLogPage;
import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorMenuStats;
import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorRouteStats;
import lombok.RequiredArgsConstructor;
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
public class QrManageSuperVisitorStatsService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final int DEFAULT_IP_ACCESS_LOG_PAGE_SIZE = 50;
    private static final int MAX_IP_ACCESS_LOG_PAGE_SIZE = 200;
    private static final int PAGE_LINK_COUNT = 5;

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
                .totalBotPv(orZero(periodStats != null ? periodStats.getTotalBotPv() : null))
                .totalBotUv(orZero(periodStats != null ? periodStats.getTotalBotUv() : null))
                .todayBotPv(orZero(todayStats != null ? todayStats.getTodayBotPv() : null))
                .todayBotUv(orZero(todayStats != null ? todayStats.getTodayBotUv() : null))
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
                        .botPv(0L)
                        .botUv(0L)
                        .build();
            } else {
                stat.setPv(orZero(stat.getPv()));
                stat.setUv(orZero(stat.getUv()));
                stat.setBotPv(orZero(stat.getBotPv()));
                stat.setBotUv(orZero(stat.getBotUv()));
            }

            result.add(stat);
            currentDate = currentDate.plusDays(1);
        }

        return result;
    }

    /**
     * 서비스와 메뉴 요약 통계를 조회한다.
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
     * 상세 경로 통계를 조회한다.
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
                .filter(stat -> parseDeviceType(stat.getDeviceType()) != PackVisitorDeviceType.BOT)
                .map(QrManageSuperVisitorDeviceStats::getPv)
                .filter(value -> value != null)
                .mapToLong(Long::longValue)
                .sum();

        List<QrManageSuperVisitorDeviceStats> result = new ArrayList<>();
        for (PackVisitorDeviceType deviceType : PackVisitorDeviceType.values()) {
            if (deviceType == PackVisitorDeviceType.BOT) {
                continue;
            }

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

    /**
     * IP 접속 기록을 페이지 단위로 조회한다.
     */
    public QrManageSuperVisitorIpAccessLogPage getIpAccessLogPage(
            LocalDate startDate,
            LocalDate endDate,
            PackVisitorServiceCode serviceCode,
            PackVisitorMenuCode menuCode,
            Integer page,
            Integer pageSize,
            boolean includeBots
    ) {
        int normalizedPageSize = normalizePageSize(pageSize);
        long totalCount = orZero(visitorStatsMapper.selectRecentIpAccessLogCount(
                formatDate(startDate),
                formatDate(endDate),
                codeOf(serviceCode),
                codeOf(menuCode),
                includeBots
        ));

        int totalPages = totalCount > 0 ? (int) Math.ceil(totalCount / (double) normalizedPageSize) : 1;
        int currentPage = normalizePageNumber(page, totalPages);
        int offset = (currentPage - 1) * normalizedPageSize;

        List<QrManageSuperVisitorIpAccessLog> items = totalCount > 0
                ? visitorStatsMapper.selectRecentIpAccessLogs(
                formatDate(startDate),
                formatDate(endDate),
                codeOf(serviceCode),
                codeOf(menuCode),
                includeBots,
                offset,
                normalizedPageSize
        ).stream()
                .peek(this::populateIpAccessLogDisplayValues)
                .toList()
                : List.of();

        int endPage = Math.min(totalPages, Math.max(PAGE_LINK_COUNT, currentPage + 2));
        int startPage = Math.max(1, endPage - PAGE_LINK_COUNT + 1);
        endPage = Math.min(totalPages, startPage + PAGE_LINK_COUNT - 1);

        return QrManageSuperVisitorIpAccessLogPage.builder()
                .items(items)
                .totalCount(totalCount)
                .currentPage(currentPage)
                .pageSize(normalizedPageSize)
                .totalPages(totalPages)
                .startPage(startPage)
                .endPage(endPage)
                .includeBots(includeBots)
                .build();
    }

    private void populateIpAccessLogDisplayValues(QrManageSuperVisitorIpAccessLog logEntry) {
        logEntry.setIpAddress(hasText(logEntry.getIpAddress()) ? logEntry.getIpAddress() : "-");
        logEntry.setServiceDisplayName(defaultDisplayName(resolveServiceDisplayName(logEntry.getServiceCode())));
        logEntry.setMenuDisplayName(defaultDisplayName(resolveMenuDisplayName(logEntry.getMenuCode())));
        logEntry.setRouteKey(hasText(logEntry.getRouteKey()) ? logEntry.getRouteKey() : "-");
        PackVisitorDeviceType deviceType = parseDeviceType(logEntry.getDeviceType());
        logEntry.setDeviceDisplayName(deviceType.getDisplayName());
        logEntry.setVisitorTypeDisplayName(deviceType == PackVisitorDeviceType.BOT ? "BOT" : "일반");
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return DEFAULT_IP_ACCESS_LOG_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_IP_ACCESS_LOG_PAGE_SIZE);
    }

    private int normalizePageNumber(Integer page, int totalPages) {
        int normalizedPage = page == null || page < 1 ? 1 : page;
        return Math.min(normalizedPage, totalPages);
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

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String defaultDisplayName(String value) {
        return hasText(value) ? value : "-";
    }
}
