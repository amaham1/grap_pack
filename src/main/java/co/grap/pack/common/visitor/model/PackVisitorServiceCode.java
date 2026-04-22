package co.grap.pack.common.visitor.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 방문자 통계 서비스 구분 코드다.
 */
@Getter
@RequiredArgsConstructor
public enum PackVisitorServiceCode {

    LANDING("랜딩"),
    GRAP("Grap CMS"),
    QRGEN("QR 생성기"),
    QRMANAGE("QR 메뉴");

    private final String displayName;

    /**
     * 문자열 코드를 enum 으로 변환한다.
     */
    public static PackVisitorServiceCode fromCode(String code) {
        return Arrays.stream(values())
                .filter(value -> value.name().equals(code))
                .findFirst()
                .orElse(null);
    }
}
