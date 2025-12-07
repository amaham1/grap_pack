package co.grap.pack.qrmanage.shopadmin.category.mapper;

import co.grap.pack.qrmanage.shopadmin.category.model.QrManageCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 카테고리 Mapper
 */
@Mapper
public interface QrManageCategoryMapper {

    /**
     * 상점의 카테고리 목록 조회
     */
    List<QrManageCategory> findByShopId(@Param("shopId") Long shopId);

    /**
     * 공개된 카테고리만 조회
     */
    List<QrManageCategory> findVisibleByShopId(@Param("shopId") Long shopId);

    /**
     * ID로 카테고리 조회
     */
    QrManageCategory findById(@Param("id") Long id);

    /**
     * 카테고리 등록
     */
    void insert(QrManageCategory category);

    /**
     * 카테고리 수정
     */
    void update(QrManageCategory category);

    /**
     * 카테고리 삭제
     */
    void delete(@Param("id") Long id);

    /**
     * 정렬 순서 변경
     */
    void updateSortOrder(@Param("id") Long id, @Param("sortOrder") Integer sortOrder);

    /**
     * 공개 여부 변경
     */
    void updateVisibility(@Param("id") Long id, @Param("isVisible") Boolean isVisible);

    /**
     * 다음 정렬 순서 조회
     */
    Integer getNextSortOrder(@Param("shopId") Long shopId);

    /**
     * 상점 ID와 카테고리 ID로 카테고리 존재 확인
     */
    boolean existsByIdAndShopId(@Param("id") Long id, @Param("shopId") Long shopId);
}
