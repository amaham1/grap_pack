package co.grap.pack.common.seo.model;

import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * 사이트맵 URL 정보를 담는 모델이다.
 */
@Data
@Alias("PublicSeoSitemapUrl")
public class PublicSeoSitemapUrl {

    /**
     * 상대 경로다.
     */
    private String path;

    /**
     * 마지막 수정일 문자열이다.
     */
    private String lastModified;

    /**
     * 변경 주기다.
     */
    private String changeFrequency;

    /**
     * 우선순위다.
     */
    private String priority;
}
