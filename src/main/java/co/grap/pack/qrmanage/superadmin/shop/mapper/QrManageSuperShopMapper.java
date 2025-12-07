package co.grap.pack.qrmanage.superadmin.shop.mapper;

import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShop;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShopStatus;
import co.grap.pack.qrmanage.superadmin.shop.model.QrManageShopSearchParam;
import co.grap.pack.qrmanage.superadmin.shop.model.QrManageShopMemo;
import co.grap.pack.qrmanage.superadmin.shop.model.QrManageShopReviewHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 최고관리자용 상점 관리 Mapper
 */
@Mapper
public interface QrManageSuperShopMapper {

    /**
     * 상점 목록 조회 (페이징)
     */
    List<QrManageShop> findAll(QrManageShopSearchParam param);

    /**
     * 상점 총 개수
     */
    int countAll(QrManageShopSearchParam param);

    /**
     * ID로 상점 조회
     */
    QrManageShop findById(@Param("id") Long id);

    /**
     * 상점 상태 변경
     */
    int updateStatus(@Param("id") Long id, @Param("status") QrManageShopStatus status);

    /**
     * 상점 노출 여부 변경
     */
    int updateVisibility(@Param("id") Long id, @Param("isVisible") Boolean isVisible);

    /**
     * 상태별 상점 수
     */
    int countByStatus(@Param("status") QrManageShopStatus status);

    // ===== 검수 이력 =====

    /**
     * 검수 이력 등록
     */
    int insertReviewHistory(QrManageShopReviewHistory history);

    /**
     * 상점의 검수 이력 조회
     */
    List<QrManageShopReviewHistory> findReviewHistoryByShopId(@Param("shopId") Long shopId);

    // ===== 메모 =====

    /**
     * 메모 등록
     */
    int insertMemo(QrManageShopMemo memo);

    /**
     * 상점의 메모 목록 조회
     */
    List<QrManageShopMemo> findMemosByShopId(@Param("shopId") Long shopId);

    /**
     * 메모 삭제
     */
    int deleteMemo(@Param("id") Long id);
}
