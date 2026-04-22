package co.grap.pack.grap.realestate.support;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

/**
 * 부동산 화면 표시용 포맷터.
 */
public final class RealEstateDisplayUtils {

    private static final BigDecimal PYEONG_DIVISOR = new BigDecimal("3.305785");
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,##0.##");
    private static final DecimalFormat INTEGER_FORMAT = new DecimalFormat("#,##0");

    private RealEstateDisplayUtils() {
    }

    public static String getCategoryLabel(String propertyCategory) {
        if ("apartment".equals(propertyCategory)) {
            return "아파트";
        }
        if ("rowhouse".equals(propertyCategory)) {
            return "연립다세대";
        }
        if ("officetel".equals(propertyCategory)) {
            return "오피스텔";
        }
        if ("detached-house".equals(propertyCategory)) {
            return "단독/다가구";
        }
        if ("commercial".equals(propertyCategory)) {
            return "상업/업무용";
        }
        return "부동산";
    }

    public static String getTransactionLabel(String transactionType) {
        return "rent".equals(transactionType) ? "전월세" : "매매";
    }

    public static String getAmountLabel(String transactionType) {
        return "rent".equals(transactionType) ? "보증금" : "거래가";
    }

    public static String formatPriceLabel(Number value) {
        int normalized = value == null ? 0 : (int) Math.floor(value.doubleValue());
        if (normalized < 10000) {
            return INTEGER_FORMAT.format(normalized) + "만원";
        }

        int eok = normalized / 10000;
        int remainder = normalized % 10000;
        String remainderLabel = formatManwonRemainder(remainder);
        if (remainderLabel.isBlank()) {
            return INTEGER_FORMAT.format(eok) + "억원";
        }
        return INTEGER_FORMAT.format(eok) + "억 " + remainderLabel;
    }

    public static String formatMonthlyRentLabel(Number value) {
        int normalized = value == null ? 0 : (int) Math.floor(value.doubleValue());
        return INTEGER_FORMAT.format(normalized) + "만원";
    }

    public static BigDecimal toPyeong(BigDecimal areaM2) {
        if (areaM2 == null) {
            return BigDecimal.ZERO;
        }
        return areaM2.divide(PYEONG_DIVISOR, 2, RoundingMode.HALF_UP);
    }

    public static String formatAreaLabel(BigDecimal areaM2) {
        if (areaM2 == null || areaM2.compareTo(BigDecimal.ZERO) <= 0) {
            return "-";
        }
        return NUMBER_FORMAT.format(areaM2) + "㎡ (" + NUMBER_FORMAT.format(toPyeong(areaM2)) + "평)";
    }

    public static String formatDealDateLabel(LocalDate dealDate) {
        if (dealDate == null) {
            return "-";
        }
        return dealDate.getYear() + "년 " + dealDate.getMonthValue() + "월 " + dealDate.getDayOfMonth() + "일";
    }

    public static String formatYearMonthLabel(String yearMonth) {
        if (yearMonth == null || yearMonth.length() != 6) {
            return "-";
        }
        return yearMonth.substring(0, 4) + "년 " + Integer.parseInt(yearMonth.substring(4, 6)) + "월";
    }

    public static String formatFloorLabel(Number floor) {
        int normalized = floor == null ? 0 : floor.intValue();
        return normalized > 0 ? normalized + "층" : "-";
    }

    public static String formatBuildYearLabel(Number buildYear) {
        int normalized = buildYear == null ? 0 : buildYear.intValue();
        return normalized > 0 ? normalized + "년" : "-";
    }

    public static String formatPercent(double value) {
        return NUMBER_FORMAT.format(value) + "%";
    }

    private static String formatManwonRemainder(int value) {
        if (value <= 0) {
            return "";
        }
        if (value % 1000 == 0) {
            return INTEGER_FORMAT.format(value / 1000) + "천만원";
        }
        return INTEGER_FORMAT.format(value) + "만원";
    }
}
