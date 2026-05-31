package co.grap.pack.common.seo.support;

import co.grap.pack.common.seo.model.PublicSeoBreadcrumbItem;
import co.grap.pack.common.seo.model.PublicSeoMeta;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ui.Model;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 공개 SEO 공통 유틸리티다.
 */
public final class PublicSeoSupport {

    public static final String DOMAIN = "https://grap.co.kr";
    public static final String SITE_NAME = "Grap";
    public static final String LOCALE = "ko_KR";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private PublicSeoSupport() {
    }

    /**
     * SEO 메타 정보를 모델에 주입한다.
     *
     * @param model SEO를 주입할 모델
     * @param seoMeta SEO 메타 정보
     */
    public static void applySeo(Model model, PublicSeoMeta seoMeta) {
        model.addAttribute("seoTitle", seoMeta.getTitle());
        model.addAttribute("seoDescription", seoMeta.getDescription());
        model.addAttribute("seoCanonical", seoMeta.getCanonicalUrl());
        model.addAttribute("seoRobots", seoMeta.getRobots());
        model.addAttribute("seoOgType", seoMeta.getOgType());
        model.addAttribute("seoOgImage", seoMeta.getOgImageUrl());
        model.addAttribute("seoSiteName", seoMeta.getSiteName());
        model.addAttribute("seoLocale", seoMeta.getLocale());
        model.addAttribute("seoTwitterCard", seoMeta.getTwitterCard());
        model.addAttribute("seoStructuredDataList", seoMeta.getStructuredDataList());
    }

