package co.grap.pack.common.seo.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 공개 페이지 SEO 메타 정보를 담는 모델이다.
 */
@Getter
@Builder
public class PublicSeoMeta {

    /**
     * 문서 제목이다.
     */
    private final String title;

    /**
     * 문서 설명이다.
     */
    private final String description;

    /**
     * 대표 URL이다.
     */
    private final String canonicalUrl;

    /**
     * 검색 엔진 색인 정책이다.
     */
    private final String robots;

    /**
     * Open Graph 타입이다.
     */
    private final String ogType;

    /**
     * Open Graph 이미지 URL이다.
     */
    private final String ogImageUrl;

    /**
     * 사이트 이름이다.
     */
    private final String siteName;

    /**
     * 로케일 값이다.
     */
    private final String locale;

    /**
     * 트위터 카드 타입이다.
     */
    private final String twitterCard;

    /**
     * 구조화 데이터 JSON 목록이다.
     */
    private final List<String> structuredDataList;
}
