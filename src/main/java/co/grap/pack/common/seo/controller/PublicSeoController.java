package co.grap.pack.common.seo.controller;

import co.grap.pack.common.seo.service.PublicSeoSitemapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 공개 SEO 엔드포인트를 제공하는 컨트롤러다.
 */
@Controller
@RequiredArgsConstructor
public class PublicSeoController {

    private final PublicSeoSitemapService publicSeoSitemapService;

    /**
     * robots.txt를 제공한다.
     *
     * @return robots.txt 문자열
     */
    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String robotsTxt() {
        return publicSeoSitemapService.buildRobotsTxt();
    }

    /**
     * sitemap.xml 인덱스를 제공한다.
     *
     * @return sitemap.xml 인덱스 문자열
     */
    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String sitemapXml() {
        return publicSeoSitemapService.buildSitemapXml();
    }

    /**
     * 정적 URL sitemap을 제공한다.
     *
     * @return 정적 URL sitemap 문자열
     */
    @GetMapping(value = "/sitemap-static.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String staticSitemapXml() {
        return publicSeoSitemapService.buildStaticSitemapXml();
    }

    /**
     * 일반 콘텐츠 sitemap을 제공한다.
     *
     * @return 일반 콘텐츠 sitemap 문자열
     */
    @GetMapping(value = "/sitemap-content.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String contentSitemapXml() {
        return publicSeoSitemapService.buildContentSitemapXml();
    }

}
