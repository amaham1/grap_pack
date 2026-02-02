/**
 * QRgen 방문자 추적 JavaScript
 * 체류시간, 화면 해상도, 브라우저 언어를 수집하여 서버로 전송
 */
(function() {
    'use strict';

    // 방문자 ID (서버에서 주입)
    const visitorId = window.QRGEN_VISITOR_ID;

    if (!visitorId) {
        console.warn('[QrGen Visitor] visitorId가 설정되지 않았습니다.');
        return;
    }

    // 페이지 진입 시간
    const pageEnterTime = Date.now();

    // 클라이언트 정보 수집
    const clientInfo = {
        screenResolution: `${screen.width}x${screen.height}`,
        language: navigator.language || navigator.userLanguage || 'unknown'
    };

    /**
     * 방문자 정보 업데이트 전송
     */
    function sendVisitorUpdate() {
        const durationSeconds = Math.round((Date.now() - pageEnterTime) / 1000);

        const data = {
            visitorId: visitorId,
            durationSeconds: durationSeconds,
            screenResolution: clientInfo.screenResolution,
            language: clientInfo.language
        };

        // Beacon API 사용 (페이지 이탈 시에도 확실히 전송)
        const blob = new Blob([JSON.stringify(data)], { type: 'application/json' });
        const sent = navigator.sendBeacon('/qrgen/visitor/update', blob);

        if (!sent) {
            // Beacon 실패 시 fetch로 시도
            fetch('/qrgen/visitor/update', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data),
                keepalive: true
            }).catch(() => {
                // 실패해도 무시 (사용자 경험에 영향 없음)
            });
        }
    }

    // 페이지 이탈 시 전송
    document.addEventListener('visibilitychange', function() {
        if (document.visibilityState === 'hidden') {
            sendVisitorUpdate();
        }
    });

    // beforeunload에서도 전송 (visibilitychange를 지원하지 않는 브라우저 대비)
    window.addEventListener('beforeunload', sendVisitorUpdate);

    // pagehide 이벤트 (모바일 Safari 등)
    window.addEventListener('pagehide', sendVisitorUpdate);

})();