    /**
     * 상대 경로를 절대 URL로 바꾼다.
     *
     * @param path 상대 경로 또는 절대 URL
     * @return 절대 URL
     */
    public static String absoluteUrl(String path) {
        if (!hasText(path)) {
            return DOMAIN;
        }
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }
        if (path.startsWith("/")) {
            return DOMAIN + path;
        }
        return DOMAIN + "/" + path;
    }

    /**
     * HTML을 걷어낸 짧은 설명을 만든다.
     *
     * @param rawText 원본 문자열
     * @param maxLength 최대 길이
     * @return 정리된 설명 문자열
     */
    public static String summarize(String rawText, int maxLength) {
        String cleaned = cleanText(rawText);
        if (!hasText(cleaned)) {
            return "";
        }
        if (cleaned.length() <= maxLength) {
            return cleaned;
        }
        return cleaned.substring(0, Math.max(0, maxLength - 1)).trim() + "…";
    }

    /**
     * HTML과 엔티티를 정리한 일반 텍스트를 만든다.
     *
     * @param rawText 원본 문자열
     * @return 정리된 문자열
     */
    public static String cleanText(String rawText) {
        if (rawText == null) {
            return "";
        }
        String withoutTags = rawText
                .replaceAll("(?i)<br\\s*/?>", " ")
                .replaceAll("<[^>]+>", " ");
        String unescaped = HtmlUtils.htmlUnescape(withoutTags);
        return unescaped.replaceAll("\\s+", " ").trim();
    }

    /**
     * 비어 있지 않은 첫 값을 반환한다.
     *
     * @param values 후보 값들
     * @return 첫 번째 유효 값
     */
    public static String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (hasText(value)) {
                return value.trim();
            }
        }
        return "";
    }

    /**
     * 값 존재 여부를 확인한다.
     *
     * @param value 문자열
     * @return 값 존재 여부
     */
    public static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    /**
     * 구조화 데이터용 기본 맵을 만든다.
     *
     * @param type schema.org 타입
     * @return 기본 맵
     */
    public static Map<String, Object> linkedData(String type) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("@context", "https://schema.org");
        data.put("@type", type);
        return data;
    }

    /**
     * 구조화 데이터 JSON 문자열을 만든다.
     *
     * @param data 구조화 데이터 맵
     * @return JSON 문자열
     */
    public static String toJson(Map<String, Object> data) {
        try {
            return OBJECT_MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("SEO 구조화 데이터를 JSON으로 변환하지 못했습니다.", exception);
        }
    }

    /**
     * BreadcrumbList JSON 문자열을 만든다.
     *
     * @param items breadcrumb 항목
     * @return JSON 문자열
     */
    public static String breadcrumbJson(List<PublicSeoBreadcrumbItem> items) {
        Map<String, Object> breadcrumbData = linkedData("BreadcrumbList");
        List<Map<String, Object>> elements = new ArrayList<>();
        int position = 1;

        for (PublicSeoBreadcrumbItem item : items) {
            Map<String, Object> element = new LinkedHashMap<>();
            element.put("@type", "ListItem");
            element.put("position", position++);
            element.put("name", item.name());
            element.put("item", absoluteUrl(item.path()));
            elements.add(element);
        }

        breadcrumbData.put("itemListElement", elements);
        return toJson(breadcrumbData);
    }

    /**
     * 리스트 페이지 구조화 데이터를 만든다.
     *
     * @param name 페이지 이름
     * @param description 설명
     * @param path 경로
     * @return JSON 문자열
     */
    public static String collectionPageJson(String name, String description, String path) {
        Map<String, Object> data = linkedData("CollectionPage");
        data.put("name", name);
        data.put("description", description);
        data.put("url", absoluteUrl(path));
        data.put("inLanguage", "ko-KR");
        return toJson(data);
    }

    /**
     * 웹 페이지 구조화 데이터를 만든다.
     *
     * @param name 페이지 이름
     * @param description 설명
     * @param path 경로
     * @param imageUrl 대표 이미지
     * @param dateModified 수정일
     * @return JSON 문자열
     */
    public static String webPageJson(
            String name,
            String description,
            String path,
            String imageUrl,
            String dateModified
    ) {
        Map<String, Object> data = linkedData("WebPage");
        data.put("name", name);
        data.put("description", description);
        data.put("url", absoluteUrl(path));
        data.put("inLanguage", "ko-KR");
        if (hasText(imageUrl)) {
            data.put("image", absoluteUrl(imageUrl));
        }
        if (hasText(dateModified)) {
            data.put("dateModified", dateModified);
        }
        return toJson(data);
    }

    /**
     * 기사형 구조화 데이터를 만든다.
     *
     * @param headline 제목
     * @param description 설명
     * @param path 경로
     * @param imageUrl 대표 이미지
     * @param datePublished 발행일
     * @param dateModified 수정일
     * @param articleSection 섹션명
     * @return JSON 문자열
     */
    public static String articleJson(
            String headline,
            String description,
            String path,
            String imageUrl,
            String datePublished,
            String dateModified,
            String articleSection
    ) {
        Map<String, Object> data = linkedData("Article");
        data.put("headline", headline);
        data.put("description", description);
        data.put("mainEntityOfPage", absoluteUrl(path));
        data.put("author", Map.of("@type", "Organization", "name", SITE_NAME));
        data.put("publisher", Map.of("@type", "Organization", "name", SITE_NAME));
        data.put("inLanguage", "ko-KR");
        if (hasText(imageUrl)) {
            data.put("image", List.of(absoluteUrl(imageUrl)));
        }
        if (hasText(datePublished)) {
            data.put("datePublished", datePublished);
        }
        if (hasText(dateModified)) {
            data.put("dateModified", dateModified);
        }
        if (hasText(articleSection)) {
            data.put("articleSection", articleSection);
        }
        return toJson(data);
    }

    /**
     * 구조화 데이터용 날짜 문자열을 만든다.
     *
     * @param value 날짜 값
     * @return ISO 형식 날짜 문자열
     */
    public static String toIsoDateTime(Object value) {
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.format(ISO_DATE_TIME);
        }
        if (value instanceof LocalDate localDate) {
            return localDate.atStartOfDay().format(ISO_DATE_TIME);
        }
        return "";
    }

    /**
     * 구조화 데이터용 날짜 문자열을 만든다.
     *
     * @param value 날짜 값
     * @return ISO 날짜 문자열
     */
    public static String toIsoDate(Object value) {
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.toLocalDate().format(ISO_DATE);
        }
        if (value instanceof LocalDate localDate) {
            return localDate.format(ISO_DATE);
        }
        return "";
    }

    /**
     * robots 정책 기본값을 만든다.
     *
     * @param index 색인 허용 여부
     * @return robots 값
     */
    public static String robots(boolean index) {
        return index ? "index, follow" : "noindex, follow";
    }

    /**
     * 이미지 존재 여부에 따라 트위터 카드 타입을 만든다.
     *
     * @param imageUrl 이미지 URL
     * @return 트위터 카드 타입
     */
    public static String twitterCard(String imageUrl) {
        return hasText(imageUrl) ? "summary_large_image" : "summary";
    }
}
