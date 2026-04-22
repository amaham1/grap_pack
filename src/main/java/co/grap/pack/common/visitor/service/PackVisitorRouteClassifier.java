package co.grap.pack.common.visitor.service;

import co.grap.pack.common.visitor.model.PackVisitorClassification;
import co.grap.pack.common.visitor.model.PackVisitorMenuCode;
import co.grap.pack.common.visitor.model.PackVisitorServiceCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 공개 페이지 요청을 서비스/메뉴/라우트 키로 분류한다.
 */
@Component
public class PackVisitorRouteClassifier {

    private static final Pattern CONTENT_DETAIL_PATTERN = Pattern.compile("^/grap/user/content/detail/[^/]+$");
    private static final Pattern REAL_ESTATE_DETAIL_PATTERN = Pattern.compile("^/grap/user/content/real-estate/[^/]+$");
    private static final Pattern FESTIVAL_DETAIL_PATTERN = Pattern.compile("^/grap/user/content/festivals/[^/]+$");
    private static final Pattern EXHIBITION_DETAIL_PATTERN = Pattern.compile("^/grap/user/content/exhibitions/[^/]+$");
    private static final Pattern WELFARE_DETAIL_PATTERN = Pattern.compile("^/grap/user/content/welfare/[^/]+$");
    private static final Pattern GAS_STATION_DETAIL_PATTERN = Pattern.compile("^/grap/user/content/gas-stations/[^/]+$");
    private static final Pattern QRMANAGE_SHOP_VIEW_PATTERN = Pattern.compile("^/qr-manage/view/shop/[^/]+$");
    private static final Pattern QRMANAGE_MENU_LIST_PATTERN = Pattern.compile("^/qr-manage/view/menu/[^/]+$");
    private static final Pattern QRMANAGE_MENU_DETAIL_PATTERN = Pattern.compile("^/qr-manage/view/menu/[^/]+/[^/]+$");

    /**
     * 요청 기준으로 라우트를 분류한다.
     */
    public Optional<PackVisitorClassification> classify(HttpServletRequest request) {
        return classify(request.getRequestURI(), extractSingleValueParams(request));
    }

    /**
     * 저장된 pageUrl 문자열 기준으로 라우트를 분류한다.
     */
    public Optional<PackVisitorClassification> classify(String pageUrl) {
        ParsedPageUrl parsedPageUrl = parsePageUrl(pageUrl);
        return classify(parsedPageUrl.path(), parsedPageUrl.queryParams());
    }

