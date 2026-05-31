package co.grap.pack.common.visitor.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 방문자 인증 범위 구분값이다.
 */
@Getter
@RequiredArgsConstructor
public enum PackVisitorAuthScope {

    ANONYMOUS("익명"),
    QRGEN_USER("QRgen 회원");

    private final String displayName;
}
