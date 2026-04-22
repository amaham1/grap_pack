package co.grap.pack.grap.realestate.support;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 부동산 공공데이터 API 설정.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "external-api.real-estate")
public class RealEstateApiProperties {

    private String baseUrl = "https://apis.data.go.kr/1613000";
    private String serviceKey = "";
    private int recentMonths = 3;

    public String getNormalizedServiceKey() {
        if (serviceKey == null || serviceKey.isBlank()) {
            return "";
        }

        try {
            return URLDecoder.decode(serviceKey.trim(), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            return serviceKey.trim();
        }
    }

    public String getEncodedServiceKey() {
        String normalized = getNormalizedServiceKey();
        if (normalized.isBlank()) {
            return "";
        }
        return URLEncoder.encode(normalized, StandardCharsets.UTF_8);
    }

    public URI buildUri(RealEstateDataset dataset, String lawdCode, String dealYearMonth, int pageNo, int numOfRows) {
        return UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .path(dataset.getEndpoint())
                .query("serviceKey=" + getEncodedServiceKey())
                .queryParam("LAWD_CD", lawdCode)
                .queryParam("DEAL_YMD", dealYearMonth)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("_type", "xml")
                .build(true)
                .toUri();
    }
}
