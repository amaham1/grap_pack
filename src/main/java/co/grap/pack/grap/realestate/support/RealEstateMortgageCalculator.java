package co.grap.pack.grap.realestate.support;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 주택 매매 대출 계산기.
 */
public final class RealEstateMortgageCalculator {

    public static final Set<String> SUPPORTED_CATEGORIES = Set.of("apartment", "rowhouse", "detached-house");
    public static final String BUYER_PROFILE_FIRST_HOME = "firstHome";
    public static final String BUYER_PROFILE_NO_HOME = "noHome";
    public static final String BUYER_PROFILE_REPLACE_HOME = "replaceHome";
    public static final String BUYER_PROFILE_ADDITIONAL_HOME = "additionalHome";
    public static final String AREA_POLICY_JEJU_NON_REGULATED = "jejuNonRegulated";
    public static final String AREA_POLICY_METRO_REGULATED = "metroRegulated";

    public static final List<Map<String, String>> BUYER_PROFILES = List.of(
            Map.of("value", BUYER_PROFILE_FIRST_HOME, "label", "생애최초 주택 구입", "description", "처음 집을 사는 경우"),
            Map.of("value", BUYER_PROFILE_NO_HOME, "label", "무주택", "description", "현재 주택이 없는 경우"),
            Map.of("value", BUYER_PROFILE_REPLACE_HOME, "label", "1주택 처분 예정", "description", "기존 집을 팔고 갈아타는 경우"),
            Map.of("value", BUYER_PROFILE_ADDITIONAL_HOME, "label", "기존 주택 보유 / 추가 매수", "description", "추가로 주택을 매수하는 경우")
    );

    public static final List<Map<String, String>> AREA_POLICIES = List.of(
            Map.of("value", AREA_POLICY_JEJU_NON_REGULATED, "label", "제주/지방 비규제지역", "description", "현재 제주 기본 가정"),
            Map.of("value", AREA_POLICY_METRO_REGULATED, "label", "수도권 규제지역", "description", "강한 대출 규제 가정")
    );

    private RealEstateMortgageCalculator() {
    }

    public static boolean isSupportedProperty(String propertyCategory, String transactionType) {
        return "trade".equals(transactionType) && SUPPORTED_CATEGORIES.contains(propertyCategory);
    }

    public static Map<String, Object> createDefaultFormValues() {
        return Map.of(
                "cashAmount", 5000,
                "annualIncome", 4000,
                "existingMonthlyDebtPayment", 0,
                "interestRate", 4.2,
                "loanTermYears", 30,
                "dsrLimitRatio", 80,
                "buyerProfile", BUYER_PROFILE_NO_HOME,
                "areaPolicy", AREA_POLICY_JEJU_NON_REGULATED
        );
    }

    public static double getDefaultStressRate(String areaPolicy) {
        return AREA_POLICY_METRO_REGULATED.equals(areaPolicy) ? 3.0 : 0.75;
    }

    public static double getApplicableLtvRatio(String buyerProfile, String areaPolicy) {
        if (AREA_POLICY_METRO_REGULATED.equals(areaPolicy)) {
            if (BUYER_PROFILE_ADDITIONAL_HOME.equals(buyerProfile)) {
                return 0;
            }
            return 0.4;
        }

        if (BUYER_PROFILE_FIRST_HOME.equals(buyerProfile)) {
            return 0.8;
        }
        if (BUYER_PROFILE_ADDITIONAL_HOME.equals(buyerProfile)) {
            return 0.6;
        }
        return 0.7;
    }

    public static double getPurchaseLoanCapAmount(int propertyPrice, String areaPolicy) {
        if (!AREA_POLICY_METRO_REGULATED.equals(areaPolicy)) {
            return Double.POSITIVE_INFINITY;
        }
        if (propertyPrice <= 150000) {
            return 60000;
        }
        if (propertyPrice <= 250000) {
            return 40000;
        }
        return 20000;
    }

