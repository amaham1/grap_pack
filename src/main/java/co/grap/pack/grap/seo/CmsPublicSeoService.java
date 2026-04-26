package co.grap.pack.grap.seo;

import co.grap.pack.common.seo.model.PublicSeoBreadcrumbItem;
import co.grap.pack.common.seo.model.PublicSeoMeta;
import co.grap.pack.common.seo.support.PublicSeoSupport;
import co.grap.pack.grap.user.content.model.CmsUserContent;
import co.grap.pack.grap.user.content.model.CmsUserContentSearchParam;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Grap 공개 페이지 SEO 구성을 담당한다.
 */
@Service
public class CmsPublicSeoService {

    private static final String REAL_ESTATE_PATH = "/grap/user/content/real-estate";
    private static final String REAL_ESTATE_KEYWORDS = "제주도 부동산, 제주도 부동산 실거래가, 제주 부동산, 제주 아파트 실거래가, 제주 전월세 실거래가";

    /**
     * 루트 랜딩 SEO를 적용한다.
     *
     * @param model 화면 모델
     */
    public void applyLandingSeo(Model model) {
        String title = "제주도 부동산과 생활 정보";
        String description = "Grap에서 제주도 부동산 실거래가, 전월세 시세, 축제, 전시, 복지, 주유소 정보와 QR 코드 생성 도구를 한 번에 확인하세요.";

        Map<String, Object> website = PublicSeoSupport.linkedData("WebSite");
        website.put("name", PublicSeoSupport.SITE_NAME);
        website.put("url", PublicSeoSupport.absoluteUrl("/"));
        website.put("description", description);
        website.put("inLanguage", "ko-KR");

        Map<String, Object> organization = PublicSeoSupport.linkedData("Organization");
        organization.put("name", PublicSeoSupport.SITE_NAME);
        organization.put("url", PublicSeoSupport.absoluteUrl("/"));
        organization.put("description", description);

        applySeo(model, PublicSeoMeta.builder()
                .title(title)
                .description(description)
                .canonicalUrl(PublicSeoSupport.absoluteUrl("/"))
                .robots(PublicSeoSupport.robots(true))
                .ogType("website")
                .siteName(PublicSeoSupport.SITE_NAME)
                .locale(PublicSeoSupport.LOCALE)
                .twitterCard("summary")
                .structuredDataList(List.of(
                        PublicSeoSupport.toJson(website),
                        PublicSeoSupport.toJson(organization)
                ))
                .build());
    }

    /**
     * 일반 콘텐츠 목록 SEO를 적용한다.
     *
     * @param model 화면 모델
     * @param searchParam 검색 파라미터
     */
    public void applyContentListSeo(Model model, CmsUserContentSearchParam searchParam) {
        boolean indexable = !PublicSeoSupport.hasText(searchParam.getKeyword())
                && searchParam.getContentTypeId() == null
                && (searchParam.getPage() == null || searchParam.getPage() <= 1);

        applyListSeo(
                model,
                "/grap/user/content/list",
                "제주 공개 콘텐츠 모음",
                "제주 생활 정보, 소식, 안내 콘텐츠를 Grap 공개 콘텐츠 목록에서 한 번에 찾아보세요.",
                indexable,
                List.of(
                        new PublicSeoBreadcrumbItem("홈", "/"),
                        new PublicSeoBreadcrumbItem("공개 콘텐츠", "/grap/user/content/list")
                )
        );
    }

