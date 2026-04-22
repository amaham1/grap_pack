/**
 * 공통 방문자 추적 JavaScript
 * 페이지 체류시간, 화면 해상도, 브라우저 언어를 수집해 서버로 전송한다.
 */
(function () {
    'use strict';

    const visitorId = window.PACK_VISITOR_ID;
    if (!visitorId) {
        return;
    }

    const pageEnterTime = Date.now();
    let sent = false;

    const clientInfo = {
        screenResolution: `${window.screen.width}x${window.screen.height}`,
        language: navigator.language || navigator.userLanguage || 'unknown'
    };

    function sendVisitorUpdate() {
        if (sent) {
            return;
        }

        sent = true;

        const durationSeconds = Math.max(1, Math.round((Date.now() - pageEnterTime) / 1000));
        const data = {
            visitorId: visitorId,
            durationSeconds: durationSeconds,
            screenResolution: clientInfo.screenResolution,
            language: clientInfo.language
        };

        const blob = new Blob([JSON.stringify(data)], { type: 'application/json' });
        const beaconSent = navigator.sendBeacon('/api/visitor/update', blob);

        if (!beaconSent) {
            fetch('/api/visitor/update', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data),
                keepalive: true
            }).catch(() => {
                // 방문자 추적 실패는 사용자 흐름에 영향을 주지 않는다.
            });
        }
    }

    document.addEventListener('visibilitychange', function () {
        if (document.visibilityState === 'hidden') {
            sendVisitorUpdate();
        }
    });

    window.addEventListener('pagehide', sendVisitorUpdate);
    window.addEventListener('beforeunload', sendVisitorUpdate);
})();
