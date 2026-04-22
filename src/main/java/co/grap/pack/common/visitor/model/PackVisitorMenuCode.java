package co.grap.pack.common.visitor.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * 방문자 통계 메뉴 구분 코드다.
 */
@Getter
@RequiredArgsConstructor
public enum PackVisitorMenuCode {

    LANDING_HOME(PackVisitorServiceCode.LANDING, "랜딩 홈"),

    GRAP_CONTENT_LIST(PackVisitorServiceCode.GRAP, "Grap 일반 콘텐츠 목록"),
    GRAP_CONTENT_DETAIL(PackVisitorServiceCode.GRAP, "Grap 일반 콘텐츠 상세"),
    GRAP_REAL_ESTATE_LIST(PackVisitorServiceCode.GRAP, "Grap 부동산 목록"),
    GRAP_REAL_ESTATE_DETAIL(PackVisitorServiceCode.GRAP, "Grap 부동산 상세"),
    GRAP_FESTIVAL_LIST(PackVisitorServiceCode.GRAP, "Grap 축제 목록"),
    GRAP_FESTIVAL_DETAIL(PackVisitorServiceCode.GRAP, "Grap 축제 상세"),
    GRAP_FESTIVAL_REQUEST(PackVisitorServiceCode.GRAP, "Grap 축제 등록 요청"),
    GRAP_EXHIBITION_LIST(PackVisitorServiceCode.GRAP, "Grap 공연/전시 목록"),
    GRAP_EXHIBITION_DETAIL(PackVisitorServiceCode.GRAP, "Grap 공연/전시 상세"),
    GRAP_EXHIBITION_REQUEST(PackVisitorServiceCode.GRAP, "Grap 공연/전시 등록 요청"),
    GRAP_WELFARE_LIST(PackVisitorServiceCode.GRAP, "Grap 복지 목록"),
    GRAP_WELFARE_DETAIL(PackVisitorServiceCode.GRAP, "Grap 복지 상세"),
    GRAP_WELFARE_REQUEST(PackVisitorServiceCode.GRAP, "Grap 복지 등록 요청"),
    GRAP_GAS_STATION_LIST(PackVisitorServiceCode.GRAP, "Grap 주유소 목록"),
    GRAP_GAS_STATION_DETAIL(PackVisitorServiceCode.GRAP, "Grap 주유소 상세"),

    QRGEN_HOME(PackVisitorServiceCode.QRGEN, "QR 생성기 홈"),

    QRMANAGE_SHOP_VIEW(PackVisitorServiceCode.QRMANAGE, "QR 매장 보기"),
    QRMANAGE_MENU_LIST(PackVisitorServiceCode.QRMANAGE, "QR 메뉴 목록"),
    QRMANAGE_MENU_DETAIL(PackVisitorServiceCode.QRMANAGE, "QR 메뉴 상세");

    private final PackVisitorServiceCode serviceCode;
    private final String displayName;

    /**
     * 서비스 기준 메뉴 목록을 조회한다.
     */
    public static List<PackVisitorMenuCode> findByServiceCode(PackVisitorServiceCode serviceCode) {
        return Arrays.stream(values())
                .filter(value -> serviceCode == null || value.getServiceCode() == serviceCode)
                .toList();
    }

    /**
     * 문자열 코드를 enum 으로 변환한다.
     */
    public static PackVisitorMenuCode fromCode(String code) {
        return Arrays.stream(values())
                .filter(value -> value.name().equals(code))
                .findFirst()
                .orElse(null);
    }
}