    /**
     * 일반 콘텐츠 상세 SEO를 적용한다.
     *
     * @param model 화면 모델
     * @param contentData 콘텐츠 데이터
     */
    public void applyContentDetailSeo(Model model, CmsUserContent contentData) {
        String path = "/grap/user/content/detail/" + contentData.getContentId();
        String description = firstNonBlank(
                PublicSeoSupport.summarize(contentData.getContent(), 155),
                contentData.getTypeName() + " 콘텐츠 상세 페이지입니다."
        );
        String imageUrl = firstImage(contentData.getImageList());
        String publishedAt = PublicSeoSupport.firstNonBlank(
                PublicSeoSupport.toIsoDateTime(contentData.getCreateDt()),
                PublicSeoSupport.toIsoDateTime(contentData.getUpdateDt())
        );
        String modifiedAt = PublicSeoSupport.firstNonBlank(
                PublicSeoSupport.toIsoDateTime(contentData.getUpdateDt()),
                publishedAt
        );

        applySeo(model, PublicSeoMeta.builder()
                .title(contentData.getTitle())
                .description(description)
                .canonicalUrl(PublicSeoSupport.absoluteUrl(path))
                .robots(PublicSeoSupport.robots(true))
                .ogType("article")
                .ogImageUrl(PublicSeoSupport.hasText(imageUrl) ? PublicSeoSupport.absoluteUrl(imageUrl) : null)
                .siteName(PublicSeoSupport.SITE_NAME)
                .locale(PublicSeoSupport.LOCALE)
                .twitterCard(PublicSeoSupport.twitterCard(imageUrl))
                .structuredDataList(List.of(
                        PublicSeoSupport.articleJson(
                                contentData.getTitle(),
                                description,
                                path,
                                imageUrl,
                                publishedAt,
                                modifiedAt,
                                contentData.getTypeName()
                        ),
                        PublicSeoSupport.breadcrumbJson(List.of(
                                new PublicSeoBreadcrumbItem("홈", "/"),
                                new PublicSeoBreadcrumbItem("공개 콘텐츠", "/grap/user/content/list"),
                                new PublicSeoBreadcrumbItem(contentData.getTitle(), path)
                        ))
                ))
                .build());
    }

    /**
     * 축제 목록 SEO를 적용한다.
     *
     * @param model 화면 모델
     * @param keyword 검색어
     * @param page 페이지 번호
     */
    public void applyFestivalListSeo(Model model, String keyword, Integer page) {
        boolean indexable = !PublicSeoSupport.hasText(keyword) && (page == null || page <= 1);
        applyListSeo(
                model,
                "/grap/user/content/festivals",
                "제주 축제 행사 모음",
                "제주에서 열리는 축제와 행사 정보를 날짜, 제목, 상세 안내와 함께 확인하세요.",
                indexable,
                List.of(
                        new PublicSeoBreadcrumbItem("홈", "/"),
                        new PublicSeoBreadcrumbItem("축제·행사", "/grap/user/content/festivals")
                )
        );
    }

    /**
     * 축제 상세 SEO를 적용한다.
     *
     * @param model 화면 모델
     * @param festival 축제 데이터
     */
    public void applyFestivalDetailSeo(Model model, Map<String, Object> festival) {
        String path = "/grap/user/content/festivals/" + festival.get("id");
        String title = stringValue(festival.get("title"));
        String description = firstNonBlank(
                PublicSeoSupport.summarize(stringValue(festival.get("contentHtml")), 155),
                title + " 상세 안내 페이지입니다."
        );
        String imageUrl = firstImage(castStringList(festival.get("imageUrls")));
        String publishedAt = PublicSeoSupport.firstNonBlank(
                PublicSeoSupport.toIsoDateTime(festival.get("writtenDate")),
                PublicSeoSupport.toIsoDateTime(festival.get("createdAt"))
        );
        String modifiedAt = PublicSeoSupport.firstNonBlank(
                PublicSeoSupport.toIsoDateTime(festival.get("updatedAt")),
                publishedAt
        );

        applySeo(model, PublicSeoMeta.builder()
                .title(title)
                .description(description)
                .canonicalUrl(PublicSeoSupport.absoluteUrl(path))
                .robots(PublicSeoSupport.robots(true))
                .ogType("article")
                .ogImageUrl(PublicSeoSupport.hasText(imageUrl) ? PublicSeoSupport.absoluteUrl(imageUrl) : null)
                .siteName(PublicSeoSupport.SITE_NAME)
                .locale(PublicSeoSupport.LOCALE)
                .twitterCard(PublicSeoSupport.twitterCard(imageUrl))
                .structuredDataList(List.of(
                        PublicSeoSupport.articleJson(
                                title,
                                description,
                                path,
                                imageUrl,
                                publishedAt,
                                modifiedAt,
                                "축제·행사"
                        ),
                        PublicSeoSupport.breadcrumbJson(List.of(
                                new PublicSeoBreadcrumbItem("홈", "/"),
                                new PublicSeoBreadcrumbItem("축제·행사", "/grap/user/content/festivals"),
                                new PublicSeoBreadcrumbItem(title, path)
                        ))
                ))
                .build());
    }

