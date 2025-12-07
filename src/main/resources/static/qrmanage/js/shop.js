/**
 * QR 관리 시스템 - 상점관리자 JavaScript
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

    // 이미지 미리보기
    const imageInputs = document.querySelectorAll('input[type="file"][data-preview]');
    imageInputs.forEach(input => {
        input.addEventListener('change', (e) => {
            const previewId = input.dataset.preview;
            const preview = document.getElementById(previewId);
            if (preview && input.files && input.files[0]) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    preview.src = e.target.result;
                    preview.style.display = 'block';
                };
                reader.readAsDataURL(input.files[0]);
            }
        });
    });

    // 가격 입력 포맷팅
    const priceInputs = document.querySelectorAll('input[data-type="price"]');
    priceInputs.forEach(input => {
        input.addEventListener('input', (e) => {
            let value = e.target.value.replace(/[^0-9]/g, '');
            e.target.value = value;
        });
    });

    // 드래그 정렬 기능
    initSortable();

    // 모바일 메뉴 토글
    initMobileMenu();

    // 알림 배지 실시간 업데이트
    updateNotificationBadge();

    console.log('✅ QR 관리 시스템 (상점관리자) 초기화 완료');
});

/**
 * 드래그 앤 드롭 정렬 기능 초기화
 */
function initSortable() {
    const sortableLists = document.querySelectorAll('.qr-manage-sortable-list');

    sortableLists.forEach(list => {
        let draggedItem = null;

        list.querySelectorAll('.qr-manage-sortable-item').forEach(item => {
            item.setAttribute('draggable', 'true');

            item.addEventListener('dragstart', (e) => {
                draggedItem = item;
                item.classList.add('dragging');
                e.dataTransfer.effectAllowed = 'move';
            });

            item.addEventListener('dragend', () => {
                item.classList.remove('dragging');
                draggedItem = null;
                list.querySelectorAll('.qr-manage-sortable-item').forEach(i => {
                    i.classList.remove('drag-over');
                });

                // 정렬 순서 저장
                saveSortOrder(list);
            });

            item.addEventListener('dragover', (e) => {
                e.preventDefault();
                if (item !== draggedItem) {
                    item.classList.add('drag-over');
                }
            });

            item.addEventListener('dragleave', () => {
                item.classList.remove('drag-over');
            });

            item.addEventListener('drop', (e) => {
                e.preventDefault();
                if (item !== draggedItem) {
                    const rect = item.getBoundingClientRect();
                    const midPoint = rect.top + rect.height / 2;

                    if (e.clientY < midPoint) {
                        item.parentNode.insertBefore(draggedItem, item);
                    } else {
                        item.parentNode.insertBefore(draggedItem, item.nextSibling);
                    }
                }
                item.classList.remove('drag-over');
            });
        });
    });
}

/**
 * 정렬 순서 저장
 */
function saveSortOrder(list) {
    const url = list.dataset.sortUrl;
    if (!url) return;

    const items = list.querySelectorAll('.qr-manage-sortable-item');
    const order = Array.from(items).map((item, index) => ({
        id: parseInt(item.dataset.id),
        sortOrder: index
    }));

    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(order)
    }).then(response => {
        if (response.ok) {
            console.log('✅ 정렬 순서 저장 완료');
        }
    }).catch(err => {
        console.error('정렬 순서 저장 실패:', err);
    });
}

/**
 * 모바일 메뉴 토글
 */
function initMobileMenu() {
    const menuToggle = document.querySelector('.qr-manage-menu-toggle');
    const mobileNav = document.querySelector('.qr-manage-mobile-nav');

    if (menuToggle && mobileNav) {
        menuToggle.addEventListener('click', () => {
            mobileNav.classList.toggle('active');
        });

        // 외부 클릭 시 메뉴 닫기
        document.addEventListener('click', (e) => {
            if (!menuToggle.contains(e.target) && !mobileNav.contains(e.target)) {
                mobileNav.classList.remove('active');
            }
        });
    }
}

/**
 * 알림 배지 업데이트
 */
function updateNotificationBadge() {
    const badge = document.querySelector('.qr-manage-notification-badge');

    // 5분마다 알림 수 확인
    setInterval(() => {
        fetch('/qr-manage/shop/admin/notification/unread-count')
            .then(res => res.json())
            .then(data => {
                if (badge) {
                    if (data.count > 0) {
                        badge.textContent = data.count;
                        badge.style.display = 'block';
                    } else {
                        badge.style.display = 'none';
                    }
                }
            })
            .catch(err => console.error('알림 확인 실패:', err));
    }, 300000); // 5분
}

/**
 * 로딩 스피너 표시
 */
function showLoading(container) {
    const loading = document.createElement('div');
    loading.className = 'qr-manage-loading';
    loading.innerHTML = '<div class="qr-manage-spinner"></div>';
    container.appendChild(loading);
    return loading;
}

/**
 * 로딩 스피너 제거
 */
function hideLoading(loading) {
    if (loading) {
        loading.remove();
    }
}

/**
 * 토스트 메시지 표시
 */
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `qr-manage-toast qr-manage-toast-${type}`;
    toast.textContent = message;
    toast.style.cssText = `
        position: fixed;
        bottom: 20px;
        right: 20px;
        padding: 15px 25px;
        background: ${type === 'success' ? '#27ae60' : '#e74c3c'};
        color: white;
        border-radius: 4px;
        z-index: 9999;
        animation: slideIn 0.3s ease;
    `;

    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}
