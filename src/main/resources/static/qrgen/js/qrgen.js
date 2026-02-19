/**
 * QR Generator JavaScript
 */
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('qrgen-form');
    const generateBtn = document.getElementById('generateBtn');
    const downloadBtn = document.getElementById('downloadBtn');
    const contentTypeSelect = document.getElementById('contentType');
    const contentValueInput = document.getElementById('contentValue');
    const previewImg = document.getElementById('qrgen-preview-img');
    const previewPlaceholder = document.getElementById('qrgen-preview-placeholder');

    let currentQrBlob = null;

    // ==========================================
    // 유형별 필드 전환
    // ==========================================

    /**
     * 선택된 QR 유형에 맞는 입력 필드만 표시
     */
    const qrgenShowFieldsForType = (type) => {
        document.querySelectorAll('.qrgen-dynamic-fields').forEach(el => {
            el.classList.remove('active');
        });
        const activeFields = document.querySelector(`.qrgen-fields-${type}`);
        if (activeFields) {
            activeFields.classList.add('active');
        }
    };

    // ==========================================
    // 값 조합 (개별 필드 → contentValue)
    // ==========================================

    /**
     * 현재 유형의 개별 필드 값을 읽어 contentValue 문자열로 조합
     */
    const qrgenBuildContentValue = () => {
        const type = contentTypeSelect?.value || 'URL';

        switch (type) {
            case 'URL':
                return document.getElementById('qrgen-url')?.value?.trim() || '';

            case 'TEXT':
                return document.getElementById('qrgen-text')?.value?.trim() || '';

            case 'EMAIL':
                return document.getElementById('qrgen-email-addr')?.value?.trim() || '';

            case 'PHONE':
                return document.getElementById('qrgen-phone')?.value?.trim() || '';

            case 'WIFI': {
                const ssid = document.getElementById('qrgen-wifi-ssid')?.value?.trim() || '';
                const password = document.getElementById('qrgen-wifi-password')?.value?.trim() || '';
                const encryption = document.getElementById('qrgen-wifi-encryption')?.value || 'WPA';
                if (!ssid) return '';
                return `${ssid}:${password}:${encryption}`;
            }

            case 'GEO': {
                const lat = document.getElementById('qrgen-geo-lat')?.value?.trim() || '';
                const lng = document.getElementById('qrgen-geo-lng')?.value?.trim() || '';
                if (!lat || !lng) return '';
                return `${lat},${lng}`;
            }

            default:
                return '';
        }
    };

    // ==========================================
    // 히스토리 복원 (contentValue → 개별 필드)
    // ==========================================

    /**
     * 저장된 contentValue를 파싱하여 개별 필드에 분배
     */
    const qrgenParseContentValue = (type, value) => {
        if (!value) return;

        switch (type) {
            case 'URL': {
                const el = document.getElementById('qrgen-url');
                if (el) el.value = value;
                break;
            }

            case 'TEXT': {
                const el = document.getElementById('qrgen-text');
                if (el) el.value = value;
                break;
            }

            case 'EMAIL': {
                const addrEl = document.getElementById('qrgen-email-addr');
                if (addrEl) addrEl.value = value.split('?')[0] || '';
                break;
            }

            case 'PHONE': {
                const el = document.getElementById('qrgen-phone');
                if (el) el.value = value;
                break;
            }

            case 'WIFI': {
                const parts = value.split(':');
                const ssidEl = document.getElementById('qrgen-wifi-ssid');
                const passEl = document.getElementById('qrgen-wifi-password');
                const encEl = document.getElementById('qrgen-wifi-encryption');
                if (ssidEl) ssidEl.value = parts[0] || '';
                if (passEl) passEl.value = parts[1] || '';
                if (encEl && parts[2]) encEl.value = parts[2];
                break;
            }

            case 'GEO': {
                const [lat, lng] = value.split(',');
                const latEl = document.getElementById('qrgen-geo-lat');
                const lngEl = document.getElementById('qrgen-geo-lng');
                if (latEl) latEl.value = lat || '';
                if (lngEl) lngEl.value = lng || '';
                break;
            }
        }
    };

    // ==========================================
    // 실시간 미리보기
    // ==========================================

    /**
     * 실시간 미리보기 업데이트 (서버 미리보기 API 호출)
     */
    const qrgenUpdatePreview = () => {
        const value = qrgenBuildContentValue();
        if (!value) {
            if (previewImg) previewImg.style.display = 'none';
            if (previewPlaceholder) previewPlaceholder.style.display = '';
            return;
        }

        // hidden input에 조합된 값 동기화
        if (contentValueInput) contentValueInput.value = value;

        const params = new URLSearchParams({
            contentType: contentTypeSelect?.value || 'TEXT',
            contentValue: value,
            size: document.getElementById('size')?.value || '300',
            errorCorrection: document.getElementById('errorCorrection')?.value || 'M',
            foregroundColor: document.getElementById('foregroundColor')?.value || '#000000',
            backgroundColor: document.getElementById('backgroundColor')?.value || '#FFFFFF'
        });

        if (previewImg) {
            previewImg.src = '/qrgen/preview?' + params.toString();
            previewImg.style.display = '';
        }
        if (previewPlaceholder) previewPlaceholder.style.display = 'none';

        // 서버 생성 후 폼 수정 시: 다운로드 버튼 숨기고 blob 초기화
        if (currentQrBlob) {
            currentQrBlob = null;
            if (downloadBtn) downloadBtn.style.display = 'none';
        }
    };

    /**
     * 디바운스 유틸리티
     */
    const qrgenDebounce = (fn, delay) => {
        let timer = null;
        return (...args) => {
            clearTimeout(timer);
            timer = setTimeout(() => fn(...args), delay);
        };
    };

    const qrgenDebouncedPreview = qrgenDebounce(qrgenUpdatePreview, 300);
    const qrgenDebouncedColorPreview = qrgenDebounce(qrgenUpdatePreview, 50);

    // 미리보기 이미지 로드 실패 시 placeholder 표시
    if (previewImg) {
        previewImg.addEventListener('error', () => {
            previewImg.style.display = 'none';
            if (previewPlaceholder) previewPlaceholder.style.display = '';
        });
    }

    // ==========================================
    // 이벤트 바인딩
    // ==========================================

    // 동적 필드 입력 이벤트: 디바운스 미리보기
    document.querySelectorAll('.qrgen-dynamic-fields input, .qrgen-dynamic-fields textarea').forEach(el => {
        el.addEventListener('input', qrgenDebouncedPreview);
    });
    document.querySelectorAll('.qrgen-dynamic-fields select').forEach(el => {
        el.addEventListener('change', qrgenDebouncedPreview);
    });

    // 콘텐츠 타입 변경: 필드 전환 + 미리보기 반영
    if (contentTypeSelect) {
        contentTypeSelect.addEventListener('change', function() {
            qrgenShowFieldsForType(this.value);
            qrgenUpdatePreview();
        });
    }

    // 에러 보정 변경: 즉시 반영
    const errorCorrectionSelect = document.getElementById('errorCorrection');
    if (errorCorrectionSelect) {
        errorCorrectionSelect.addEventListener('change', qrgenUpdatePreview);
    }

    // 크기 변경: input 300ms 디바운스, change 즉시
    const sizeInput = document.getElementById('size');
    if (sizeInput) {
        sizeInput.addEventListener('input', qrgenDebouncedPreview);
        sizeInput.addEventListener('change', qrgenUpdatePreview);
    }

    // 전경색 변경: input 50ms 디바운스, change 즉시
    const foregroundColorInput = document.getElementById('foregroundColor');
    if (foregroundColorInput) {
        foregroundColorInput.addEventListener('input', qrgenDebouncedColorPreview);
        foregroundColorInput.addEventListener('change', qrgenUpdatePreview);
    }

    // 배경색 변경: input 50ms 디바운스, change 즉시
    const backgroundColorInput = document.getElementById('backgroundColor');
    if (backgroundColorInput) {
        backgroundColorInput.addEventListener('input', qrgenDebouncedColorPreview);
        backgroundColorInput.addEventListener('change', qrgenUpdatePreview);
    }

    // ==========================================
    // QR 코드 생성 (서버 API 호출)
    // ==========================================

    if (generateBtn) {
        generateBtn.addEventListener('click', async function() {
            const builtValue = qrgenBuildContentValue();
            if (!builtValue) {
                alert('내용을 입력해주세요.');
                return;
            }

            // hidden input에 조합된 값 동기화
            if (contentValueInput) contentValueInput.value = builtValue;

            const requestData = {
                contentType: contentTypeSelect.value,
                contentValue: builtValue,
                size: parseInt(document.getElementById('size').value) || 300,
                errorCorrection: document.getElementById('errorCorrection').value,
                foregroundColor: document.getElementById('foregroundColor').value,
                backgroundColor: document.getElementById('backgroundColor').value,
                title: document.getElementById('title')?.value || null,
                memo: null
            };

            generateBtn.disabled = true;
            generateBtn.textContent = '생성 중...';

            try {
                const response = await fetch('/qrgen/generate', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(requestData)
                });

                if (response.status === 429) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || '일일 QR 생성 한도를 초과했습니다.');
                }

                if (!response.ok) {
                    throw new Error('QR 코드 생성에 실패했습니다.');
                }

                const blob = await response.blob();
                currentQrBlob = blob;

                // 서버 생성 이미지로 미리보기 교체
                const imageUrl = URL.createObjectURL(blob);
                if (previewImg) {
                    previewImg.src = imageUrl;
                    previewImg.style.display = '';
                }
                if (previewPlaceholder) previewPlaceholder.style.display = 'none';

                // 다운로드 버튼 표시
                downloadBtn.style.display = 'inline-block';

            } catch (error) {
                console.error('Error:', error);
                alert(error.message || 'QR 코드 생성 중 오류가 발생했습니다.');
            } finally {
                generateBtn.disabled = false;
                generateBtn.textContent = 'QR 코드 생성';
            }
        });
    }

    // QR 코드 다운로드
    if (downloadBtn) {
        downloadBtn.addEventListener('click', function() {
            if (!currentQrBlob) {
                alert('먼저 QR 코드를 생성해주세요.');
                return;
            }

            const url = URL.createObjectURL(currentQrBlob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'qrcode_' + Date.now() + '.png';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);
        });
    }

    // 폼 엔터키 제출 방지
    if (form) {
        form.addEventListener('keypress', function(e) {
            if (e.key === 'Enter' && e.target.tagName !== 'TEXTAREA') {
                e.preventDefault();
                generateBtn.click();
            }
        });
    }

    // ==========================================
    // 초기화
    // ==========================================

    // 히스토리 복원 또는 초기 필드 표시
    const historyType = document.getElementById('historyContentType')?.value;
    const historyValue = document.getElementById('historyContentValue')?.value;

    if (historyType && historyValue) {
        // 히스토리에서 복원
        qrgenShowFieldsForType(historyType);
        qrgenParseContentValue(historyType, historyValue);
        qrgenUpdatePreview();
    } else {
        // 초기 유형에 맞는 필드 표시
        qrgenShowFieldsForType(contentTypeSelect?.value || 'URL');
    }
});
