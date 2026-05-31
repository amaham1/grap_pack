package co.grap.pack.grap.user.content.mapper;

import co.grap.pack.grap.user.content.model.CmsUserRealEstateSearchParam;
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

    List<String> selectAvailableSggNames(@Param("dealYearMonth") String dealYearMonth);

    List<String> selectAvailableUmdNames(@Param("dealYearMonth") String dealYearMonth,
                                         @Param("sggName") String sggName);

    int selectRealEstateCount(@Param("searchParam") CmsUserRealEstateSearchParam searchParam);

    List<Map<String, Object>> selectRealEstateList(@Param("searchParam") CmsUserRealEstateSearchParam searchParam,
                                                   @Param("offset") int offset,
                                                   @Param("size") int size);

    Map<String, Object> selectRealEstateById(@Param("id") Long id);

    List<Integer> selectPropertyAvailableYears(@Param("propertyMatchKey") String propertyMatchKey);

    List<Map<String, Object>> selectPropertyMonthlyAverageHistory(@Param("propertyMatchKey") String propertyMatchKey,
                                                                  @Param("year") Integer year,
                                                                  @Param("metricType") String metricType);

    List<Map<String, Object>> selectSamePropertyRecentTransactions(@Param("propertyMatchKey") String propertyMatchKey,
                                                                   @Param("excludeId") Long excludeId,
                                                                   @Param("limit") int limit);

    List<Map<String, Object>> selectSimilarConditionTransactions(@Param("property") Map<String, Object> property,
                                                                 @Param("limit") int limit);
}
