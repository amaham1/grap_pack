package co.grap.pack.grap.user.content.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 사용자 부동산 조회 Mapper.
 */
@Mapper
public interface CmsUserRealEstateMapper {

    String selectLatestDealYearMonth();

    List<String> selectAvailableDealYearMonths();

    int selectRealEstateCount(@Param("keyword") String keyword,
                              @Param("dealYearMonth") String dealYearMonth);

    List<Map<String, Object>> selectRealEstateList(@Param("keyword") String keyword,
                                                   @Param("dealYearMonth") String dealYearMonth,
                                                   @Param("sort") String sort,
                                                   @Param("offset") int offset,
                                                   @Param("size") int size);

    Map<String, Object> selectRealEstateById(@Param("id") Long id);

    List<Integer> selectPropertyAvailableYears(@Param("propertyMatchKey") String propertyMatchKey);

    List<Map<String, Object>> selectPropertyMonthlyAverageHistory(@Param("propertyMatchKey") String propertyMatchKey,
                                                                  @Param("year") Integer year,
                                                                  @Param("metricType") String metricType);
}
