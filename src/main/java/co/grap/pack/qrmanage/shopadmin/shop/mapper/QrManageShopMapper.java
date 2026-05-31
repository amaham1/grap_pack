package co.grap.pack.qrmanage.shopadmin.shop.mapper;

import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageBusinessHours;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShop;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShopStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 상점 Mapper (상점관리자용)
 */
@Mapper
public interface QrManageShopMapper {

    /**
     * 상점관리자 ID로 상점 조회
     * @param shopAdminId 상점관리자 ID
     * @return 상점 정보
     */
    QrManageShop findByShopAdminId(@Param("shopAdminId") Long shopAdminId);

    /**
     * ID로 상점 조회
     * @param id 상점 ID
     * @return 상점 정보
     */
    QrManageShop findById(@Param("id") Long id);

    /**
     * 상점 등록
     * @param shop 상점 정보
     * @return 등록된 행 수
     */
    int insert(QrManageShop shop);

    /**
     * 상점 수정
     * @param shop 상점 정보
     * @return 수정된 행 수
     */
    int update(QrManageShop shop);

    /**
     * 상점 상태 변경
     * @param id 상점 ID
     * @param status 상태
     * @return 수정된 행 수
     */
    int updateStatus(@Param("id") Long id, @Param("status") QrManageShopStatus status);

    // ===== 영업시간 관련 =====

    /**
     * 상점의 영업시간 목록 조회
     * @param shopId 상점 ID
     * @return 영업시간 목록
     */
    List<QrManageBusinessHours> findBusinessHoursByShopId(@Param("shopId") Long shopId);

    /**
     * 영업시간 저장 (upsert)
     * @param businessHours 영업시간
     * @return 저장된 행 수
     */
    int upsertBusinessHours(QrManageBusinessHours businessHours);

    /**
     * 상점의 모든 영업시간 삭제
     * @param shopId 상점 ID
     * @return 삭제된 행 수
     */
    int deleteBusinessHoursByShopId(@Param("shopId") Long shopId);

    /**
     * 상점관리자 이메일로 상점 ID 조회
     * @param email 상점관리자 이메일
     * @return 상점 ID (없으면 null)
     */
    Long findShopIdByAdminEmail(@Param("email") String email);

    /**
     * 상점관리자 username으로 상점 ID 조회
     * @param username 상점관리자 username
     * @return 상점 ID (없으면 null)
     */
    Long findShopIdByAdminUsername(@Param("username") String username);

    /**
     * 상점관리자 이메일로 상점 정보 조회
     * @param email 상점관리자 이메일
     * @return 상점 정보 (없으면 null)
     */
    QrManageShop findByAdminEmail(@Param("email") String email);

    /**
     * 상점관리자 username으로 상점 정보 조회
     * @param username 상점관리자 username
     * @return 상점 정보 (없으면 null)
     */
    QrManageShop findByAdminUsername(@Param("username") String username);
}
