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

import java.time.LocalDate;
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
     * sitemap.xml 내용을 만든다.
     *
     * @return sitemap.xml 문자열
     */
    public String buildSitemapXml() {
        List<PublicSeoSitemapUrl> urls = new ArrayList<>(createStaticUrls());
        try {
            urls.addAll(publicSeoMapper.selectDynamicSitemapUrls());
        } catch (DataAccessException exception) {
            log.warn("Public sitemap dynamic URLs are not fully available. Serving static sitemap only.", exception);
        }

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

    private List<PublicSeoSitemapUrl> createStaticUrls() {
        String today = LocalDate.now().toString();
        List<PublicSeoSitemapUrl> urls = new ArrayList<>();
        urls.add(staticUrl("/", today, "weekly", "1.0"));
        urls.add(staticUrl("/grap/user/content/list", today, "daily", "0.9"));
        urls.add(staticUrl("/grap/user/content/festivals", today, "daily", "0.9"));
        urls.add(staticUrl("/grap/user/content/exhibitions", today, "daily", "0.9"));
        urls.add(staticUrl("/grap/user/content/welfare", today, "daily", "0.8"));
        urls.add(staticUrl("/grap/user/content/gas-stations", today, "daily", "0.8"));
        urls.add(staticUrl("/grap/user/content/real-estate", today, "daily", "0.9"));
        urls.add(staticUrl("/qrgen/", today, "weekly", "0.8"));
        urls.add(staticUrl("/qrgen/auth/register", today, "monthly", "0.5"));
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
}
