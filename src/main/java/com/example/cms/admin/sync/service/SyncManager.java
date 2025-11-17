package com.example.cms.admin.sync.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 동기화 작업 상태를 관리하는 서비스
 * 세션별로 동기화 중단 플래그를 관리합니다.
 */
@Slf4j
@Service
public class SyncManager {

    // 세션ID별 동기화 중단 플래그
    private final Map<String, AtomicBoolean> cancelFlags = new ConcurrentHashMap<>();

    /**
     * 동기화 시작 시 호출 - 중단 플래그 초기화
     */
    public void startSync(String sessionId) {
        log.info("===> [SyncManager] 동기화 시작 - 세션ID: {}", sessionId);
        cancelFlags.put(sessionId, new AtomicBoolean(false));
        log.info("===> [SyncManager] 현재 활성 세션 수: {}", cancelFlags.size());
    }

    /**
     * 동기화 중단 요청
     */
    public void cancelSync(String sessionId) {
        log.warn("===> [SyncManager] *** 동기화 중단 요청 *** - 세션ID: {}", sessionId);
        AtomicBoolean flag = cancelFlags.get(sessionId);
        if (flag != null) {
            boolean oldValue = flag.getAndSet(true);
            log.warn("===> [SyncManager] 중단 플래그 설정: {} -> true", oldValue);
        } else {
            log.warn("===> [SyncManager] 경고: 세션ID {}에 대한 플래그를 찾을 수 없음", sessionId);
            log.warn("===> [SyncManager] 현재 활성 세션 목록: {}", cancelFlags.keySet());
        }
    }

    /**
     * 동기화 완료 시 호출 - 플래그 제거
     */
    public void completeSync(String sessionId) {
        log.info("===> [SyncManager] 동기화 완료 - 세션ID: {}", sessionId);
        cancelFlags.remove(sessionId);
        log.info("===> [SyncManager] 남은 활성 세션 수: {}", cancelFlags.size());
    }

    /**
     * 중단 요청 여부 확인
     */
    public boolean isCancelled(String sessionId) {
        AtomicBoolean flag = cancelFlags.get(sessionId);
        boolean cancelled = flag != null && flag.get();
        if (cancelled) {
            log.warn("===> [SyncManager] 중단 플래그 감지! - 세션ID: {}", sessionId);
        }
        return cancelled;
    }

    /**
     * 중단 여부를 체크하고, 중단되었다면 예외를 던집니다.
     */
    public void checkCancellation(String sessionId) {
        log.debug("===> [SyncManager] 중단 체크 - 세션ID: {}", sessionId);
        if (isCancelled(sessionId)) {
            log.error("===> [SyncManager] !!! 동기화 중단 예외 발생 !!! - 세션ID: {}", sessionId);
            throw new SyncCancelledException("사용자가 동기화를 중단했습니다.");
        }
    }

    /**
     * 동기화 중단 예외
     */
    public static class SyncCancelledException extends RuntimeException {
        public SyncCancelledException(String message) {
            super(message);
        }
    }
}
