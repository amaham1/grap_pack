package co.grap.pack.qrmanage.superadmin.auth.mapper;

import co.grap.pack.qrmanage.superadmin.auth.model.QrManageSuperAdmin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * QR 관리 서비스 최고관리자 Mapper
 */
@Mapper
public interface QrManageSuperAdminMapper {

    /**
     * 사용자명으로 최고관리자 조회
     * @param username 사용자명
     * @return 최고관리자 정보
     */
    QrManageSuperAdmin findByUsername(@Param("username") String username);

    /**
     * ID로 최고관리자 조회
     * @param id 최고관리자 ID
     * @return 최고관리자 정보
     */
    QrManageSuperAdmin findById(@Param("id") Long id);

    /**
     * 최고관리자 등록
     * @param superAdmin 최고관리자 정보
     * @return 등록된 행 수
     */
    int insert(QrManageSuperAdmin superAdmin);

    /**
     * 최고관리자 정보 수정
     * @param superAdmin 최고관리자 정보
     * @return 수정된 행 수
     */
    int update(QrManageSuperAdmin superAdmin);
}
