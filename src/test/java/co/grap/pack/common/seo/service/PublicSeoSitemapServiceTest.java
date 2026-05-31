package co.grap.pack.common.seo.service;

import co.grap.pack.common.seo.mapper.PublicSeoMapper;
import co.grap.pack.common.seo.model.PublicSeoSitemapUrl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.BadSqlGrammarException;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * 공개 사이트맵 서비스 테스트다.
 */
@ExtendWith(MockitoExtension.class)
class PublicSeoSitemapServiceTest {

    @Mock
    private PublicSeoMapper publicSeoMapper;

    @InjectMocks
    private PublicSeoSitemapService publicSeoSitemapService;

    /**
     * sitemap.xml이 핵심 sitemap만 안내하고 대량 부동산 상세 URL을 제외하는지 검증한다.
     */
    @Test
    void buildSitemapXmlIncludesCompactSitemapIndex() {
        String sitemapXml = publicSeoSitemapService.buildSitemapXml();

        assertThat(sitemapXml).contains("<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
        assertThat(sitemapXml).contains("<loc>https://grap.co.kr/sitemap-static.xml</loc>");
        assertThat(sitemapXml).contains("<loc>https://grap.co.kr/sitemap-content.xml</loc>");
        assertThat(sitemapXml).doesNotContain("<lastmod>");
        assertThat(sitemapXml).doesNotContain("sitemap-real-estate");
        assertThat(sitemapXml).doesNotContain("/grap/user/content/real-estate/120</loc>");
    }

    /**
     * 정적 URL sitemap에 공개 핵심 경로가 포함되는지 검증한다.
     */
    @Test
    void buildStaticSitemapXmlIncludesStaticUrls() {
        when(publicSeoMapper.selectRealEstateSitemapLastModified()).thenReturn("2026-04-24");

        String sitemapXml = publicSeoSitemapService.buildStaticSitemapXml();

        assertThat(sitemapXml).contains("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
        assertThat(sitemapXml).contains("<loc>https://grap.co.kr/</loc>");
        assertThat(sitemapXml).contains("<loc>https://grap.co.kr/grap/user/content/real-estate</loc>");
        assertThat(sitemapXml).contains("<lastmod>2026-04-24</lastmod>");
        assertThat(sitemapXml).doesNotContain("<lastmod>2026-04-30</lastmod>");
    }

    /**
     * 일반 콘텐츠 sitemap에 부동산을 제외한 동적 URL이 포함되는지 검증한다.
     */
    @Test
    void buildContentSitemapXmlIncludesGeneralDynamicUrls() {
        PublicSeoSitemapUrl dynamicUrl = new PublicSeoSitemapUrl();
        dynamicUrl.setPath("/grap/user/content/detail/99");
        dynamicUrl.setLastModified("2026-04-22");
        dynamicUrl.setChangeFrequency("weekly");
        dynamicUrl.setPriority("0.8");

        when(publicSeoMapper.selectGeneralDynamicSitemapUrls()).thenReturn(List.of(dynamicUrl));

        String sitemapXml = publicSeoSitemapService.buildContentSitemapXml();

        assertThat(sitemapXml).contains("<loc>https://grap.co.kr/grap/user/content/detail/99</loc>");
        assertThat(sitemapXml).contains("<changefreq>weekly</changefreq>");
        assertThat(sitemapXml).doesNotContain("/grap/user/content/real-estate/120");
    }

    /**
     * 일반 콘텐츠 URL 조회가 실패해도 빈 sitemap을 반환하는지 검증한다.
     */
    @Test
    void buildContentSitemapXmlFallsBackToEmptyUrlsetWhenDynamicQueryFails() {
        when(publicSeoMapper.selectGeneralDynamicSitemapUrls()).thenThrow(
                new BadSqlGrammarException("selectGeneralDynamicSitemapUrls", "SELECT", new SQLException("table missing"))
        );

        String sitemapXml = publicSeoSitemapService.buildContentSitemapXml();

        assertThat(sitemapXml).contains("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
        assertThat(sitemapXml).doesNotContain("<url>");
    }
}
