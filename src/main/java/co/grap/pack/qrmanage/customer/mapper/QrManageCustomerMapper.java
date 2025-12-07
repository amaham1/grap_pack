package co.grap.pack.qrmanage.customer.mapper;

import co.grap.pack.qrmanage.customer.model.QrManageQrScanLog;
import co.grap.pack.qrmanage.shopadmin.category.model.QrManageCategory;
import co.grap.pack.qrmanage.shopadmin.menu.model.QrManageMenu;
import co.grap.pack.qrmanage.shopadmin.menu.model.QrManageMenuOption;
import co.grap.pack.qrmanage.shopadmin.menu.model.QrManageMenuOptionGroup;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageBusinessHours;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 고객용 Mapper
 */
@Mapper
public interface QrManageCustomerMapper {

    /**
     * 상점 조회 (공개된 것만)
     */
    QrManageShop findShopById(@Param("id") Long id);

    /**
     * 영업시간 조회
     */
    List<QrManageBusinessHours> findBusinessHoursByShopId(@Param("shopId") Long shopId);

    /**
     * 공개된 카테고리 목록 조회
     */
    List<QrManageCategory> findVisibleCategoriesByShopId(@Param("shopId") Long shopId);

    /**
     * 카테고리별 공개된 메뉴 목록 조회
     */
    List<QrManageMenu> findVisibleMenusByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 메뉴 상세 조회
     */
    QrManageMenu findMenuById(@Param("id") Long id);

    /**
     * 메뉴의 옵션 그룹 목록 조회
     */
    List<QrManageMenuOptionGroup> findOptionGroupsByMenuId(@Param("menuId") Long menuId);

    /**
     * 옵션 그룹의 옵션 목록 조회
     */
    List<QrManageMenuOption> findOptionsByGroupId(@Param("optionGroupId") Long optionGroupId);

    /**
     * QR 스캔 로그 등록
     */
    void insertScanLog(QrManageQrScanLog scanLog);
}
