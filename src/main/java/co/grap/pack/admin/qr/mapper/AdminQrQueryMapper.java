package co.grap.pack.admin.qr.mapper;

import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 통합 운영 포털 QR 조회 Mapper다.
 */
@Mapper
public interface AdminQrQueryMapper {

    /**
     * 슈퍼 관리자 알림 총건수를 조회한다.
     */
    int countSuperAdminNotifications();

    /**
     * 점주가 관리하는 상점 목록을 조회한다.
     */
    List<QrManageShop> findShopsByShopAdminId(@Param("shopAdminId") Long shopAdminId);
}
