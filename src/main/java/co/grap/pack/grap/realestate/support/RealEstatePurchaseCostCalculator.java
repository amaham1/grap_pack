package co.grap.pack.grap.realestate.support;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 주택 매매 총 필요 현금 간편 추정 계산기.
 */
public final class RealEstatePurchaseCostCalculator {

    private static final int SIX_EOK_MANWON = 60000;
    private static final int NINE_EOK_MANWON = 90000;
    private static final int DEFAULT_LEGAL_SERVICE_AMOUNT = 80;

    // 정책 근거 링크는 화면 안내와 추후 세율 점검 기준으로 함께 사용한다.
    public static final List<Map<String, String>> POLICY_REFERENCES = List.of(
            Map.of("label", "지방세법", "url", "https://law.go.kr/LSW/lsLinkCommonInfo.do?chrClsCd=010202&lsJoLnkSeq=1020081767"),
            Map.of("label", "공인중개사법 시행규칙 제20조", "url", "https://www.law.go.kr/lsLinkCommonInfo.do?chrClsCd=010202&lspttninfSeq=106774"),
            Map.of("label", "제주특별자치도 주택 중개보수 조례", "url", "https://www.law.go.kr/LSW/ordinInfoP.do?ordinSeq=1164448")
    );

    private RealEstatePurchaseCostCalculator() {
    }

    public static Map<String, Object> createDefaultFormValues() {
        return Map.of(
                "legalServiceAmount", DEFAULT_LEGAL_SERVICE_AMOUNT,
                "otherCostAmount", 0
        );
    }

    public static boolean isSupportedProperty(String propertyCategory, String transactionType) {
        return "trade".equals(transactionType) && RealEstateMortgageCalculator.SUPPORTED_CATEGORIES.contains(propertyCategory);
    }

    public static PurchaseCostScenario calculateScenario(PurchaseCostInput input) {
        int propertyPrice = Math.max(0, input.getPropertyPrice());
        int legalServiceAmount = Math.max(0, input.getLegalServiceAmount());
        int otherCostAmount = Math.max(0, input.getOtherCostAmount());
        double acquisitionTaxRate = getSimpleAcquisitionTaxRate(propertyPrice);
        BrokerageFee brokerageFee = calculateBrokerageFee(propertyPrice);
        int acquisitionTaxAmount = (int) Math.round(propertyPrice * acquisitionTaxRate);
        int totalExtraCostAmount = acquisitionTaxAmount + brokerageFee.getAmount() + legalServiceAmount + otherCostAmount;

        return PurchaseCostScenario.builder()
                .propertyPrice(propertyPrice)
                .acquisitionTaxRate(acquisitionTaxRate)
                .acquisitionTaxAmount(acquisitionTaxAmount)
                .brokerageFeeRate(brokerageFee.getRate())
                .brokerageFeeAmount(brokerageFee.getAmount())
                .legalServiceAmount(legalServiceAmount)
                .otherCostAmount(otherCostAmount)
                .totalExtraCostAmount(totalExtraCostAmount)
                .totalRequiredCashAmount(propertyPrice + totalExtraCostAmount)
                .build();
    }

    public static double getSimpleAcquisitionTaxRate(int propertyPrice) {
        if (propertyPrice <= SIX_EOK_MANWON) {
            return 0.01;
        }
        if (propertyPrice <= NINE_EOK_MANWON) {
            return 0.02;
        }
        return 0.03;
    }

    public static BrokerageFee calculateBrokerageFee(int propertyPrice) {
        if (propertyPrice < 5000) {
            return brokerage(propertyPrice, 0.006, 25);
        }
        if (propertyPrice < 20000) {
            return brokerage(propertyPrice, 0.005, 80);
        }
        if (propertyPrice < 90000) {
            return brokerage(propertyPrice, 0.004, 0);
        }
        if (propertyPrice < 120000) {
            return brokerage(propertyPrice, 0.005, 0);
        }
        if (propertyPrice < 150000) {
            return brokerage(propertyPrice, 0.006, 0);
        }
        return brokerage(propertyPrice, 0.007, 0);
    }

    private static BrokerageFee brokerage(int propertyPrice, double rate, int capAmount) {
        int amount = (int) Math.round(propertyPrice * rate);
        if (capAmount > 0) {
            amount = Math.min(amount, capAmount);
        }
        return BrokerageFee.builder()
                .rate(rate)
                .amount(amount)
                .capAmount(capAmount)
                .build();
    }

    @Getter
    @Builder
    public static class PurchaseCostInput {
        private int propertyPrice;
        private int legalServiceAmount;
        private int otherCostAmount;
    }

    @Getter
    @Builder
    public static class PurchaseCostScenario {
        private int propertyPrice;
        private double acquisitionTaxRate;
        private int acquisitionTaxAmount;
        private double brokerageFeeRate;
        private int brokerageFeeAmount;
        private int legalServiceAmount;
        private int otherCostAmount;
        private int totalExtraCostAmount;
        private int totalRequiredCashAmount;
    }

    @Getter
    @Builder
    public static class BrokerageFee {
        private double rate;
        private int amount;
        private int capAmount;
    }
}
