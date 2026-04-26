(function () {
    function safeNumber(value) {
        var parsed = Number(value);
        return Number.isFinite(parsed) ? parsed : 0;
    }

    function formatNumber(value, maximumFractionDigits) {
        return new Intl.NumberFormat('ko-KR', {
            maximumFractionDigits: maximumFractionDigits
        }).format(safeNumber(value));
    }

    function formatPriceLabel(value) {
        var normalized = Math.trunc(safeNumber(value));
        if (normalized < 10000) {
            return formatNumber(normalized, 0) + '만원';
        }

        var eok = Math.floor(normalized / 10000);
        var remainder = normalized % 10000;
        if (!remainder) {
            return formatNumber(eok, 0) + '억원';
        }
        if (remainder % 1000 === 0) {
            return formatNumber(eok, 0) + '억 ' + formatNumber(remainder / 1000, 0) + '천만원';
        }
        return formatNumber(eok, 0) + '억 ' + formatNumber(remainder, 0) + '만원';
    }

    function formatPercent(value) {
        return formatNumber(value, 2) + '%';
    }

    function calculatePrincipalByMonthlyBudget(monthlyBudget, annualInterestRate, loanTermYears) {
        var normalizedBudget = Math.max(0, safeNumber(monthlyBudget));
        var normalizedRate = Math.max(0, safeNumber(annualInterestRate)) / 100 / 12;
        var normalizedMonths = Math.max(1, Math.trunc(safeNumber(loanTermYears) * 12));

        if (!normalizedBudget) {
            return 0;
        }
        if (!normalizedRate) {
            return normalizedBudget * normalizedMonths;
        }
        return normalizedBudget * ((1 - Math.pow(1 + normalizedRate, -normalizedMonths)) / normalizedRate);
    }

    function calculateMonthlyPayment(principal, annualInterestRate, loanTermYears) {
        var normalizedPrincipal = Math.max(0, safeNumber(principal));
        var normalizedRate = Math.max(0, safeNumber(annualInterestRate)) / 100 / 12;
        var normalizedMonths = Math.max(1, Math.trunc(safeNumber(loanTermYears) * 12));

        if (!normalizedPrincipal) {
            return 0;
        }
        if (!normalizedRate) {
            return normalizedPrincipal / normalizedMonths;
        }

        var monthlyFactor = (normalizedRate * Math.pow(1 + normalizedRate, normalizedMonths))
            / (Math.pow(1 + normalizedRate, normalizedMonths) - 1);
        return normalizedPrincipal * monthlyFactor;
    }

    function getDefaultStressRate(areaPolicy) {
        return areaPolicy === 'metroRegulated' ? 3.0 : 0.75;
    }

    function getApplicableLtvRatio(buyerProfile, areaPolicy) {
        if (areaPolicy === 'metroRegulated') {
            if (buyerProfile === 'additionalHome') {
                return 0;
            }
            return 0.4;
        }

        if (buyerProfile === 'firstHome') {
            return 0.8;
        }
        if (buyerProfile === 'additionalHome') {
            return 0.6;
        }
        return 0.7;
    }

    function getPurchaseLoanCapAmount(propertyPrice, areaPolicy) {
        if (areaPolicy !== 'metroRegulated') {
            return Number.POSITIVE_INFINITY;
        }
        if (propertyPrice <= 150000) {
            return 60000;
        }
        if (propertyPrice <= 250000) {
            return 40000;
        }
        return 20000;
    }

    function getSimpleAcquisitionTaxRate(propertyPrice) {
        if (propertyPrice <= 60000) {
            return 0.01;
        }
        if (propertyPrice <= 90000) {
            return 0.02;
        }
        return 0.03;
    }

    function calculateBrokerageFee(propertyPrice) {
        if (propertyPrice < 5000) {
            return createBrokerageFee(propertyPrice, 0.006, 25);
        }
        if (propertyPrice < 20000) {
            return createBrokerageFee(propertyPrice, 0.005, 80);
        }
        if (propertyPrice < 90000) {
            return createBrokerageFee(propertyPrice, 0.004, 0);
        }
        if (propertyPrice < 120000) {
            return createBrokerageFee(propertyPrice, 0.005, 0);
        }
        if (propertyPrice < 150000) {
            return createBrokerageFee(propertyPrice, 0.006, 0);
        }
        return createBrokerageFee(propertyPrice, 0.007, 0);
    }

    function createBrokerageFee(propertyPrice, rate, capAmount) {
        var amount = Math.round(propertyPrice * rate);
        if (capAmount > 0) {
            amount = Math.min(amount, capAmount);
        }
        return {
            amount: amount,
            rate: rate
        };
    }

    function selectedLabel(selectElement) {
        var option = selectElement.options[selectElement.selectedIndex];
        return option ? option.text : '';
    }

    function calculateScenario(formElement) {
        var propertyPrice = safeNumber(formElement.dataset.propertyPrice);
        var cashAmount = safeNumber(formElement.elements.cashAmount.value);
        var annualIncome = safeNumber(formElement.elements.annualIncome.value);
        var existingMonthlyDebtPayment = safeNumber(formElement.elements.existingMonthlyDebtPayment.value);
        var interestRate = Math.min(Math.max(safeNumber(formElement.elements.interestRate.value), 0), 20);
        var loanTermYears = Math.min(Math.max(safeNumber(formElement.elements.loanTermYears.value), 1), 40);
        var dsrLimitRatio = Math.min(Math.max(safeNumber(formElement.elements.dsrLimitRatio.value), 1), 100);
        var buyerProfile = formElement.elements.buyerProfile.value;
        var areaPolicy = formElement.elements.areaPolicy.value;
        var stressRate = getDefaultStressRate(areaPolicy);
        var requiredLoanAmount = Math.max(propertyPrice - cashAmount, 0);
        var requiredLoanToValueRatio = propertyPrice ? (requiredLoanAmount / propertyPrice) * 100 : 0;
        var applicableLtvRatio = getApplicableLtvRatio(buyerProfile, areaPolicy);
        var maxByLtv = Math.floor(propertyPrice * applicableLtvRatio);
        var purchaseLoanCapAmount = getPurchaseLoanCapAmount(propertyPrice, areaPolicy);
        var annualDsrBudget = annualIncome * (dsrLimitRatio / 100);
        var annualExistingDebtService = existingMonthlyDebtPayment * 12;
        var availableAnnualDebtService = Math.max(annualDsrBudget - annualExistingDebtService, 0);
        var monthlyRepaymentBudget = availableAnnualDebtService / 12;
        var maxByDsr = Math.floor(calculatePrincipalByMonthlyBudget(monthlyRepaymentBudget, interestRate + stressRate, loanTermYears));
        var maxAvailableLoan = Math.max(0, Math.floor(Math.min(maxByLtv, maxByDsr, purchaseLoanCapAmount)));
        var additionalCashNeeded = Math.max(propertyPrice - cashAmount - maxAvailableLoan, 0);
        var monthlyPaymentForRequiredLoan = calculateMonthlyPayment(requiredLoanAmount, interestRate, loanTermYears);
        var monthlyPaymentForMaxLoan = calculateMonthlyPayment(maxAvailableLoan, interestRate, loanTermYears);
        var availableLoanToValueRatio = propertyPrice ? (maxAvailableLoan / propertyPrice) * 100 : 0;
        var affordable = requiredLoanAmount <= maxAvailableLoan;
        var constraint = 'none';

        if (maxAvailableLoan <= 0 && areaPolicy === 'metroRegulated' && buyerProfile === 'additionalHome') {
            constraint = 'additionalPurchaseRestricted';
        } else if (maxAvailableLoan === maxByDsr) {
            constraint = 'dsr';
        } else if (Number.isFinite(purchaseLoanCapAmount) && maxAvailableLoan === purchaseLoanCapAmount) {
            constraint = 'policyCap';
        } else if (maxAvailableLoan === maxByLtv) {
            constraint = 'ltv';
        }

        return {
            requiredLoanAmount: requiredLoanAmount,
            maxAvailableLoan: maxAvailableLoan,
            additionalCashNeeded: additionalCashNeeded,
            monthlyPaymentForMaxLoan: Math.round(monthlyPaymentForMaxLoan),
            applicableLtvRatio: applicableLtvRatio * 100,
            requiredLoanToValueRatio: requiredLoanToValueRatio,
            availableLoanToValueRatio: availableLoanToValueRatio,
            maxByLtv: maxByLtv,
            maxByDsr: maxByDsr,
            purchaseLoanCapAmount: purchaseLoanCapAmount,
            stressRate: stressRate,
            monthlyRepaymentBudget: Math.round(monthlyRepaymentBudget),
            monthlyPaymentForRequiredLoan: Math.round(monthlyPaymentForRequiredLoan),
            affordable: affordable,
            constraint: constraint,
            buyerProfileLabel: selectedLabel(formElement.elements.buyerProfile),
            areaPolicyLabel: selectedLabel(formElement.elements.areaPolicy)
        };
    }

    function renderMortgageScenario(formElement, scenario) {
        var outputs = {
            requiredLoanAmount: formatPriceLabel(scenario.requiredLoanAmount),
            maxAvailableLoan: formatPriceLabel(scenario.maxAvailableLoan),
            additionalCashNeeded: formatPriceLabel(scenario.additionalCashNeeded),
            monthlyPaymentForMaxLoan: formatPriceLabel(scenario.monthlyPaymentForMaxLoan),
            applicableLtvRatio: formatPercent(scenario.applicableLtvRatio),
            requiredLoanToValueRatio: formatPercent(scenario.requiredLoanToValueRatio),
            availableLoanToValueRatio: formatPercent(scenario.availableLoanToValueRatio),
            maxByLtv: formatPriceLabel(scenario.maxByLtv),
            maxByDsr: formatPriceLabel(scenario.maxByDsr),
            policyCapLabel: Number.isFinite(scenario.purchaseLoanCapAmount) ? formatPriceLabel(scenario.purchaseLoanCapAmount) : '상한 없음',
            buyerProfileLabel: scenario.buyerProfileLabel,
            areaPolicyLabel: scenario.areaPolicyLabel,
            stressRate: formatPercent(scenario.stressRate),
            monthlyRepaymentBudget: formatPriceLabel(scenario.monthlyRepaymentBudget),
            monthlyPaymentForRequiredLoan: formatPriceLabel(scenario.monthlyPaymentForRequiredLoan),
            constraintLabel: {
                additionalPurchaseRestricted: '규제지역 추가 매수 제한',
                ltv: 'LTV 한도',
                policyCap: '정책 대출 상한',
                dsr: 'DSR 한도',
                none: '제한 없음'
            }[scenario.constraint]
        };

        Object.keys(outputs).forEach(function (key) {
            var element = document.querySelector('[data-mortgage-output="' + key + '"]');
            if (element) {
                element.textContent = outputs[key];
            }
        });

        var statusCard = document.getElementById('mortgage-status-card');
        var statusEyebrow = document.getElementById('mortgage-status-eyebrow');
        var statusTitle = document.getElementById('mortgage-status-title');
        var statusMessage = document.getElementById('mortgage-status-message');

        if (!statusCard || !statusEyebrow || !statusTitle || !statusMessage) {
            return;
        }

        if (!scenario.requiredLoanAmount) {
            statusEyebrow.textContent = '현금 충분';
            statusTitle.textContent = '현재 현금만으로도 매수가 가능합니다.';
            statusMessage.textContent = '보유 현금이 매매가 이상이라 대출 없이도 매수 가능한 상태입니다.';
            statusCard.classList.add('is-success');
            statusCard.classList.remove('is-warning');
        } else if (scenario.affordable) {
            statusEyebrow.textContent = '가능성';
            statusTitle.textContent = '현재 조건으로 매수 가능성이 있습니다.';
            statusMessage.textContent = 'LTV, DSR, 정책 상한을 반영한 현재 계산상 매수가 가능합니다.';
            statusCard.classList.add('is-success');
            statusCard.classList.remove('is-warning');
        } else {
            statusEyebrow.textContent = '현금 부족';
            statusTitle.textContent = '추가 현금이 더 필요합니다.';
            statusMessage.textContent = '현재 조건에서는 추가 현금 ' + formatPriceLabel(scenario.additionalCashNeeded) + ' 정도가 더 필요합니다.';
            statusCard.classList.add('is-warning');
            statusCard.classList.remove('is-success');
        }
    }

    function initMortgageCalculator() {
        var formElement = document.getElementById('mortgage-calculator-form');
        if (!formElement) {
            return;
        }

        function update() {
            renderMortgageScenario(formElement, calculateScenario(formElement));
        }

        formElement.addEventListener('input', update);
        formElement.addEventListener('change', update);
        update();
    }

    function calculatePurchaseCostScenario(formElement) {
        var propertyPrice = Math.max(0, safeNumber(formElement.dataset.propertyPrice));
        var legalServiceAmount = Math.max(0, safeNumber(formElement.elements.legalServiceAmount.value));
        var otherCostAmount = Math.max(0, safeNumber(formElement.elements.otherCostAmount.value));
        var acquisitionTaxRate = getSimpleAcquisitionTaxRate(propertyPrice);
        var acquisitionTaxAmount = Math.round(propertyPrice * acquisitionTaxRate);
        var brokerageFee = calculateBrokerageFee(propertyPrice);
        var totalExtraCostAmount = acquisitionTaxAmount + brokerageFee.amount + legalServiceAmount + otherCostAmount;

        return {
            propertyPrice: propertyPrice,
            acquisitionTaxRate: acquisitionTaxRate * 100,
            acquisitionTaxAmount: acquisitionTaxAmount,
            brokerageFeeRate: brokerageFee.rate * 100,
            brokerageFeeAmount: brokerageFee.amount,
            legalServiceAmount: legalServiceAmount,
            otherCostAmount: otherCostAmount,
            totalExtraCostAmount: totalExtraCostAmount,
            totalRequiredCashAmount: propertyPrice + totalExtraCostAmount
        };
    }

    function renderPurchaseCostScenario(scenario) {
        var outputs = {
            propertyPrice: formatPriceLabel(scenario.propertyPrice),
            acquisitionTaxAmount: formatPriceLabel(scenario.acquisitionTaxAmount),
            acquisitionTaxRate: formatPercent(scenario.acquisitionTaxRate),
            brokerageFeeAmount: formatPriceLabel(scenario.brokerageFeeAmount),
            brokerageFeeRate: formatPercent(scenario.brokerageFeeRate),
            legalServiceAmount: formatPriceLabel(scenario.legalServiceAmount),
            otherCostAmount: formatPriceLabel(scenario.otherCostAmount),
            totalExtraCostAmount: formatPriceLabel(scenario.totalExtraCostAmount),
            totalRequiredCashAmount: formatPriceLabel(scenario.totalRequiredCashAmount)
        };

        Object.keys(outputs).forEach(function (key) {
            var element = document.querySelector('[data-purchase-cost-output="' + key + '"]');
            if (element) {
                element.textContent = outputs[key];
            }
        });
    }

    function initPurchaseCostCalculator() {
        var formElement = document.getElementById('purchase-cost-form');
        if (!formElement) {
            return;
        }

        function update() {
            renderPurchaseCostScenario(calculatePurchaseCostScenario(formElement));
        }

        formElement.addEventListener('input', update);
        formElement.addEventListener('change', update);
        update();
    }

    function initPriceChart() {
        var canvas = document.getElementById('real-estate-price-chart');
        var scriptElement = document.getElementById('real-estate-price-series');
        if (!canvas || !scriptElement || !window.Chart) {
            return;
        }

        var priceSeries;
        try {
            priceSeries = JSON.parse(scriptElement.textContent || '[]');
        } catch (error) {
            return;
        }

        if (!Array.isArray(priceSeries) || !priceSeries.length) {
            return;
        }

        var context = canvas.getContext('2d');
        var gradient = context.createLinearGradient(0, 0, 0, 320);
        gradient.addColorStop(0, 'rgba(49, 130, 246, 0.24)');
        gradient.addColorStop(1, 'rgba(49, 130, 246, 0.02)');

        new window.Chart(context, {
            type: 'line',
            data: {
                labels: priceSeries.map(function (item) { return item.label; }),
                datasets: [
                    {
                        label: '월별 평균 시세',
                        data: priceSeries.map(function (item) { return item.value; }),
                        borderColor: '#3182f6',
                        backgroundColor: gradient,
                        fill: true,
                        tension: 0.35,
                        borderWidth: 3,
                        pointRadius: 3,
                        pointHoverRadius: 5,
                        pointBackgroundColor: '#ffffff',
                        pointBorderColor: '#3182f6',
                        pointBorderWidth: 2
                    },
                    {
                        label: '월별 최저가',
                        data: priceSeries.map(function (item) { return item.minAmount; }),
                        borderColor: '#16a34a',
                        backgroundColor: 'transparent',
                        borderDash: [6, 6],
                        tension: 0.25,
                        borderWidth: 2,
                        pointRadius: 2,
                        pointHoverRadius: 4
                    },
                    {
                        label: '월별 최고가',
                        data: priceSeries.map(function (item) { return item.maxAmount; }),
                        borderColor: '#f97316',
                        backgroundColor: 'transparent',
                        borderDash: [4, 4],
                        tension: 0.25,
                        borderWidth: 2,
                        pointRadius: 2,
                        pointHoverRadius: 4
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: true },
                    tooltip: {
                        callbacks: {
                            label: function (contextValue) {
                                return contextValue.dataset.label + ': ' + formatPriceLabel(contextValue.parsed.y);
                            },
                            afterBody: function (items) {
                                if (!items.length) {
                                    return '';
                                }
                                var item = priceSeries[items[0].dataIndex];
                                return '거래 ' + formatNumber(item.transactionCount || 0, 0) + '건';
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        grid: { display: false }
                    },
                    y: {
                        ticks: {
                            callback: function (value) {
                                return formatPriceLabel(value);
                            }
                        },
                        grid: {
                            color: '#eef1f4'
                        }
                    }
                }
            }
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        initPriceChart();
        initMortgageCalculator();
        initPurchaseCostCalculator();
    });
}());
