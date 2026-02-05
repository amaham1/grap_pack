package co.grap.pack.qrgen.seo;

import org.springframework.ui.Model;

/**
 * QRgen SEO 유틸리티
 * 공개/보호 페이지의 SEO 모델 속성을 설정
 */
public class QrGenSeoHelper {

    private static final String DOMAIN = "https://grap.co.kr";
    private static final String SITE_NAME = "Grap QR - 무료 QR 코드 생성기";
    private static final String DEFAULT_OG_IMAGE = DOMAIN + "/qrgen/images/og-image.png";

    private QrGenSeoHelper() {
    }

    /**
     * 공개 페이지 SEO 속성 설정
     *
     * @param model       Spring Model
     * @param path        페이지 경로 (예: "/qrgen/", "/qrgen/auth/login")
     * @param title       페이지 제목
     * @param description 페이지 설명
     */
    public static void setQrGenPublicPageSeo(Model model, String path, String title, String description) {
        model.addAttribute("seoTitle", title);
        model.addAttribute("seoDescription", description);
        model.addAttribute("seoCanonical", DOMAIN + path);
        model.addAttribute("seoRobots", "index, follow");
        model.addAttribute("seoOgType", "website");
        model.addAttribute("seoOgImage", DEFAULT_OG_IMAGE);
        model.addAttribute("seoSiteName", SITE_NAME);
    }

    /**
     * 보호 페이지 SEO 속성 설정 (noindex, nofollow)
     *
     * @param model Spring Model
     */
    public static void setQrGenProtectedPageSeo(Model model) {
        model.addAttribute("seoRobots", "noindex, nofollow");
    }
}
