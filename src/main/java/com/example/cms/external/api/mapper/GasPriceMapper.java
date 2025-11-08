package com.example.cms.external.api.mapper;

import com.example.cms.external.api.model.GasPrice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 주유소 가격 Mapper
 */
@Mapper
public interface GasPriceMapper {
    /**
     * 주유소 가격 정보를 저장합니다.
     */
    void insert(GasPrice gasPrice);

    /**
     * 주유소 가격 정보를 업데이트합니다.
     */
    void update(GasPrice gasPrice);

    /**
     * opinet_id와 price_date로 가격 정보를 조회합니다.
     */
    GasPrice findByOpinetIdAndDate(@Param("opinetId") String opinetId, @Param("priceDate") LocalDate priceDate);

    /**
     * opinet_id로 가격 목록을 조회합니다.
     */
    List<GasPrice> findByOpinetId(@Param("opinetId") String opinetId);

    /**
     * 특정 날짜의 모든 주유소 가격을 조회합니다.
     */
    List<GasPrice> findByDate(@Param("priceDate") LocalDate priceDate);

    /**
     * 최신 가격 정보를 조회합니다.
     */
    List<GasPrice> findLatest();

    /**
     * 주유소 가격을 upsert합니다.
     * (존재하면 update, 없으면 insert)
     */
    void upsert(GasPrice gasPrice);
}
