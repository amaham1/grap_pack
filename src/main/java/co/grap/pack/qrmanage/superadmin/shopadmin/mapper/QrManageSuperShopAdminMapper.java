package co.grap.pack.qrmanage.superadmin.shopadmin.mapper;

import co.grap.pack.qrmanage.shopadmin.auth.model.QrManageShopAdmin;
import co.grap.pack.qrmanage.shopadmin.auth.model.QrManageShopAdminStatus;
import co.grap.pack.qrmanage.superadmin.shopadmin.model.QrManageShopAdminSearchParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 최고관리자용 상점관리자 관리 Mapper
 */
@Mapper
public interface QrManageSuperShopAdminMapper {

    /**
     * 상점관리자 목록 조회 (페이징)
     * @param param 검색 파라미터
     * @return 상점관리자 목록
     */
    List<QrManageShopAdmin> findAll(QrManageShopAdminSearchParam param);

    /**
     * 상점관리자 총 개수
     * @param param 검색 파라미터
     * @return 총 개수
     */
    int countAll(QrManageShopAdminSearchParam param);

    /**
     * ID로 상점관리자 조회
     * @param id 상점관리자 ID
     * @return 상점관리자 정보
     */
    QrManageShopAdmin findById(@Param("id") Long id);

    /**
     * 상점관리자 상태 변경
     * @param id 상점관리자 ID
     * @param status 변경할 상태
     * @return 수정된 행 수
     */
    int updateStatus(@Param("id") Long id, @Param("status") QrManageShopAdminStatus status);

    /**
     * 상점관리자 등록 (최고관리자가 직접 생성)
     * @param shopAdmin 상점관리자 정보
     * @return 등록된 행 수
     */
    int insert(QrManageShopAdmin shopAdmin);

    /**
     * 상태별 상점관리자 수 조회
     * @param status 상태
     * @return 개수
     */
    int countByStatus(@Param("status") QrManageShopAdminStatus status);
}
