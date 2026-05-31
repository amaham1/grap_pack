/**
 * 공통 방문자 추적 JavaScript
 * 페이지 체류시간, 화면 노출 시간, 사용자 상호작용, 화면 해상도, 브라우저 언어를 수집해 서버로 전송한다.
 */
(function () {
    'use strict';

    const HUMAN_VISIBLE_SECONDS = 3;
    const visitorId = window.PACK_VISITOR_ID;
    if (!visitorId) {
        return;
    }

    const pageEnterTime = Date.now();
    let finalUpdateSent = false;
    let humanSignalSent = false;
    let visibleStartedAt = document.visibilityState === 'visible' ? pageEnterTime : null;
    let visibleDurationMilliseconds = 0;
    let interactionCount = 0;
    let firstInteractionElapsedSeconds = null;

    const clientInfo = {
        screenResolution: `${window.screen.width}x${window.screen.height}`,
        language: navigator.language || navigator.userLanguage || 'unknown'
    };

    function getVisibleDurationMilliseconds() {
        if (visibleStartedAt === null) {
            return visibleDurationMilliseconds;
        }

        return visibleDurationMilliseconds + Math.max(0, Date.now() - visibleStartedAt);
    }

    function getVisibleDurationSeconds() {
        return Math.max(0, Math.round(getVisibleDurationMilliseconds() / 1000));
    }

    function getDurationSeconds() {
        return Math.max(1, Math.round((Date.now() - pageEnterTime) / 1000));
    }

    function sendVisitorUpdate(options) {
        const shouldFinalize = options && options.final === true;

        if (shouldFinalize && finalUpdateSent) {
            return;
        }

        if (shouldFinalize) {
            finalUpdateSent = true;
        }

        const data = {
            visitorId: visitorId,
            durationSeconds: getDurationSeconds(),
            visibleDurationSeconds: getVisibleDurationSeconds(),
            interactionCount: interactionCount,
            firstInteractionElapsedSeconds: firstInteractionElapsedSeconds,
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

    function maybeSendHumanSignal() {
        if (humanSignalSent || interactionCount <= 0 || getVisibleDurationSeconds() < HUMAN_VISIBLE_SECONDS) {
            return;
        }

        humanSignalSent = true;
        sendVisitorUpdate({ final: false });
    }

    function recordInteraction(event) {
        if (event && event.isTrusted === false) {
            return;
        }

        interactionCount = Math.min(interactionCount + 1, 999);

        if (firstInteractionElapsedSeconds === null) {
            firstInteractionElapsedSeconds = getDurationSeconds();
        }

        maybeSendHumanSignal();
    }

    document.addEventListener('visibilitychange', function () {
        if (document.visibilityState === 'hidden') {
            if (visibleStartedAt !== null) {
                visibleDurationMilliseconds += Math.max(0, Date.now() - visibleStartedAt);
                visibleStartedAt = null;
            }
            sendVisitorUpdate({ final: true });
            return;
        }

        if (visibleStartedAt === null) {
            visibleStartedAt = Date.now();
        }
    });

    ['pointerdown', 'click', 'keydown', 'touchstart', 'scroll', 'wheel'].forEach(function (eventName) {
        window.addEventListener(eventName, recordInteraction, { passive: true });
    });

    window.setTimeout(maybeSendHumanSignal, HUMAN_VISIBLE_SECONDS * 1000);
    window.addEventListener('pagehide', function () {
        sendVisitorUpdate({ final: true });
    });
    window.addEventListener('beforeunload', function () {
        sendVisitorUpdate({ final: true });
    });
})();
