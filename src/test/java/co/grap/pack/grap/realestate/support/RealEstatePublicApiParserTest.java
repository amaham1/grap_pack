package co.grap.pack.grap.realestate.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RealEstatePublicApiParserTest {

    private RealEstatePublicApiParser parser;

    @BeforeEach
    void setUp() {
        parser = new RealEstatePublicApiParser(new ObjectMapper());
    }

    @Test
    void parserBuildsReadableFallbackNameForPlaceholderOfficetelNames() {
        String xml = xmlResponse(
                "<item>" +
                        "<offiNm>(155-4)</offiNm>" +
                        "<buildYear>2021</buildYear>" +
                        "<deposit>20000</deposit>" +
                        "<monthlyRent>80</monthlyRent>" +
                        "<dealDay>2</dealDay>" +
                        "<dealMonth>4</dealMonth>" +
                        "<dealYear>2026</dealYear>" +
                        "<excluUseAr>29.7</excluUseAr>" +
                        "<floor>9</floor>" +
                        "<jibun>155-4</jibun>" +
                        "<umdNm>강정동</umdNm>" +
                        "<sggNm>서귀포시</sggNm>" +
                        "</item>"
        );

        RealEstatePublicApiParser.ParsedPage parsedPage = parser.parse(
                xml,
                RealEstateDataset.OFFICETEL_RENT,
                "50130",
                "서귀포시",
                LocalDateTime.of(2026, 4, 22, 10, 0)
        );

        assertThat(parsedPage.items()).hasSize(1);
        assertThat(parsedPage.items().get(0).getDisplayName()).isEqualTo("강정동 오피스텔");
        assertThat(parsedPage.items().get(0).getDepositAmountManwon()).isEqualTo(20000);
        assertThat(parsedPage.items().get(0).getMonthlyRentManwon()).isEqualTo(80);
    }

    @Test
    void parserBuildsReadableDetachedHouseNamesFromGenericApiTitles() {
        String xml = xmlResponse(
                "<item>" +
                        "<houseType>단독</houseType>" +
                        "<buildYear>2026</buildYear>" +
                        "<deposit>5000</deposit>" +
                        "<monthlyRent>0</monthlyRent>" +
                        "<dealDay>20</dealDay>" +
                        "<dealMonth>4</dealMonth>" +
                        "<dealYear>2026</dealYear>" +
                        "<totalFloorAr>39</totalFloorAr>" +
                        "<jibun></jibun>" +
                        "<umdNm>조천읍 대흘리</umdNm>" +
                        "</item>"
        );

        RealEstatePublicApiParser.ParsedPage parsedPage = parser.parse(
                xml,
                RealEstateDataset.DETACHED_RENT,
                "50110",
                "제주시",
                LocalDateTime.of(2026, 4, 22, 10, 0)
        );

        assertThat(parsedPage.items()).hasSize(1);
        assertThat(parsedPage.items().get(0).getDisplayName()).isEqualTo("조천읍 대흘리 단독주택");
        assertThat(parsedPage.items().get(0).getDealYearMonth()).isEqualTo("202604");
    }

    private String xmlResponse(String itemXml) {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <response>
                    <header>
                        <resultCode>000</resultCode>
                        <resultMsg>NORMAL SERVICE.</resultMsg>
                    </header>
                    <body>
                        <totalCount>1</totalCount>
                        <items>
                """ + itemXml + """
                        </items>
                    </body>
                </response>
                """;
    }
}
