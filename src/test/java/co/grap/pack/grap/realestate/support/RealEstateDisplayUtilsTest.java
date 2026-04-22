package co.grap.pack.grap.realestate.support;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class RealEstateDisplayUtilsTest {

    @Test
    void formatPriceLabelUsesEokUnitForAmountsOverOneHundredMillionWon() {
        assertThat(RealEstateDisplayUtils.formatPriceLabel(7000)).isEqualTo("7,000만원");
        assertThat(RealEstateDisplayUtils.formatPriceLabel(10000)).isEqualTo("1억원");
        assertThat(RealEstateDisplayUtils.formatPriceLabel(11000)).isEqualTo("1억 1천만원");
        assertThat(RealEstateDisplayUtils.formatPriceLabel(11500)).isEqualTo("1억 1,500만원");
    }

    @Test
    void formattersCreateReadableAreaAndDateLabels() {
        assertThat(RealEstateDisplayUtils.formatAreaLabel(new BigDecimal("84.92"))).isEqualTo("84.92㎡ (25.69평)");
        assertThat(RealEstateDisplayUtils.formatDealDateLabel(LocalDate.of(2026, 4, 20))).isEqualTo("2026년 4월 20일");
        assertThat(RealEstateDisplayUtils.formatYearMonthLabel("202604")).isEqualTo("2026년 4월");
    }
}
