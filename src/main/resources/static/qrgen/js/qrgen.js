/**
 * QR Generator JavaScript
 */
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('qrgen-form');
    const generateBtn = document.getElementById('generateBtn');
    const downloadBtn = document.getElementById('downloadBtn');
    const preview = document.getElementById('qrgen-preview');
    const contentTypeSelect = document.getElementById('contentType');
    const contentValueInput = document.getElementById('contentValue');
    const contentHint = document.getElementById('contentHint');
    const previewImg = document.getElementById('qrgen-preview-img');
    const previewPlaceholder = document.getElementById('qrgen-preview-placeholder');

    let currentQrBlob = null;

    // 콘텐츠 타입별 힌트
    const hints = {
        'URL': 'https://example.com',
        'TEXT': '원하는 텍스트를 입력하세요',
        'EMAIL': 'example@email.com',
        'PHONE': '+82-10-1234-5678',
        'SMS': '+82-10-1234-5678',
        'WIFI': 'SSID:비밀번호:WPA (예: MyWiFi:password123:WPA)',
        'VCARD': 'BEGIN:VCARD\\nVERSION:3.0\\nN:홍길동\\nTEL:010-1234-5678\\nEND:VCARD',
        'GEO': '위도,경도 (예: 37.5665,126.9780)'
    };

    /**
     * 실시간 미리보기 업데이트 (서버 미리보기 API 호출)
     */
    const qrgenUpdatePreview = () => {
        const value = contentValueInput?.value?.trim();
        if (!value) {
            if (previewImg) previewImg.style.display = 'none';
            if (previewPlaceholder) previewPlaceholder.style.display = '';
            return;
        }

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

    // --- 이벤트 바인딩 ---

    // textarea 입력: 300ms 디바운스
    if (contentValueInput) {
        contentValueInput.addEventListener('input', qrgenDebouncedPreview);
    }

    // 콘텐츠 타입 변경: 힌트 업데이트 + 즉시 미리보기 반영
    if (contentTypeSelect && contentHint) {
        contentTypeSelect.addEventListener('change', function() {
            const type = this.value;
            contentHint.textContent = hints[type] || '';
            contentValueInput.placeholder = hints[type] || '내용을 입력하세요';
            qrgenUpdatePreview();
        });

        // 초기 힌트 설정
        const initialType = contentTypeSelect.value;
        if (hints[initialType]) {
            contentHint.textContent = hints[initialType];
            contentValueInput.placeholder = hints[initialType];
        }
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

    // --- QR 코드 생성 (서버 API 호출 + 히스토리 저장 + Rate Limit) ---
    if (generateBtn) {
        generateBtn.addEventListener('click', async function() {
            if (!contentValueInput.value.trim()) {
                alert('내용을 입력해주세요.');
                contentValueInput.focus();
                return;
            }

            const requestData = {
                contentType: document.getElementById('contentType').value,
                contentValue: document.getElementById('contentValue').value,
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

    // 초기 미리보기 (히스토리에서 값이 미리 채워진 경우)
    if (contentValueInput?.value?.trim()) {
        qrgenUpdatePreview();
    }
});
