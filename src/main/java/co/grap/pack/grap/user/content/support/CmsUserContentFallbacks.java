package co.grap.pack.grap.user.content.support;

import co.grap.pack.common.util.PaginationUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CmsUserContentFallbacks {

    private CmsUserContentFallbacks() {
    }

    public static Map<String, Object> unavailableList(
            String listAttributeName,
            String keyword,
            Integer page,
            Integer size,
            String message
    ) {
        return unavailableList(listAttributeName, keyword, page, size, message, Map.of());
    }

    public static Map<String, Object> unavailableList(
            String listAttributeName,
            String keyword,
            Integer page,
            Integer size,
            String message,
            Map<String, Object> extraAttributes
    ) {
        Map<String, Object> result = new HashMap<>(PaginationUtil.createPaginationResult(0, page, size));
        result.put(listAttributeName, List.of());
        result.put("keyword", keyword);
        result.put("serviceUnavailable", true);
        result.put("serviceUnavailableMessage", message);
        result.putAll(extraAttributes);
        return result;
    }
}