    /**
     * 전시 목록 SEO를 적용한다.
     *
     * @param model 화면 모델
     * @param keyword 검색어
     * @param tab 탭 값
     * @param page 페이지 번호
     */
    public void applyExhibitionListSeo(Model model, String keyword, String tab, Integer page) {
        boolean indexable = !PublicSeoSupport.hasText(keyword)
                && (!PublicSeoSupport.hasText(tab) || "ongoing".equals(tab))
                && (page == null || page <= 1);

        applyListSeo(
                model,
                "/grap/user/content/exhibitions",
                "제주 공연 전시 모음",
                "제주에서 열리는 공연과 전시 일정을 장소, 기간, 운영 정보와 함께 확인하세요.",
                indexable,
                List.of(
                        new PublicSeoBreadcrumbItem("홈", "/"),
                        new PublicSeoBreadcrumbItem("공연·전시", "/grap/user/content/exhibitions")
                )
        );
    }

    /**
     * 전시 상세 SEO를 적용한다.
     *
     * @param model 화면 모델
     * @param exhibition 전시 데이터
     */
    public void applyExhibitionDetailSeo(Model model, Map<String, Object> exhibition) {
        String path = "/grap/user/content/exhibitions/" + exhibition.get("id");
        String title = stringValue(exhibition.get("title"));
        String locationName = stringValue(exhibition.get("locationName"));
        String description = firstNonBlank(
                joinNonBlank(" · ",
                        locationName,
                        formatDateRange(exhibition.get("startDate"), exhibition.get("endDate")),
                        stringValue(exhibition.get("timeInfo"))
                ),
                title + " 공연·전시 상세 안내 페이지입니다."
        );
        String imageUrl = stringValue(exhibition.get("coverImageUrl"));

        Map<String, Object> eventData = PublicSeoSupport.linkedData("Event");
        eventData.put("name", title);
        eventData.put("description", description);
        eventData.put("url", PublicSeoSupport.absoluteUrl(path));
        if (PublicSeoSupport.hasText(imageUrl)) {
            eventData.put("image", List.of(PublicSeoSupport.absoluteUrl(imageUrl)));
        }
        if (PublicSeoSupport.hasText(PublicSeoSupport.toIsoDateTime(exhibition.get("startDate")))) {
            eventData.put("startDate", PublicSeoSupport.toIsoDateTime(exhibition.get("startDate")));
        }
        if (PublicSeoSupport.hasText(PublicSeoSupport.toIsoDateTime(exhibition.get("endDate")))) {
            eventData.put("endDate", PublicSeoSupport.toIsoDateTime(exhibition.get("endDate")));
        }
        if (PublicSeoSupport.hasText(locationName)) {
            eventData.put("location", Map.of(
                    "@type", "Place",
                    "name", locationName
            ));
        }
        if (PublicSeoSupport.hasText(stringValue(exhibition.get("organizerInfo")))) {
            eventData.put("organizer", Map.of(
                    "@type", "Organization",
                    "name", stringValue(exhibition.get("organizerInfo"))
            ));
        }

        applySeo(model, PublicSeoMeta.builder()
                .title(title)
                .description(description)
                .canonicalUrl(PublicSeoSupport.absoluteUrl(path))
                .robots(PublicSeoSupport.robots(true))
                .ogType("article")
                .ogImageUrl(PublicSeoSupport.hasText(imageUrl) ? PublicSeoSupport.absoluteUrl(imageUrl) : null)
                .siteName(PublicSeoSupport.SITE_NAME)
                .locale(PublicSeoSupport.LOCALE)
                .twitterCard(PublicSeoSupport.twitterCard(imageUrl))
                .structuredDataList(List.of(
                        PublicSeoSupport.toJson(eventData),
                        PublicSeoSupport.breadcrumbJson(List.of(
                                new PublicSeoBreadcrumbItem("홈", "/"),
                                new PublicSeoBreadcrumbItem("공연·전시", "/grap/user/content/exhibitions"),
                                new PublicSeoBreadcrumbItem(title, path)
                        ))
                ))
                .build());
    }

