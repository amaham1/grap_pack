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

    // 콘텐츠 타입 변경 시 힌트 업데이트
    if (contentTypeSelect && contentHint) {
        contentTypeSelect.addEventListener('change', function() {
            const type = this.value;
            contentHint.textContent = hints[type] || '';
            contentValueInput.placeholder = hints[type] || '내용을 입력하세요';
        });

        // 초기 힌트 설정
        const initialType = contentTypeSelect.value;
        if (hints[initialType]) {
            contentHint.textContent = hints[initialType];
            contentValueInput.placeholder = hints[initialType];
        }
    }

    // QR 코드 생성
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

                if (!response.ok) {
                    throw new Error('QR 코드 생성에 실패했습니다.');
                }

                const blob = await response.blob();
                currentQrBlob = blob;

                // 미리보기 표시
                const imageUrl = URL.createObjectURL(blob);
                preview.innerHTML = `<img src="${imageUrl}" alt="Generated QR Code">`;

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
});
