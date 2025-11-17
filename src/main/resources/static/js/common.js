/**
 * 공통 JavaScript - Form Ajax 처리
 */

/**
 * CSRF 토큰 가져오기
 */
function getCsrfToken() {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    return { token, header };
}

/**
 * Form 데이터를 Fetch API로 제출
 */
async function submitFormWithFetch(form, options = {}) {
    const method = (form.method || 'POST').toUpperCase();
    const action = form.action;
    const csrf = getCsrfToken();

    let fetchOptions = {
        method: method,
        headers: {}
    };

    // CSRF 토큰 추가
    if (csrf.token && csrf.header && method !== 'GET') {
        fetchOptions.headers[csrf.header] = csrf.token;
    }

    // GET 방식: URL 파라미터로 전달
    if (method === 'GET') {
        const formData = new FormData(form);
        const params = new URLSearchParams(formData);
        const url = params.toString() ? `${action}?${params}` : action;

        const response = await fetch(url, {
            method: 'GET',
            headers: fetchOptions.headers
        });

        if (response.redirected) {
            window.location.href = response.url;
            return;
        }

        const html = await response.text();
        // 전체 페이지 교체 (검색 결과)
        document.open();
        document.write(html);
        document.close();
        return;
    }

    // POST 방식: FormData로 전달
    const formData = new FormData(form);

    // CSRF 토큰을 body에도 추가
    if (csrf.token && method === 'POST') {
        formData.append('_csrf', csrf.token);
    }

    fetchOptions.body = formData;

    try {
        const response = await fetch(action, fetchOptions);

        // 리다이렉트 처리
        if (response.redirected) {
            window.location.href = response.url;
            return;
        }

        // 성공 처리
        if (response.ok) {
            // 성공 메시지가 있으면 alert 후 리로드
            if (form.dataset.successMessage) {
                alert(form.dataset.successMessage);
            }

            // 리로드 또는 리다이렉트
            if (form.dataset.redirectUrl) {
                window.location.href = form.dataset.redirectUrl;
            } else {
                window.location.reload();
            }
        } else {
            // 에러 처리
            const errorMessage = form.dataset.errorMessage || `오류가 발생했습니다 (${response.status})`;
            alert(errorMessage);
        }
    } catch (error) {
        console.error('Form submit error:', error);
        const errorMessage = form.dataset.errorMessage || '네트워크 오류가 발생했습니다.';
        alert(errorMessage);
    }
}

/**
 * Form submit 이벤트 핸들러
 */
async function handleFormSubmit(e) {
    e.preventDefault();

    const form = e.target;

    // confirm 다이얼로그 처리
    if (form.dataset.confirm) {
        if (!confirm(form.dataset.confirm)) {
            return;
        }
    }

    // 제출 버튼 비활성화 (중복 제출 방지)
    const submitButtons = form.querySelectorAll('button[type="submit"], input[type="submit"]');
    const originalTexts = [];

    submitButtons.forEach((btn, index) => {
        originalTexts[index] = btn.textContent || btn.value;
        btn.disabled = true;
        if (btn.tagName === 'BUTTON') {
            btn.innerHTML = '<i class="bi bi-hourglass-split"></i> 처리 중...';
        }
    });

    try {
        await submitFormWithFetch(form);
    } finally {
        // 버튼 복원 (페이지 리로드되지 않은 경우)
        submitButtons.forEach((btn, index) => {
            btn.disabled = false;
            if (btn.tagName === 'BUTTON') {
                btn.textContent = originalTexts[index];
            } else {
                btn.value = originalTexts[index];
            }
        });
    }
}

/**
 * Ajax Form 초기화
 */
function initAjaxForms() {
    // data-ajax="true" 속성을 가진 모든 form에 이벤트 리스너 등록
    document.querySelectorAll('form[data-ajax="true"]').forEach(form => {
        form.addEventListener('submit', handleFormSubmit);
    });

    console.log(`[common.js] ${document.querySelectorAll('form[data-ajax="true"]').length}개의 Ajax form 초기화 완료`);
}

/**
 * 페이지 로드 시 초기화
 */
document.addEventListener('DOMContentLoaded', () => {
    initAjaxForms();
});
