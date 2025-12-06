package co.grap.pack.user.content.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 사용자 주유소 Mapper
 */
@Mapper
public interface UserGasStationMapper {

    /**
     * 주유소 및 최신 가격 목록 조회
     */
    List<Map<String, Object>> selectGasStationListWithLatestPrice(@Param("keyword") String keyword,
                                                                    @Param("offset") int offset,
                                                                    @Param("size") int size);

    /**
     * 주유소 전체 개수 조회
     */
    int selectGasStationCount(@Param("keyword") String keyword);

    /**
     * 주유소 및 최신 가격 상세 조회
     */
    Map<String, Object> selectGasStationByIdWithLatestPrice(@Param("id") Long id);

    /**
     * 특정 주유소의 가격 이력 조회 (최근 30일)
     */
    List<Map<String, Object>> selectGasPriceHistory(@Param("opinetId") String opinetId,
                                                      @Param("limit") int limit);
}