    /**
     * 복지 목록 SEO를 적용한다.
     *
     * @param model 화면 모델
     * @param keyword 검색어
     * @param page 페이지 번호
     */
    public void applyWelfareListSeo(Model model, String keyword, Integer page) {
        boolean indexable = !PublicSeoSupport.hasText(keyword) && (page == null || page <= 1);
        applyListSeo(
                model,
                "/grap/user/content/welfare",
                "제주 복지 서비스 모음",
                "제주에서 신청 가능한 복지 서비스와 지원 대상, 지원 내용, 신청 방법을 확인하세요.",
                indexable,
                List.of(
                        new PublicSeoBreadcrumbItem("홈", "/"),
                        new PublicSeoBreadcrumbItem("복지 서비스", "/grap/user/content/welfare")
                )
        );
    }

    /**
     * 복지 상세 SEO를 적용한다.
     *
     * @param model 화면 모델
     * @param welfare 복지 데이터
     */
    public void applyWelfareDetailSeo(Model model, Map<String, Object> welfare) {
        String path = "/grap/user/content/welfare/" + welfare.get("id");
        String title = stringValue(welfare.get("serviceName"));
        String description = firstNonBlank(
                PublicSeoSupport.summarize(stringValue(welfare.get("supportTargetHtml")), 90),
                PublicSeoSupport.summarize(stringValue(welfare.get("supportContentHtml")), 150),
                title + " 복지 상세 안내 페이지입니다."
        );
        String modifiedAt = PublicSeoSupport.firstNonBlank(
                PublicSeoSupport.toIsoDateTime(welfare.get("updatedAt")),
                PublicSeoSupport.toIsoDateTime(welfare.get("createdAt"))
        );

        applySeo(model, PublicSeoMeta.builder()
                .title(title)
                .description(description)
                .canonicalUrl(PublicSeoSupport.absoluteUrl(path))
                .robots(PublicSeoSupport.robots(true))
                .ogType("article")
                .siteName(PublicSeoSupport.SITE_NAME)
                .locale(PublicSeoSupport.LOCALE)
                .twitterCard("summary")
                .structuredDataList(List.of(
                        PublicSeoSupport.webPageJson(title, description, path, null, modifiedAt),
                        PublicSeoSupport.breadcrumbJson(List.of(
                                new PublicSeoBreadcrumbItem("홈", "/"),
                                new PublicSeoBreadcrumbItem("복지 서비스", "/grap/user/content/welfare"),
                                new PublicSeoBreadcrumbItem(title, path)
                        ))
                ))
                .build());
    }

    /**
     * 주유소 목록 SEO를 적용한다.
     *
     * @param model 화면 모델
     * @param keyword 검색어
     * @param page 페이지 번호
     */
    public void applyGasStationListSeo(Model model, String keyword, Integer page) {
        boolean indexable = !PublicSeoSupport.hasText(keyword) && (page == null || page <= 1);
        applyListSeo(
                model,
                "/grap/user/content/gas-stations",
                "제주 주유소 가격 모음",
                "제주 주유소의 최신 휘발유, 경유, LPG 가격과 기본 정보를 한 번에 비교해 보세요.",
                indexable,
                List.of(
                        new PublicSeoBreadcrumbItem("홈", "/"),
                        new PublicSeoBreadcrumbItem("주유소 가격", "/grap/user/content/gas-stations")
                )
        );
    }

