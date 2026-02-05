package co.grap.pack.qrgen.seo;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * QRgen SEO 컨트롤러
 * robots.txt, sitemap.xml 엔드포인트 제공
 */
@Controller
public class QrGenSeoController {

    private static final String DOMAIN = "https://grap.co.kr";

    /**
     * robots.txt 제공
     */
    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String robotsTxt() {
        return """
                User-agent: *
                Allow: /qrgen/
                Allow: /qrgen/auth/login
                Allow: /qrgen/auth/register
                Disallow: /qrgen/user/
                Disallow: /qrgen/generate
                Disallow: /qrgen/download
                Disallow: /qrgen/visitor/
                Disallow: /qrgen/auth/logout
                Disallow: /grap/
                Disallow: /qr-manage/

                Sitemap: %s/sitemap.xml
                """.formatted(DOMAIN);
    }

    /**
     * sitemap.xml 제공
     */
    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String sitemapXml() {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
                    <url>
                        <loc>%1$s/qrgen/</loc>
                        <changefreq>weekly</changefreq>
                        <priority>1.0</priority>
                    </url>
                    <url>
                        <loc>%1$s/qrgen/auth/login</loc>
                        <changefreq>monthly</changefreq>
                        <priority>0.5</priority>
                    </url>
                    <url>
                        <loc>%1$s/qrgen/auth/register</loc>
                        <changefreq>monthly</changefreq>
                        <priority>0.5</priority>
                    </url>
                </urlset>
                """.formatted(DOMAIN);
    }
}
