/**
 * QR 관리 시스템 - 최고관리자 JavaScript
 */

document.addEventListener('DOMContentLoaded', () => {
    // CSRF 토큰 설정
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

    // Fetch 기본 설정
    const defaultHeaders = {
        'Content-Type': 'application/json'
    };

    if (csrfToken && csrfHeader) {
        defaultHeaders[csrfHeader] = csrfToken;
    }

    // 공통 fetch 함수
    window.qrManageFetch = async (url, options = {}) => {
        const config = {
            ...options,
            headers: {
                ...defaultHeaders,
                ...options.headers
            }
        };

        const response = await fetch(url, config);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    };

    // 확인 모달
    window.qrManageConfirm = (message) => {
        return confirm(message);
    };

    // 알림 메시지 자동 숨기기
    const alerts = document.querySelectorAll('.qr-manage-alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // 테이블 행 클릭 이벤트
    const tableRows = document.querySelectorAll('.qr-manage-table tr[data-href]');
    tableRows.forEach(row => {
        row.style.cursor = 'pointer';
        row.addEventListener('click', (e) => {
            if (e.target.tagName !== 'BUTTON' && e.target.tagName !== 'A') {
                window.location.href = row.dataset.href;
            }
        });
    });

    console.log('✅ QR 관리 시스템 (최고관리자) 초기화 완료');
});