    /**
     * 주유소 상세 SEO를 적용한다.
     *
     * @param model 화면 모델
     * @param gasStation 주유소 데이터
     */
    public void applyGasStationDetailSeo(Model model, Map<String, Object> gasStation) {
        String path = "/grap/user/content/gas-stations/" + gasStation.get("id");
        String title = stringValue(gasStation.get("stationName"));
        String description = firstNonBlank(
                joinNonBlank(" · ",
                        stringValue(gasStation.get("address")),
                        gasPriceSummary(gasStation)
                ),
                title + " 주유소 가격 상세 페이지입니다."
        );
        String modifiedAt = PublicSeoSupport.firstNonBlank(
                PublicSeoSupport.toIsoDateTime(gasStation.get("priceFetchedAt")),
                PublicSeoSupport.toIsoDateTime(gasStation.get("updatedAt")),
                PublicSeoSupport.toIsoDateTime(gasStation.get("createdAt"))
        );

        Map<String, Object> localBusiness = PublicSeoSupport.linkedData("LocalBusiness");
        localBusiness.put("name", title);
        localBusiness.put("description", description);
        localBusiness.put("url", PublicSeoSupport.absoluteUrl(path));
        if (PublicSeoSupport.hasText(stringValue(gasStation.get("address")))) {
            localBusiness.put("address", stringValue(gasStation.get("address")));
        }
        if (PublicSeoSupport.hasText(stringValue(gasStation.get("phone")))) {
            localBusiness.put("telephone", stringValue(gasStation.get("phone")));
        }
        if (gasStation.get("latitude") != null && gasStation.get("longitude") != null) {
            localBusiness.put("geo", Map.of(
                    "@type", "GeoCoordinates",
                    "latitude", gasStation.get("latitude"),
                    "longitude", gasStation.get("longitude")
            ));
        }

        applySeo(model, PublicSeoMeta.builder()
                .title(title)
                .description(description)
                .canonicalUrl(PublicSeoSupport.absoluteUrl(path))
                .robots(PublicSeoSupport.robots(true))
                .ogType("article")
                .siteName(PublicSeoSupport.SITE_NAME)
                .locale(PublicSeoSupport.LOCALE)
                .twitterCard("summary")
                .structuredDataList(List.of(
                        PublicSeoSupport.toJson(localBusiness),
                        PublicSeoSupport.breadcrumbJson(List.of(
                                new PublicSeoBreadcrumbItem("홈", "/"),
                                new PublicSeoBreadcrumbItem("주유소 가격", "/grap/user/content/gas-stations"),
                                new PublicSeoBreadcrumbItem(title, path)
                        ))
                ))
                .build());
    }

    /**
     * 부동산 목록 SEO를 적용한다.
     *
     * @param model 화면 모델
     * @param keyword 검색어
     * @param dealYearMonth 조회 월
     * @param sort 정렬 조건
     * @param page 페이지 번호
     * @param currentPropertyMonth 현재 기준 월
     */
    public void applyRealEstateListSeo(
            Model model,
            String keyword,
            String dealYearMonth,
            String sort,
            Integer page,
            String currentPropertyMonth
    ) {
        boolean indexable = !PublicSeoSupport.hasText(keyword)
                && !PublicSeoSupport.hasText(dealYearMonth)
                && !PublicSeoSupport.hasText(sort)
                && (page == null || page <= 1);

        String monthLabel = PublicSeoSupport.hasText(currentPropertyMonth) && currentPropertyMonth.length() == 6
                ? currentPropertyMonth.substring(0, 4) + "년 " + currentPropertyMonth.substring(4) + "월 기준"
                : "최신 기준";

        String title = "제주도 부동산 실거래가";
        String description = monthLabel + " 제주도 부동산 아파트, 연립·다세대, 오피스텔, 단독·다가구 매매와 전월세 실거래가를 확인하고 대출 계산까지 살펴보세요.";

        applySeo(model, PublicSeoMeta.builder()
                .title(title)
                .description(description)
                .canonicalUrl(PublicSeoSupport.absoluteUrl(REAL_ESTATE_PATH))
                .robots(PublicSeoSupport.robots(indexable))
                .ogType("website")
                .siteName(PublicSeoSupport.SITE_NAME)
                .locale(PublicSeoSupport.LOCALE)
                .twitterCard("summary")
                .structuredDataList(List.of(
                        realEstateCollectionPageJson(title, description, REAL_ESTATE_PATH),
                        PublicSeoSupport.breadcrumbJson(List.of(
                                new PublicSeoBreadcrumbItem("홈", "/"),
                                new PublicSeoBreadcrumbItem("제주도 부동산 실거래가", REAL_ESTATE_PATH)
                        ))
                ))
                .build());
    }

