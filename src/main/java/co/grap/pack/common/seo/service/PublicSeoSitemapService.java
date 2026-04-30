package co.grap.pack.common.seo.service;

import co.grap.pack.common.seo.mapper.PublicSeoMapper;
import co.grap.pack.common.seo.model.PublicSeoSitemapUrl;
import co.grap.pack.common.seo.support.PublicSeoSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 공개 SEO 사이트맵 서비스를 담당한다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicSeoSitemapService {

    private final PublicSeoMapper publicSeoMapper;

    /**
     * robots.txt 내용을 만든다.
     *
     * @return robots.txt 문자열
     */
    public String buildRobotsTxt() {
        return """
                User-agent: *
                Allow: /
                Disallow: /admin/
                Disallow: /grap/user/content/festivals/request
                Disallow: /grap/user/content/exhibitions/request
                Disallow: /grap/user/content/welfare/request
                Disallow: /qr-manage/super/
                Disallow: /qr-manage/shop/
                Disallow: /qrgen/auth/login
                Disallow: /qrgen/auth/logout
                Disallow: /qrgen/user/
                Disallow: /qrgen/generate
                Disallow: /qrgen/preview
                Disallow: /api/

                Sitemap: %s/sitemap.xml
                """.formatted(PublicSeoSupport.DOMAIN);
    }

    /**
     * sitemap.xml 인덱스 내용을 만든다.
     *
     * @return sitemap.xml 인덱스 문자열
     */
    public String buildSitemapXml() {
        List<SitemapIndexEntry> entries = new ArrayList<>();
        entries.add(new SitemapIndexEntry("/sitemap-static.xml", ""));
        entries.add(new SitemapIndexEntry("/sitemap-content.xml", ""));
        return buildSitemapIndexXml(entries);
    }

    /**
     * 정적 URL sitemap 내용을 만든다.
     *
     * @return 정적 URL sitemap 문자열
     */
    public String buildStaticSitemapXml() {
        String realEstateLastModified = "";
        try {
            realEstateLastModified = publicSeoMapper.selectRealEstateSitemapLastModified();
        } catch (DataAccessException exception) {
            log.warn("Public real estate sitemap lastmod is not available. Serving static sitemap without it.", exception);
        }
        return buildUrlsetXml(createStaticUrls(realEstateLastModified));
    }

    /**
     * 일반 콘텐츠 sitemap 내용을 만든다.
     *
     * @return 일반 콘텐츠 sitemap 문자열
     */
    public String buildContentSitemapXml() {
        List<PublicSeoSitemapUrl> urls = new ArrayList<>();
        try {
            urls.addAll(publicSeoMapper.selectGeneralDynamicSitemapUrls());
        } catch (DataAccessException exception) {
            log.warn("Public content sitemap URLs are not available. Serving empty content sitemap.", exception);
        }
        return buildUrlsetXml(urls);
    }

    private String buildSitemapIndexXml(List<SitemapIndexEntry> entries) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuilder.append("<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        for (SitemapIndexEntry entry : entries) {
            xmlBuilder.append("    <sitemap>\n");
            xmlBuilder.append("        <loc>")
                    .append(HtmlUtils.htmlEscape(PublicSeoSupport.absoluteUrl(entry.path())))
                    .append("</loc>\n");
            if (PublicSeoSupport.hasText(entry.lastModified())) {
                xmlBuilder.append("        <lastmod>")
                        .append(HtmlUtils.htmlEscape(entry.lastModified()))
                        .append("</lastmod>\n");
            }
            xmlBuilder.append("    </sitemap>\n");
        }

        xmlBuilder.append("</sitemapindex>\n");
        return xmlBuilder.toString();
    }

    private String buildUrlsetXml(List<PublicSeoSitemapUrl> urls) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuilder.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        for (PublicSeoSitemapUrl url : urls) {
            xmlBuilder.append("    <url>\n");
            xmlBuilder.append("        <loc>")
                    .append(HtmlUtils.htmlEscape(PublicSeoSupport.absoluteUrl(url.getPath())))
                    .append("</loc>\n");

            if (PublicSeoSupport.hasText(url.getLastModified())) {
                xmlBuilder.append("        <lastmod>")
                        .append(HtmlUtils.htmlEscape(url.getLastModified()))
                        .append("</lastmod>\n");
            }

            if (PublicSeoSupport.hasText(url.getChangeFrequency())) {
                xmlBuilder.append("        <changefreq>")
                        .append(HtmlUtils.htmlEscape(url.getChangeFrequency()))
                        .append("</changefreq>\n");
            }

            if (PublicSeoSupport.hasText(url.getPriority())) {
                xmlBuilder.append("        <priority>")
                        .append(HtmlUtils.htmlEscape(url.getPriority()))
                        .append("</priority>\n");
            }

            xmlBuilder.append("    </url>\n");
        }

        xmlBuilder.append("</urlset>\n");
        return xmlBuilder.toString();
    }

    private List<PublicSeoSitemapUrl> createStaticUrls(String realEstateLastModified) {
        List<PublicSeoSitemapUrl> urls = new ArrayList<>();
        urls.add(staticUrl("/", "", "weekly", "1.0"));
        urls.add(staticUrl("/grap/user/content/list", "", "daily", "0.9"));
        urls.add(staticUrl("/grap/user/content/festivals", "", "daily", "0.9"));
        urls.add(staticUrl("/grap/user/content/exhibitions", "", "daily", "0.9"));
        urls.add(staticUrl("/grap/user/content/welfare", "", "daily", "0.8"));
        urls.add(staticUrl("/grap/user/content/gas-stations", "", "daily", "0.8"));
        urls.add(staticUrl("/grap/user/content/real-estate", realEstateLastModified, "daily", "0.9"));
        urls.add(staticUrl("/qrgen/", "", "weekly", "0.8"));
        urls.add(staticUrl("/qrgen/auth/register", "", "monthly", "0.5"));
        return urls;
    }

    private PublicSeoSitemapUrl staticUrl(String path, String lastModified, String changeFrequency, String priority) {
        PublicSeoSitemapUrl url = new PublicSeoSitemapUrl();
        url.setPath(path);
        url.setLastModified(lastModified);
        url.setChangeFrequency(changeFrequency);
        url.setPriority(priority);
        return url;
    }

    private record SitemapIndexEntry(String path, String lastModified) {
    }
}