    public static MortgageScenario calculateScenario(MortgageInput input) {
        int propertyPrice = Math.max(0, input.getPropertyPrice());
        int cashAmount = Math.max(0, input.getCashAmount());
        int annualIncome = Math.max(0, input.getAnnualIncome());
        int existingMonthlyDebtPayment = Math.max(0, input.getExistingMonthlyDebtPayment());
        double interestRate = clamp(input.getInterestRate(), 0, 20);
        int loanTermYears = (int) clamp(input.getLoanTermYears(), 1, 40);
        double dsrLimitRatio = clamp(input.getDsrLimitRatio(), 1, 100);
        double stressRate = Math.max(0, input.getStressRate());

        int requiredLoanAmount = Math.max(propertyPrice - cashAmount, 0);
        double requiredLoanToValueRatio = propertyPrice > 0 ? ((double) requiredLoanAmount / propertyPrice) * 100 : 0;
        double applicableLtvRatio = getApplicableLtvRatio(input.getBuyerProfile(), input.getAreaPolicy());
        int maxByLtv = (int) Math.floor(propertyPrice * applicableLtvRatio);
        double purchaseLoanCapAmount = getPurchaseLoanCapAmount(propertyPrice, input.getAreaPolicy());
        double annualDsrBudget = annualIncome * (dsrLimitRatio / 100);
        double annualExistingDebtService = existingMonthlyDebtPayment * 12.0;
        double availableAnnualDebtService = Math.max(annualDsrBudget - annualExistingDebtService, 0);
        double monthlyRepaymentBudget = availableAnnualDebtService / 12.0;
        int maxByDsr = (int) Math.floor(calculatePrincipalByMonthlyBudget(
                monthlyRepaymentBudget,
                interestRate + stressRate,
                loanTermYears
        ));
        int maxAvailableLoan = Math.max(0, (int) Math.floor(Math.min(Math.min(maxByLtv, maxByDsr), purchaseLoanCapAmount)));
        int additionalCashNeeded = Math.max(propertyPrice - cashAmount - maxAvailableLoan, 0);
        double monthlyPaymentForRequiredLoan = calculateMonthlyPayment(requiredLoanAmount, interestRate, loanTermYears);
        double monthlyPaymentForMaxLoan = calculateMonthlyPayment(maxAvailableLoan, interestRate, loanTermYears);
        double availableLoanToValueRatio = propertyPrice > 0 ? ((double) maxAvailableLoan / propertyPrice) * 100 : 0;
        boolean isAffordable = requiredLoanAmount <= maxAvailableLoan;
        String constraint = identifyConstraint(
                maxByLtv,
                maxByDsr,
                purchaseLoanCapAmount,
                maxAvailableLoan,
                input.getAreaPolicy(),
                input.getBuyerProfile()
        );

        return MortgageScenario.builder()
                .propertyPrice(propertyPrice)
                .cashAmount(cashAmount)
                .annualIncome(annualIncome)
                .existingMonthlyDebtPayment(existingMonthlyDebtPayment)
                .interestRate(interestRate)
                .loanTermYears(loanTermYears)
                .dsrLimitRatio(dsrLimitRatio)
                .stressRate(stressRate)
                .buyerProfile(input.getBuyerProfile())
                .areaPolicy(input.getAreaPolicy())
                .applicableLtvRatio(applicableLtvRatio)
                .requiredLoanAmount(requiredLoanAmount)
                .requiredLoanToValueRatio(requiredLoanToValueRatio)
                .maxByLtv(maxByLtv)
                .maxByDsr(maxByDsr)
                .purchaseLoanCapAmount(purchaseLoanCapAmount)
                .maxAvailableLoan(maxAvailableLoan)
                .availableLoanToValueRatio(availableLoanToValueRatio)
                .annualDsrBudget(annualDsrBudget)
                .availableAnnualDebtService(availableAnnualDebtService)
                .monthlyRepaymentBudget(monthlyRepaymentBudget)
                .monthlyPaymentForRequiredLoan(monthlyPaymentForRequiredLoan)
                .monthlyPaymentForMaxLoan(monthlyPaymentForMaxLoan)
                .additionalCashNeeded(additionalCashNeeded)
                .affordable(isAffordable)
                .constraint(constraint)
                .build();
    }

