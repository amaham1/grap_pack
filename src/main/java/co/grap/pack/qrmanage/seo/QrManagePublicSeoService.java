package co.grap.pack.qrmanage.seo;

import co.grap.pack.common.seo.model.PublicSeoBreadcrumbItem;
import co.grap.pack.common.seo.model.PublicSeoMeta;
import co.grap.pack.common.seo.support.PublicSeoSupport;
import co.grap.pack.qrmanage.shopadmin.menu.model.QrManageMenu;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShop;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * QR Manage 고객용 페이지 SEO를 담당한다.
 */
@Service
public class QrManagePublicSeoService {

    /**
     * 상점 소개 페이지 SEO를 적용한다.
     *
     * @param model 화면 모델
     * @param qrCode QR 코드 문자열
     * @param shop 상점 정보
     */
    public void applyShopSeo(Model model, String qrCode, QrManageShop shop) {
        String path = "/qr-manage/view/shop/" + qrCode;
        String description = PublicSeoSupport.firstNonBlank(
                PublicSeoSupport.summarize(shop.getDescription(), 130),
                joinNonBlank(" · ", shop.getAddress(), shop.getPhone()),
                shop.getName() + " QR 안내 페이지입니다."
        );

        PublicSeoSupport.applySeo(model, PublicSeoMeta.builder()
                .title(shop.getName())
                .description(description)
                .canonicalUrl(PublicSeoSupport.absoluteUrl(path))
                .robots("noindex, nofollow")
                .ogType("website")
                .siteName(PublicSeoSupport.SITE_NAME)
                .locale(PublicSeoSupport.LOCALE)
                .twitterCard("summary")
                .structuredDataList(List.of(
                        PublicSeoSupport.toJson(localBusinessData(shop, path, description)),
                        PublicSeoSupport.breadcrumbJson(List.of(
                                new PublicSeoBreadcrumbItem("홈", "/"),
                                new PublicSeoBreadcrumbItem("QR 매장 안내", path)
                        ))
                ))
                .build());
    }

    /**
     * 메뉴 목록 페이지 SEO를 적용한다.
     *
     * @param model 화면 모델
     * @param qrCode QR 코드 문자열
     * @param shop 상점 정보
     */
    public void applyMenuListSeo(Model model, String qrCode, QrManageShop shop) {
        String path = "/qr-manage/view/menu/" + qrCode;
        String title = shop.getName() + " 메뉴판";
        String description = PublicSeoSupport.firstNonBlank(
                PublicSeoSupport.summarize(shop.getDescription(), 120),
                shop.getName() + "의 메뉴와 카테고리 정보를 확인하는 QR 메뉴판 페이지입니다."
        );

        PublicSeoSupport.applySeo(model, PublicSeoMeta.builder()
                .title(title)
                .description(description)
                .canonicalUrl(PublicSeoSupport.absoluteUrl(path))
                .robots("noindex, nofollow")
                .ogType("website")
                .siteName(PublicSeoSupport.SITE_NAME)
                .locale(PublicSeoSupport.LOCALE)
                .twitterCard("summary")
                .structuredDataList(List.of(
                        PublicSeoSupport.collectionPageJson(title, description, path),
                        PublicSeoSupport.toJson(localBusinessData(shop, path, description)),
                        PublicSeoSupport.breadcrumbJson(List.of(
                                new PublicSeoBreadcrumbItem("홈", "/"),
                                new PublicSeoBreadcrumbItem("QR 메뉴판", path)
                        ))
                ))
                .build());
    }

    /**
     * 메뉴 상세 페이지 SEO를 적용한다.
     *
     * @param model 화면 모델
     * @param qrCode QR 코드 문자열
     * @param shop 상점 정보
     * @param menu 메뉴 정보
     */
    public void applyMenuDetailSeo(Model model, String qrCode, QrManageShop shop, QrManageMenu menu) {
        String path = "/qr-manage/view/menu/" + qrCode + "/" + menu.getId();
        String description = PublicSeoSupport.firstNonBlank(
                PublicSeoSupport.summarize(menu.getDescription(), 140),
                joinNonBlank(" · ", shop != null ? shop.getName() : "", menu.getCategoryName()),
                menu.getName() + " 메뉴 상세 페이지입니다."
        );
        String imageUrl = menu.getPrimaryImageUrl();

        Map<String, Object> productData = PublicSeoSupport.linkedData("Product");
        productData.put("name", menu.getName());
        productData.put("description", description);
        productData.put("url", PublicSeoSupport.absoluteUrl(path));
        if (PublicSeoSupport.hasText(imageUrl)) {
            productData.put("image", List.of(PublicSeoSupport.absoluteUrl(imageUrl)));
        }
        if (shop != null) {
            productData.put("brand", Map.of(
                    "@type", "Brand",
                    "name", shop.getName()
            ));
        }
        productData.put("offers", Map.of(
                "@type", "Offer",
                "priceCurrency", "KRW",
                "price", menu.getPrice(),
                "availability", Boolean.TRUE.equals(menu.getIsSoldOut())
                        ? "https://schema.org/OutOfStock"
                        : "https://schema.org/InStock"
        ));

        PublicSeoSupport.applySeo(model, PublicSeoMeta.builder()
                .title(menu.getName())
                .description(description)
                .canonicalUrl(PublicSeoSupport.absoluteUrl(path))
                .robots("noindex, nofollow")
                .ogType("product")
                .ogImageUrl(PublicSeoSupport.hasText(imageUrl) ? PublicSeoSupport.absoluteUrl(imageUrl) : null)
                .siteName(PublicSeoSupport.SITE_NAME)
                .locale(PublicSeoSupport.LOCALE)
                .twitterCard(PublicSeoSupport.twitterCard(imageUrl))
                .structuredDataList(List.of(
                        PublicSeoSupport.toJson(productData),
                        PublicSeoSupport.breadcrumbJson(List.of(
                                new PublicSeoBreadcrumbItem("홈", "/"),
                                new PublicSeoBreadcrumbItem("QR 메뉴판", "/qr-manage/view/menu/" + qrCode),
                                new PublicSeoBreadcrumbItem(menu.getName(), path)
                        ))
                ))
                .build());
    }

    private Map<String, Object> localBusinessData(QrManageShop shop, String path, String description) {
        Map<String, Object> businessData = PublicSeoSupport.linkedData("LocalBusiness");
        businessData.put("name", shop.getName());
        businessData.put("description", description);
        businessData.put("url", PublicSeoSupport.absoluteUrl(path));
        if (PublicSeoSupport.hasText(shop.getAddress())) {
            businessData.put("address", shop.getAddress());
        }
        if (PublicSeoSupport.hasText(shop.getPhone())) {
            businessData.put("telephone", shop.getPhone());
        }
        return businessData;
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
}
