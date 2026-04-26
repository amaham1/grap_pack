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
     * 정적 URL과 동적 URL이 함께 사이트맵에 포함되는지 검증한다.
     */
    @Test
    void buildSitemapXmlIncludesStaticAndDynamicUrls() {
        PublicSeoSitemapUrl dynamicUrl = new PublicSeoSitemapUrl();
        dynamicUrl.setPath("/grap/user/content/detail/99");
        dynamicUrl.setLastModified("2026-04-22");
        dynamicUrl.setChangeFrequency("weekly");
        dynamicUrl.setPriority("0.8");

        PublicSeoSitemapUrl realEstateUrl = new PublicSeoSitemapUrl();
        realEstateUrl.setPath("/grap/user/content/real-estate/120");
        realEstateUrl.setLastModified("2026-04-24");
        realEstateUrl.setChangeFrequency("monthly");
        realEstateUrl.setPriority("0.8");

        when(publicSeoMapper.selectDynamicSitemapUrls()).thenReturn(List.of(dynamicUrl, realEstateUrl));

        String sitemapXml = publicSeoSitemapService.buildSitemapXml();

        assertThat(sitemapXml).contains("<loc>https://grap.co.kr/</loc>");
        assertThat(sitemapXml).contains("<loc>https://grap.co.kr/grap/user/content/detail/99</loc>");
        assertThat(sitemapXml).contains("<loc>https://grap.co.kr/grap/user/content/real-estate/120</loc>");
        assertThat(sitemapXml).contains("<changefreq>weekly</changefreq>");
        assertThat(sitemapXml).contains("<changefreq>monthly</changefreq>");
    }

    /**
     * 동적 URL 조회가 실패해도 정적 사이트맵은 유지되는지 검증한다.
     */
    @Test
    void buildSitemapXmlFallsBackToStaticUrlsWhenDynamicQueryFails() {
        when(publicSeoMapper.selectDynamicSitemapUrls()).thenThrow(
                new BadSqlGrammarException("selectDynamicSitemapUrls", "SELECT", new SQLException("table missing"))
        );

        String sitemapXml = publicSeoSitemapService.buildSitemapXml();

        assertThat(sitemapXml).contains("<loc>https://grap.co.kr/grap/user/content/festivals</loc>");
        assertThat(sitemapXml).doesNotContain("/grap/user/content/detail/99");
    }
}
