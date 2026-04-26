package co.grap.pack.grap.realestate.support;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RealEstatePurchaseCostCalculatorTest {

    @Test
    void calculatesSimpleAcquisitionTaxForPriceUnderSixEok() {
        RealEstatePurchaseCostCalculator.PurchaseCostScenario scenario = calculate(60000);

        assertThat(scenario.getAcquisitionTaxRate()).isEqualTo(0.01);
        assertThat(scenario.getAcquisitionTaxAmount()).isEqualTo(600);
        assertThat(scenario.getBrokerageFeeAmount()).isEqualTo(240);
        assertThat(scenario.getTotalExtraCostAmount()).isEqualTo(920);
        assertThat(scenario.getTotalRequiredCashAmount()).isEqualTo(60920);
    }

    @Test
    void calculatesSimpleAcquisitionTaxForPriceBetweenSixAndNineEok() {
        RealEstatePurchaseCostCalculator.PurchaseCostScenario scenario = calculate(70000);

        assertThat(scenario.getAcquisitionTaxRate()).isEqualTo(0.02);
        assertThat(scenario.getAcquisitionTaxAmount()).isEqualTo(1400);
        assertThat(scenario.getBrokerageFeeAmount()).isEqualTo(280);
        assertThat(scenario.getTotalExtraCostAmount()).isEqualTo(1760);
    }

    @Test
    void calculatesSimpleAcquisitionTaxForPriceOverNineEok() {
        RealEstatePurchaseCostCalculator.PurchaseCostScenario scenario = calculate(100000);

        assertThat(scenario.getAcquisitionTaxRate()).isEqualTo(0.03);
        assertThat(scenario.getAcquisitionTaxAmount()).isEqualTo(3000);
        assertThat(scenario.getBrokerageFeeRate()).isEqualTo(0.005);
        assertThat(scenario.getBrokerageFeeAmount()).isEqualTo(500);
        assertThat(scenario.getTotalExtraCostAmount()).isEqualTo(3580);
    }

    private RealEstatePurchaseCostCalculator.PurchaseCostScenario calculate(int propertyPrice) {
        return RealEstatePurchaseCostCalculator.calculateScenario(
                RealEstatePurchaseCostCalculator.PurchaseCostInput.builder()
                        .propertyPrice(propertyPrice)
                        .legalServiceAmount(80)
                        .otherCostAmount(0)
                        .build()
        );
    }
}
