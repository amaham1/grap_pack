package co.grap.pack.common.seo.mapper;

import co.grap.pack.common.seo.model.PublicSeoSitemapUrl;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 공개 SEO 전용 조회 매퍼다.
 */
@Mapper
public interface PublicSeoMapper {

    /**
     * 동적 사이트맵 URL 목록을 조회한다.
     *
     * @return 사이트맵 URL 목록
     */
    List<PublicSeoSitemapUrl> selectDynamicSitemapUrls();
}
