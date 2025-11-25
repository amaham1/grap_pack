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
    // data-disable-all-buttons="true" 속성이 있으면 페이지의 모든 버튼 비활성화
    const disableAllButtons = form.dataset.disableAllButtons === 'true';
    const submitButtons = disableAllButtons
        ? document.querySelectorAll('button[type="submit"], input[type="submit"]')
        : form.querySelectorAll('button[type="submit"], input[type="submit"]');
    const originalTexts = [];

    submitButtons.forEach((btn, index) => {
        originalTexts[index] = btn.textContent || btn.value;
        btn.disabled = true;
        if (btn.tagName === 'BUTTON') {
            // 현재 form의 버튼이면 "처리 중...", 다른 form의 버튼이면 "대기 중..."
            const isCurrentFormButton = form.contains(btn);
            btn.innerHTML = isCurrentFormButton
                ? '<i class="bi bi-hourglass-split"></i> 처리 중...'
                : '<i class="bi bi-hourglass-split"></i> 대기 중...';
        }
        if (disableAllButtons) {
            btn.style.opacity = '0.5';
            btn.style.cursor = 'not-allowed';
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
 * ===================================================================
 * 외부 데이터 관리 - 노출여부 일괄 변경 기능
 * (축제/행사, 공연/전시, 복지서비스 관리 페이지 공통 사용)
 * ===================================================================
 */

/**
 * 외부 데이터 체크박스 전체 선택/해제
 * @param {HTMLInputElement} masterCheckbox - 전체 선택 체크박스
 * @param {string} itemCheckboxSelector - 개별 체크박스 셀렉터
 */
function externalDataToggleAll(masterCheckbox, itemCheckboxSelector) {
    const itemCheckboxes = document.querySelectorAll(itemCheckboxSelector);
    itemCheckboxes.forEach(checkbox => {
        checkbox.checked = masterCheckbox.checked;
    });
    externalDataUpdateBulkButtons();
}

/**
 * 외부 데이터에서 선택된 ID 수집
 * @param {string} checkboxSelector - 체크박스 셀렉터
 * @returns {Array<string>} 선택된 ID 배열
 */
function externalDataGetSelectedIds(checkboxSelector) {
    const selectedCheckboxes = document.querySelectorAll(`${checkboxSelector}:checked`);
    return Array.from(selectedCheckboxes).map(checkbox => checkbox.value);
}

/**
 * 외부 데이터 일괄 작업 버튼 활성화/비활성화
 */
function externalDataUpdateBulkButtons() {
    const selectedCount = document.querySelectorAll('.external-data-item-checkbox:checked').length;
    const bulkButtons = document.querySelectorAll('.external-data-bulk-btn');

    bulkButtons.forEach(btn => {
        btn.disabled = selectedCount === 0;
    });
}

/**
 * 외부 데이터 노출여부 일괄 변경
 * @param {string} apiUrl - API 엔드포인트 URL
 * @param {boolean} isShow - 노출 여부 (true: 노출, false: 비노출)
 */
async function externalDataBulkUpdateIsShow(apiUrl, isShow) {
    const selectedIds = externalDataGetSelectedIds('.external-data-item-checkbox');

    if (selectedIds.length === 0) {
        alert('항목을 선택해주세요.');
        return;
    }

    const action = isShow ? '노출' : '비노출';
    if (!confirm(`선택한 ${selectedIds.length}개 항목을 ${action} 처리하시겠습니까?`)) {
        return;
    }

    const csrf = getCsrfToken();

    try {
        const response = await fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrf.header]: csrf.token
            },
            body: JSON.stringify({
                ids: selectedIds,
                isShow: isShow
            })
        });

        if (response.ok) {
            alert(`${selectedIds.length}개 항목이 ${action} 처리되었습니다.`);
            window.location.reload();
        } else {
            const error = await response.text();
            alert(`처리 실패: ${error}`);
        }
    } catch (error) {
        console.error('External data bulk update error:', error);
        alert('처리 중 오류가 발생했습니다.');
    }
}

/**
 * 페이지 로드 시 초기화
 */
document.addEventListener('DOMContentLoaded', () => {
    initAjaxForms();

    // 외부 데이터 체크박스 변경 시 버튼 상태 업데이트
    document.querySelectorAll('.external-data-item-checkbox').forEach(checkbox => {
        checkbox.addEventListener('change', externalDataUpdateBulkButtons);
    });

    // 외부 데이터 일괄 작업 버튼 초기 상태 설정
    externalDataUpdateBulkButtons();
});