    /**
     * 부동산 상세 SEO를 적용한다.
     *
     * @param model 화면 모델
     * @param property 부동산 데이터
     * @param requestedYear 조회 연도
     */
    public void applyRealEstateDetailSeo(Model model, Map<String, Object> property, Integer requestedYear) {
        String path = REAL_ESTATE_PATH + "/" + property.get("id");
        String title = realEstateDetailTitle(property);
        String description = firstNonBlank(
                joinNonBlank(" · ",
                        "제주도 부동산 실거래가",
                        stringValue(property.get("address")),
                        stringValue(property.get("formattedDisplayAmount")),
                        stringValue(property.get("formattedDealDate"))
                ),
                title + " 상세 페이지입니다."
        );
        String modifiedAt = PublicSeoSupport.toIsoDate(property.get("dealDate"));
        boolean indexable = requestedYear == null;

        applySeo(model, PublicSeoMeta.builder()
                .title(title)
                .description(description)
                .canonicalUrl(PublicSeoSupport.absoluteUrl(path))
                .robots(PublicSeoSupport.robots(indexable))
                .ogType("article")
                .siteName(PublicSeoSupport.SITE_NAME)
                .locale(PublicSeoSupport.LOCALE)
                .twitterCard("summary")
                .structuredDataList(List.of(
                        realEstateWebPageJson(title, description, path, modifiedAt),
                        PublicSeoSupport.breadcrumbJson(List.of(
                                new PublicSeoBreadcrumbItem("홈", "/"),
                                new PublicSeoBreadcrumbItem("제주도 부동산 실거래가", REAL_ESTATE_PATH),
                                new PublicSeoBreadcrumbItem(title, path)
                        ))
                ))
                .build());
    }

    /**
     * 사용자 등록 요청 폼 SEO를 적용한다.
     *
     * @param model 화면 모델
     * @param path 경로
     * @param title 제목
     * @param description 설명
     */
    public void applyRequestSeo(Model model, String path, String title, String description) {
        applySeo(model, PublicSeoMeta.builder()
                .title(title)
                .description(description)
                .canonicalUrl(PublicSeoSupport.absoluteUrl(path))
                .robots("noindex, nofollow")
                .ogType("website")
                .siteName(PublicSeoSupport.SITE_NAME)
                .locale(PublicSeoSupport.LOCALE)
                .twitterCard("summary")
                .structuredDataList(List.of(
                        PublicSeoSupport.webPageJson(title, description, path, null, null)
                ))
                .build());
    }

    private void applyListSeo(
            Model model,
            String path,
            String title,
            String description,
            boolean indexable,
            List<PublicSeoBreadcrumbItem> breadcrumbItems
    ) {
        applySeo(model, PublicSeoMeta.builder()
                .title(title)
                .description(description)
                .canonicalUrl(PublicSeoSupport.absoluteUrl(path))
                .robots(PublicSeoSupport.robots(indexable))
                .ogType("website")
                .siteName(PublicSeoSupport.SITE_NAME)
                .locale(PublicSeoSupport.LOCALE)
                .twitterCard("summary")
                .structuredDataList(List.of(
                        PublicSeoSupport.collectionPageJson(title, description, path),
                        PublicSeoSupport.breadcrumbJson(breadcrumbItems)
                ))
                .build());
    }