    private Optional<PackVisitorClassification> classify(String path, Map<String, String> queryParams) {
        if (path == null || path.isBlank()) {
            return Optional.empty();
        }

        if ("/".equals(path)) {
            return Optional.of(classification(
                    PackVisitorServiceCode.LANDING,
                    PackVisitorMenuCode.LANDING_HOME,
                    "/"
            ));
        }

        if ("/grap/user/content/list".equals(path)) {
            return Optional.of(classification(
                    PackVisitorServiceCode.GRAP,
                    PackVisitorMenuCode.GRAP_CONTENT_LIST,
                    "/grap/user/content/list"
            ));
        }

        if (CONTENT_DETAIL_PATTERN.matcher(path).matches()) {
            return Optional.of(classification(
                    PackVisitorServiceCode.GRAP,
                    PackVisitorMenuCode.GRAP_CONTENT_DETAIL,
                    "/grap/user/content/detail/{contentId}"
            ));
        }

        if ("/grap/user/content/real-estate".equals(path)) {
            return Optional.of(classification(
                    PackVisitorServiceCode.GRAP,
                    PackVisitorMenuCode.GRAP_REAL_ESTATE_LIST,
                    "/grap/user/content/real-estate"
            ));
        }

        if (REAL_ESTATE_DETAIL_PATTERN.matcher(path).matches()) {
            return Optional.of(classification(
                    PackVisitorServiceCode.GRAP,
                    PackVisitorMenuCode.GRAP_REAL_ESTATE_DETAIL,
                    "/grap/user/content/real-estate/{id}"
            ));
        }

        if ("/grap/user/content/festivals".equals(path)) {
            return Optional.of(classification(
                    PackVisitorServiceCode.GRAP,
                    PackVisitorMenuCode.GRAP_FESTIVAL_LIST,
                    "/grap/user/content/festivals"
            ));
        }

        if (FESTIVAL_DETAIL_PATTERN.matcher(path).matches()) {
            return Optional.of(classification(
                    PackVisitorServiceCode.GRAP,
                    PackVisitorMenuCode.GRAP_FESTIVAL_DETAIL,
                    "/grap/user/content/festivals/{id}"
            ));
        }

        if ("/grap/user/content/festivals/request".equals(path)) {
            return Optional.of(classification(
                    PackVisitorServiceCode.GRAP,
                    PackVisitorMenuCode.GRAP_FESTIVAL_REQUEST,
                    "/grap/user/content/festivals/request"
            ));
        }

        if ("/grap/user/content/exhibitions".equals(path)) {
            String tab = queryParams.get("tab");
            String routeKey = "/grap/user/content/exhibitions";
            if (tab != null && !tab.isBlank()) {
                routeKey += "?tab=" + tab;
            }

            return Optional.of(classification(
                    PackVisitorServiceCode.GRAP,
                    PackVisitorMenuCode.GRAP_EXHIBITION_LIST,
                    routeKey
            ));
        }

        if (EXHIBITION_DETAIL_PATTERN.matcher(path).matches()) {
            return Optional.of(classification(
                    PackVisitorServiceCode.GRAP,
                    PackVisitorMenuCode.GRAP_EXHIBITION_DETAIL,
                    "/grap/user/content/exhibitions/{id}"
            ));
        }

        if ("/grap/user/content/exhibitions/request".equals(path)) {
            return Optional.of(classification(
                    PackVisitorServiceCode.GRAP,
                    PackVisitorMenuCode.GRAP_EXHIBITION_REQUEST,
                    "/grap/user/content/exhibitions/request"
            ));
        }

        if ("/grap/user/content/welfare".equals(path)) {
            return Optional.of(classification(
                    PackVisitorServiceCode.GRAP,
                    PackVisitorMenuCode.GRAP_WELFARE_LIST,
                    "/grap/user/content/welfare"
            ));
        }

        if (WELFARE_DETAIL_PATTERN.matcher(path).matches()) {
            return Optional.of(classification(
                    PackVisitorServiceCode.GRAP,
                    PackVisitorMenuCode.GRAP_WELFARE_DETAIL,
                    "/grap/user/content/welfare/{id}"
            ));
        }

        if ("/grap/user/content/welfare/request".equals(path)) {
            return Optional.of(classification(
                    PackVisitorServiceCode.GRAP,
                    PackVisitorMenuCode.GRAP_WELFARE_REQUEST,
                    "/grap/user/content/welfare/request"
            ));
        }

        if ("/grap/user/content/gas-stations".equals(path)) {
            return Optional.of(classification(
                    PackVisitorServiceCode.GRAP,
                    PackVisitorMenuCode.GRAP_GAS_STATION_LIST,
                    "/grap/user/content/gas-stations"
            ));
        }

        if (GAS_STATION_DETAIL_PATTERN.matcher(path).matches()) {
            return Optional.of(classification(
                    PackVisitorServiceCode.GRAP,
                    PackVisitorMenuCode.GRAP_GAS_STATION_DETAIL,
                    "/grap/user/content/gas-stations/{id}"
            ));
        }

        if ("/qrgen".equals(path) || "/qrgen/".equals(path)) {
            return Optional.of(classification(
                    PackVisitorServiceCode.QRGEN,
                    PackVisitorMenuCode.QRGEN_HOME,
                    "/qrgen/"
            ));
        }

        if (QRMANAGE_SHOP_VIEW_PATTERN.matcher(path).matches()) {
            return Optional.of(classification(
                    PackVisitorServiceCode.QRMANAGE,
                    PackVisitorMenuCode.QRMANAGE_SHOP_VIEW,
                    "/qr-manage/view/shop/{qrCode}"
            ));
        }

        if (QRMANAGE_MENU_DETAIL_PATTERN.matcher(path).matches()) {
            return Optional.of(classification(
                    PackVisitorServiceCode.QRMANAGE,
                    PackVisitorMenuCode.QRMANAGE_MENU_DETAIL,
                    "/qr-manage/view/menu/{qrCode}/{menuId}"
            ));
        }

        if (QRMANAGE_MENU_LIST_PATTERN.matcher(path).matches()) {
            return Optional.of(classification(
                    PackVisitorServiceCode.QRMANAGE,
                    PackVisitorMenuCode.QRMANAGE_MENU_LIST,
                    "/qr-manage/view/menu/{qrCode}"
            ));
        }

        return Optional.empty();
    }

    private PackVisitorClassification classification(
            PackVisitorServiceCode serviceCode,
            PackVisitorMenuCode menuCode,
            String routeKey
    ) {
        return PackVisitorClassification.builder()
                .serviceCode(serviceCode)
                .menuCode(menuCode)
                .routeKey(routeKey)
                .build();
    }

    private Map<String, String> extractSingleValueParams(HttpServletRequest request) {
        Map<String, String> result = new LinkedHashMap<>();
        request.getParameterMap().forEach((key, value) -> {
            if (value != null && value.length > 0) {
                result.put(key, value[0]);
            }
        });
        return result;
    }

    private ParsedPageUrl parsePageUrl(String pageUrl) {
        if (pageUrl == null || pageUrl.isBlank()) {
            return new ParsedPageUrl("", Map.of());
        }

        try {
            URI uri = new URI(pageUrl);
            return new ParsedPageUrl(uri.getPath(), parseQueryString(uri.getRawQuery()));
        } catch (URISyntaxException exception) {
            int queryIndex = pageUrl.indexOf('?');
            if (queryIndex < 0) {
                return new ParsedPageUrl(pageUrl, Map.of());
            }

            return new ParsedPageUrl(
                    pageUrl.substring(0, queryIndex),
                    parseQueryString(pageUrl.substring(queryIndex + 1))
            );
        }
    }

    private Map<String, String> parseQueryString(String queryString) {
        if (queryString == null || queryString.isBlank()) {
            return Map.of();
        }

        Map<String, String> queryParams = new LinkedHashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            if (pair == null || pair.isBlank()) {
                continue;
            }

            int separatorIndex = pair.indexOf('=');
            if (separatorIndex < 0) {
                queryParams.put(pair, "");
                continue;
            }

            queryParams.put(pair.substring(0, separatorIndex), pair.substring(separatorIndex + 1));
        }

        return queryParams;
    }

    private record ParsedPageUrl(String path, Map<String, String> queryParams) {
    }
}
