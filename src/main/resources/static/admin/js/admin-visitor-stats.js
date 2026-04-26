(function () {
    function toNumber(value) {
        const parsedValue = Number(value);
        return Number.isFinite(parsedValue) ? parsedValue : 0;
    }

    function readDailyStats() {
        return Array.from(document.querySelectorAll("[data-admin-daily-stat]")).map((element) => ({
            date: element.dataset.date || "",
            pv: toNumber(element.dataset.pv),
            uv: toNumber(element.dataset.uv),
            humanPv: toNumber(element.dataset.humanPv),
            humanUv: toNumber(element.dataset.humanUv),
            botPv: toNumber(element.dataset.botPv),
            botUv: toNumber(element.dataset.botUv)
        }));
    }

    function showFallback(message) {
        const fallbackElement = document.getElementById("adminVisitorDailyChartFallback");
        const chartElement = document.getElementById("adminVisitorDailyChart");

        if (!fallbackElement) {
            return;
        }

        if (chartElement) {
            chartElement.hidden = true;
        }

        fallbackElement.textContent = message;
        fallbackElement.hidden = false;
    }

    function drawDailyChart() {
        const chartElement = document.getElementById("adminVisitorDailyChart");

        if (!chartElement) {
            return;
        }

        const dailyStats = readDailyStats();

        if (dailyStats.length === 0) {
            showFallback("집계 데이터가 없습니다.");
            return;
        }

        if (typeof Chart === "undefined") {
            showFallback("차트 라이브러리를 불러오지 못했습니다. 잠시 후 다시 확인하세요.");
            return;
        }

        new Chart(chartElement, {
            type: "bar",
            data: {
                labels: dailyStats.map((item) => item.date),
                datasets: [
                    {
                        type: "bar",
                        label: "일반 브라우저 요청 수",
                        data: dailyStats.map((item) => item.pv),
                        borderColor: "#2364aa",
                        backgroundColor: "rgba(35, 100, 170, 0.22)",
                        borderWidth: 1,
                        borderRadius: 4,
                        maxBarThickness: 28
                    },
                    {
                        type: "line",
                        label: "사람 추정 방문 수",
                        data: dailyStats.map((item) => item.humanPv),
                        borderColor: "#178a4d",
                        backgroundColor: "rgba(23, 138, 77, 0.14)",
                        borderWidth: 2,
                        pointRadius: 4,
                        pointHoverRadius: 6,
                        tension: 0.28,
                        fill: false
                    },
                    {
                        type: "line",
                        label: "일반 브라우저 세션 수",
                        data: dailyStats.map((item) => item.uv),
                        borderColor: "#667085",
                        backgroundColor: "rgba(102, 112, 133, 0.10)",
                        borderWidth: 2,
                        pointRadius: 2,
                        pointHoverRadius: 4,
                        tension: 0.28,
                        fill: false
                    },
                    {
                        type: "line",
                        label: "BOT 요청 수",
                        data: dailyStats.map((item) => item.botPv),
                        borderColor: "#b86b00",
                        backgroundColor: "rgba(184, 107, 0, 0.10)",
                        borderDash: [6, 4],
                        borderWidth: 2,
                        pointRadius: 2,
                        pointHoverRadius: 4,
                        tension: 0.28,
                        fill: false
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                interaction: {
                    mode: "index",
                    intersect: false
                },
                plugins: {
                    legend: {
                        position: "bottom",
                        labels: {
                            boxWidth: 12,
                            boxHeight: 12,
                            usePointStyle: true
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: (context) => `${context.dataset.label}: ${context.parsed.y.toLocaleString()}`
                        }
                    }
                },
                scales: {
                    x: {
                        grid: {
                            display: false
                        },
                        ticks: {
                            maxRotation: 0,
                            autoSkip: true,
                            maxTicksLimit: 10
                        }
                    },
                    y: {
                        beginAtZero: true,
                        ticks: {
                            precision: 0
                        }
                    }
                }
            }
        });
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", drawDailyChart);
    } else {
        drawDailyChart();
    }
})();