    private void applySeo(Model model, PublicSeoMeta seoMeta) {
        PublicSeoSupport.applySeo(model, seoMeta);
    }

    private String realEstateCollectionPageJson(String title, String description, String path) {
        Map<String, Object> data = PublicSeoSupport.linkedData("CollectionPage");
        data.put("name", title);
        data.put("description", description);
        data.put("url", PublicSeoSupport.absoluteUrl(path));
        data.put("inLanguage", "ko-KR");
        data.put("keywords", REAL_ESTATE_KEYWORDS);
        data.put("about", List.of(
                Map.of("@type", "AdministrativeArea", "name", "제주특별자치도"),
                Map.of("@type", "Thing", "name", "제주도 부동산"),
                Map.of("@type", "Thing", "name", "부동산 실거래가")
        ));
        data.put("spatialCoverage", Map.of(
                "@type", "AdministrativeArea",
                "name", "제주특별자치도"
        ));
        return PublicSeoSupport.toJson(data);
    }

    private String realEstateWebPageJson(String title, String description, String path, String modifiedAt) {
        Map<String, Object> data = PublicSeoSupport.linkedData("WebPage");
        data.put("name", title);
        data.put("description", description);
        data.put("url", PublicSeoSupport.absoluteUrl(path));
        data.put("inLanguage", "ko-KR");
        data.put("keywords", REAL_ESTATE_KEYWORDS);
        data.put("about", List.of(
                Map.of("@type", "AdministrativeArea", "name", "제주특별자치도"),
                Map.of("@type", "Thing", "name", "제주도 부동산"),
                Map.of("@type", "Thing", "name", "부동산 실거래가")
        ));
        if (PublicSeoSupport.hasText(modifiedAt)) {
            data.put("dateModified", modifiedAt);
        }
        return PublicSeoSupport.toJson(data);
    }

    private String realEstateDetailTitle(Map<String, Object> property) {
        return joinNonBlank(" ",
                "제주도 부동산",
                stringValue(property.get("sggName")),
                stringValue(property.get("umdName")),
                stringValue(property.get("displayName")),
                "실거래가"
        );
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String firstImage(List<String> images) {
        if (images == null || images.isEmpty()) {
            return "";
        }
        return images.get(0);
    }

    private List<String> castStringList(Object value) {
        if (value instanceof List<?> values) {
            return values.stream().map(String::valueOf).toList();
        }
        return List.of();
    }

    private String gasPriceSummary(Map<String, Object> gasStation) {
        List<String> prices = new ArrayList<>();
        addPrice(prices, "휘발유", gasStation.get("gasolinePrice"));
        addPrice(prices, "경유", gasStation.get("dieselPrice"));
        addPrice(prices, "LPG", gasStation.get("lpgPrice"));
        return String.join(", ", prices);
    }

    private void addPrice(List<String> prices, String label, Object value) {
        if (value instanceof Number number && number.intValue() > 0) {
            prices.add(label + " " + number.intValue() + "원");
        }
    }

    private String formatDateRange(Object startDate, Object endDate) {
        String start = PublicSeoSupport.toIsoDate(startDate);
        String end = PublicSeoSupport.toIsoDate(endDate);
        if (!PublicSeoSupport.hasText(start) && !PublicSeoSupport.hasText(end)) {
            return "";
        }
        if (!PublicSeoSupport.hasText(end)) {
            return start;
        }
        return start + " ~ " + end;
    }

    private String joinNonBlank(String delimiter, String... values) {
        List<String> availableValues = new ArrayList<>();
        for (String value : values) {
            if (PublicSeoSupport.hasText(value)) {
                availableValues.add(value.trim());
            }
        }
        return String.join(delimiter, availableValues);
    }

    private String firstNonBlank(String... values) {
        return PublicSeoSupport.firstNonBlank(values);
    }
}
