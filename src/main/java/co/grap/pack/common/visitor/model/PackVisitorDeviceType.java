package co.grap.pack.common.visitor.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 방문자 디바이스 유형이다.
 */
@Getter
@RequiredArgsConstructor
public enum PackVisitorDeviceType {

    DESKTOP("데스크톱"),
    MOBILE("모바일"),
    TABLET("태블릿"),
    BOT("봇"),
    UNKNOWN("알 수 없음");

    private final String displayName;
}
