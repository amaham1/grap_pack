package co.grap.pack.grap.realestate.support;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RealEstateMortgageCalculatorTest {

    @Test
    void regulatedAdditionalPurchaseCanBeBlockedCompletely() {
        RealEstateMortgageCalculator.MortgageScenario scenario = RealEstateMortgageCalculator.calculateScenario(
                RealEstateMortgageCalculator.MortgageInput.builder()
                        .propertyPrice(60000)
                        .cashAmount(5000)
                        .annualIncome(4000)
                        .existingMonthlyDebtPayment(0)
                        .interestRate(4.2)
                        .loanTermYears(30)
                        .dsrLimitRatio(80)
                        .buyerProfile(RealEstateMortgageCalculator.BUYER_PROFILE_ADDITIONAL_HOME)
                        .areaPolicy(RealEstateMortgageCalculator.AREA_POLICY_METRO_REGULATED)
                        .stressRate(RealEstateMortgageCalculator.getDefaultStressRate(RealEstateMortgageCalculator.AREA_POLICY_METRO_REGULATED))
                        .build()
        );

        assertThat(scenario.getMaxAvailableLoan()).isZero();
        assertThat(scenario.getConstraint()).isEqualTo("additionalPurchaseRestricted");
        assertThat(scenario.isAffordable()).isFalse();
    }

    @Test
    void firstHomeJejuScenarioUsesEightyPercentLtv() {
        RealEstateMortgageCalculator.MortgageScenario scenario = RealEstateMortgageCalculator.calculateScenario(
                RealEstateMortgageCalculator.MortgageInput.builder()
                        .propertyPrice(50000)
                        .cashAmount(5000)
                        .annualIncome(7000)
                        .existingMonthlyDebtPayment(0)
                        .interestRate(4.2)
                        .loanTermYears(30)
                        .dsrLimitRatio(80)
                        .buyerProfile(RealEstateMortgageCalculator.BUYER_PROFILE_FIRST_HOME)
                        .areaPolicy(RealEstateMortgageCalculator.AREA_POLICY_JEJU_NON_REGULATED)
                        .stressRate(RealEstateMortgageCalculator.getDefaultStressRate(RealEstateMortgageCalculator.AREA_POLICY_JEJU_NON_REGULATED))
                        .build()
        );

        assertThat(scenario.getApplicableLtvRatio()).isEqualTo(0.8);
        assertThat(scenario.getMaxByLtv()).isEqualTo(40000);
        assertThat(scenario.getMaxAvailableLoan()).isPositive();
    }
}