    private static double calculatePrincipalByMonthlyBudget(double monthlyBudget, double annualInterestRate, int loanTermYears) {
        double normalizedBudget = Math.max(0, monthlyBudget);
        double normalizedRate = Math.max(0, annualInterestRate) / 100 / 12;
        int normalizedMonths = Math.max(1, loanTermYears * 12);

        if (normalizedBudget == 0) {
            return 0;
        }
        if (normalizedRate == 0) {
            return normalizedBudget * normalizedMonths;
        }
        return normalizedBudget * ((1 - Math.pow(1 + normalizedRate, -normalizedMonths)) / normalizedRate);
    }

    private static double calculateMonthlyPayment(double principal, double annualInterestRate, int loanTermYears) {
        double normalizedPrincipal = Math.max(0, principal);
        double normalizedRate = Math.max(0, annualInterestRate) / 100 / 12;
        int normalizedMonths = Math.max(1, loanTermYears * 12);

        if (normalizedPrincipal == 0) {
            return 0;
        }
        if (normalizedRate == 0) {
            return normalizedPrincipal / normalizedMonths;
        }

        double monthlyFactor = (normalizedRate * Math.pow(1 + normalizedRate, normalizedMonths))
                / (Math.pow(1 + normalizedRate, normalizedMonths) - 1);
        return normalizedPrincipal * monthlyFactor;
    }

    private static String identifyConstraint(
            int maxByLtv,
            int maxByDsr,
            double purchaseLoanCapAmount,
            int maxAvailableLoan,
            String areaPolicy,
            String buyerProfile
    ) {
        if (maxAvailableLoan <= 0 && AREA_POLICY_METRO_REGULATED.equals(areaPolicy) && BUYER_PROFILE_ADDITIONAL_HOME.equals(buyerProfile)) {
            return "additionalPurchaseRestricted";
        }
        if (maxAvailableLoan == maxByDsr) {
            return "dsr";
        }
        if (Double.isFinite(purchaseLoanCapAmount) && maxAvailableLoan == (int) Math.floor(purchaseLoanCapAmount)) {
            return "policyCap";
        }
        if (maxAvailableLoan == maxByLtv) {
            return "ltv";
        }
        return "none";
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.min(Math.max(value, minimum), maximum);
    }

    @Getter
    @Builder
    public static class MortgageInput {
        private int propertyPrice;
        private int cashAmount;
        private int annualIncome;
        private int existingMonthlyDebtPayment;
        private double interestRate;
        private int loanTermYears;
        private double dsrLimitRatio;
        private String buyerProfile;
        private String areaPolicy;
        private double stressRate;
    }

    @Getter
    @Builder
    public static class MortgageScenario {
        private int propertyPrice;
        private int cashAmount;
        private int annualIncome;
        private int existingMonthlyDebtPayment;
        private double interestRate;
        private int loanTermYears;
        private double dsrLimitRatio;
        private double stressRate;
        private String buyerProfile;
        private String areaPolicy;
        private double applicableLtvRatio;
        private int requiredLoanAmount;
        private double requiredLoanToValueRatio;
        private int maxByLtv;
        private int maxByDsr;
        private double purchaseLoanCapAmount;
        private int maxAvailableLoan;
        private double availableLoanToValueRatio;
        private double annualDsrBudget;
        private double availableAnnualDebtService;
        private double monthlyRepaymentBudget;
        private double monthlyPaymentForRequiredLoan;
        private double monthlyPaymentForMaxLoan;
        private int additionalCashNeeded;
        private boolean affordable;
        private String constraint;
    }
}
