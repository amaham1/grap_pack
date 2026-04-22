package co.grap.pack.qrgen.seo;

import co.grap.pack.common.seo.model.PublicSeoBreadcrumbItem;
import co.grap.pack.common.seo.model.PublicSeoMeta;
import co.grap.pack.common.seo.support.PublicSeoSupport;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

/**
 * QRgen 공개 페이지 SEO 유틸리티다.
 */
public final class QrGenSeoHelper {

    private static final String FAVICON_URL = PublicSeoSupport.absoluteUrl("/qrgen/images/favicon.svg");

    private QrGenSeoHelper() {
    }

    /**
     * QRgen 메인 SEO를 적용한다.
     *
     * @param model 화면 모델
     */
    public static void setQrGenHomeSeo(Model model) {
        String title = "무료 QR 코드 생성기";
        String description = "URL, 텍스트, 와이파이, 이메일, 전화번호 QR 코드를 무료로 빠르게 만들고 내려받을 수 있습니다.";

        Map<String, Object> appData = PublicSeoSupport.linkedData("WebApplication");
        appData.put("name", "Grap QR");
        appData.put("url", PublicSeoSupport.absoluteUrl("/qrgen/"));
        appData.put("applicationCategory", "UtilitiesApplication");
        appData.put("operatingSystem", "Web");
        appData.put("description", description);

        applySeo(model, PublicSeoMeta.builder()
                .title(title)
                .description(description)
                .canonicalUrl(PublicSeoSupport.absoluteUrl("/qrgen/"))
                .robots(PublicSeoSupport.robots(true))
                .ogType("website")
                .ogImageUrl(FAVICON_URL)
                .siteName("Grap QR")
                .locale(PublicSeoSupport.LOCALE)
                .twitterCard(PublicSeoSupport.twitterCard(FAVICON_URL))
                .structuredDataList(List.of(
                        PublicSeoSupport.toJson(appData),
                        PublicSeoSupport.breadcrumbJson(List.of(
                                new PublicSeoBreadcrumbItem("홈", "/"),
                                new PublicSeoBreadcrumbItem("QR 코드 생성기", "/qrgen/")
                        ))
                ))
                .build());
    }

    /**
     * QRgen 로그인 SEO를 적용한다.
     *
     * @param model 화면 모델
     */
    public static void setQrGenLoginSeo(Model model) {
        String title = "QR 생성 기록 로그인";
        String description = "기존 QR 생성 기록을 확인하려면 Grap QR 계정으로 로그인하세요.";

        applySeo(model, PublicSeoMeta.builder()
                .title(title)
                .description(description)
                .canonicalUrl(PublicSeoSupport.absoluteUrl("/qrgen/auth/login"))
                .robots("noindex, nofollow")
                .ogType("website")
                .ogImageUrl(FAVICON_URL)
                .siteName("Grap QR")
                .locale(PublicSeoSupport.LOCALE)
                .twitterCard(PublicSeoSupport.twitterCard(FAVICON_URL))
                .structuredDataList(List.of(
                        PublicSeoSupport.webPageJson(title, description, "/qrgen/auth/login", FAVICON_URL, null)
                ))
                .build());
    }

    /**
     * QRgen 회원가입 SEO를 적용한다.
     *
     * @param model 화면 모델
     */
    public static void setQrGenRegisterSeo(Model model) {
        String title = "QR 코드 생성기 회원가입";
        String description = "Grap QR에 가입하고 QR 생성 기록 저장, 재사용, 히스토리 관리 기능을 이용하세요.";

        applySeo(model, PublicSeoMeta.builder()
                .title(title)
                .description(description)
                .canonicalUrl(PublicSeoSupport.absoluteUrl("/qrgen/auth/register"))
                .robots(PublicSeoSupport.robots(true))
                .ogType("website")
                .ogImageUrl(FAVICON_URL)
                .siteName("Grap QR")
                .locale(PublicSeoSupport.LOCALE)
                .twitterCard(PublicSeoSupport.twitterCard(FAVICON_URL))
                .structuredDataList(List.of(
                        PublicSeoSupport.webPageJson(title, description, "/qrgen/auth/register", FAVICON_URL, null),
                        PublicSeoSupport.breadcrumbJson(List.of(
                                new PublicSeoBreadcrumbItem("홈", "/"),
                                new PublicSeoBreadcrumbItem("QR 코드 생성기", "/qrgen/"),
                                new PublicSeoBreadcrumbItem("회원가입", "/qrgen/auth/register")
                        ))
                ))
                .build());
    }

    /**
     * 보호 페이지 SEO를 적용한다.
     *
     * @param model 화면 모델
     * @param title 제목
     */
    public static void setQrGenProtectedPageSeo(Model model, String title) {
        applySeo(model, PublicSeoMeta.builder()
                .title(title)
                .description("로그인 사용자 전용 QR 생성 기록 페이지입니다.")
                .robots("noindex, nofollow")
                .ogType("website")
                .siteName("Grap QR")
                .locale(PublicSeoSupport.LOCALE)
                .twitterCard("summary")
                .structuredDataList(List.of())
                .build());
    }

    private static void applySeo(Model model, PublicSeoMeta seoMeta) {
        PublicSeoSupport.applySeo(model, seoMeta);
    }
}
