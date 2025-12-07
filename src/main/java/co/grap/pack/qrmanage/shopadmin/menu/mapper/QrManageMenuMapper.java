package co.grap.pack.qrmanage.shopadmin.menu.mapper;

import co.grap.pack.qrmanage.shopadmin.menu.model.QrManageMenu;
import co.grap.pack.qrmanage.shopadmin.menu.model.QrManageMenuOption;
import co.grap.pack.qrmanage.shopadmin.menu.model.QrManageMenuOptionGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 메뉴 Mapper
 */
@Mapper
public interface QrManageMenuMapper {

    // ========== 메뉴 ==========

    /**
     * 상점의 메뉴 목록 조회
     */
    List<QrManageMenu> findByShopId(@Param("shopId") Long shopId);

    /**
     * 카테고리별 메뉴 목록 조회
     */
    List<QrManageMenu> findByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 공개된 메뉴만 조회
     */
    List<QrManageMenu> findVisibleByShopId(@Param("shopId") Long shopId);

    /**
     * 공개된 메뉴만 카테고리별 조회
     */
    List<QrManageMenu> findVisibleByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * ID로 메뉴 조회
     */
    QrManageMenu findById(@Param("id") Long id);

    /**
     * 메뉴 등록
     */
    void insert(QrManageMenu menu);

    /**
     * 메뉴 수정
     */
    void update(QrManageMenu menu);

    /**
     * 메뉴 삭제
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
     * 품절 여부 변경
     */
    void updateSoldOut(@Param("id") Long id, @Param("isSoldOut") Boolean isSoldOut);

    /**
     * 대표 이미지 변경
     */
    void updatePrimaryImage(@Param("id") Long id, @Param("primaryImageId") Long primaryImageId);

    /**
     * 다음 정렬 순서 조회
     */
    Integer getNextSortOrder(@Param("categoryId") Long categoryId);

    /**
     * 상점 ID와 메뉴 ID로 메뉴 존재 확인
     */
    boolean existsByIdAndShopId(@Param("id") Long id, @Param("shopId") Long shopId);

    // ========== 옵션 그룹 ==========

    /**
     * 메뉴의 옵션 그룹 목록 조회
     */
    List<QrManageMenuOptionGroup> findOptionGroupsByMenuId(@Param("menuId") Long menuId);

    /**
     * 옵션 그룹 조회
     */
    QrManageMenuOptionGroup findOptionGroupById(@Param("id") Long id);

    /**
     * 옵션 그룹 등록
     */
    void insertOptionGroup(QrManageMenuOptionGroup optionGroup);

    /**
     * 옵션 그룹 수정
     */
    void updateOptionGroup(QrManageMenuOptionGroup optionGroup);

    /**
     * 옵션 그룹 삭제
     */
    void deleteOptionGroup(@Param("id") Long id);

    /**
     * 옵션 그룹 정렬 순서 변경
     */
    void updateOptionGroupSortOrder(@Param("id") Long id, @Param("sortOrder") Integer sortOrder);

    /**
     * 다음 옵션 그룹 정렬 순서
     */
    Integer getNextOptionGroupSortOrder(@Param("menuId") Long menuId);

    // ========== 옵션 ==========

    /**
     * 옵션 그룹의 옵션 목록 조회
     */
    List<QrManageMenuOption> findOptionsByGroupId(@Param("optionGroupId") Long optionGroupId);

    /**
     * 옵션 조회
     */
    QrManageMenuOption findOptionById(@Param("id") Long id);

    /**
     * 옵션 등록
     */
    void insertOption(QrManageMenuOption option);

    /**
     * 옵션 수정
     */
    void updateOption(QrManageMenuOption option);

    /**
     * 옵션 삭제
     */
    void deleteOption(@Param("id") Long id);

    /**
     * 옵션 그룹의 모든 옵션 삭제
     */
    void deleteOptionsByGroupId(@Param("optionGroupId") Long optionGroupId);

    /**
     * 옵션 정렬 순서 변경
     */
    void updateOptionSortOrder(@Param("id") Long id, @Param("sortOrder") Integer sortOrder);

    /**
     * 다음 옵션 정렬 순서
     */
    Integer getNextOptionSortOrder(@Param("optionGroupId") Long optionGroupId);
}
