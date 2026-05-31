/**
 * QR 관리 시스템 - 고객용 JavaScript
 */

document.addEventListener('DOMContentLoaded', () => {
    // 카테고리 탭 클릭
    const categoryTabs = document.querySelectorAll('.qr-manage-category-tab');
    categoryTabs.forEach(tab => {
        tab.addEventListener('click', () => {
            // 활성 탭 변경
            categoryTabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');

            // 해당 카테고리 메뉴만 표시
            const categoryId = tab.dataset.categoryId;
            const menuItems = document.querySelectorAll('.qr-manage-menu-item');

            menuItems.forEach(item => {
                if (categoryId === 'all' || item.dataset.categoryId === categoryId) {
                    item.style.display = 'flex';
                } else {
                    item.style.display = 'none';
                }
            });
        });
    });

    // 이미지 레이지 로딩
    const lazyImages = document.querySelectorAll('img[data-src]');
    const imageObserver = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const img = entry.target;
                img.src = img.dataset.src;
                img.removeAttribute('data-src');
                observer.unobserve(img);
            }
        });
    });

    lazyImages.forEach(img => imageObserver.observe(img));

    console.log('✅ QR 관리 시스템 (고객용) 초기화 완료');
});
