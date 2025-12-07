package co.grap.pack.qrmanage.shopadmin.auth.mapper;

import co.grap.pack.qrmanage.shopadmin.auth.model.QrManageShopAdmin;
import co.grap.pack.qrmanage.shopadmin.auth.model.QrManageShopAdminStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * QR 관리 서비스 상점관리자 Mapper
 */
@Mapper
public interface QrManageShopAdminMapper {

    /**
     * 사용자명으로 상점관리자 조회
     * @param username 사용자명
     * @return 상점관리자 정보
     */
    QrManageShopAdmin findByUsername(@Param("username") String username);

    /**
     * ID로 상점관리자 조회
     * @param id 상점관리자 ID
     * @return 상점관리자 정보
     */
    QrManageShopAdmin findById(@Param("id") Long id);

    /**
     * 이메일로 상점관리자 조회
     * @param email 이메일
     * @return 상점관리자 정보
     */
    QrManageShopAdmin findByEmail(@Param("email") String email);

    /**
     * 상점관리자 목록 조회
     * @param status 상태 필터 (null이면 전체)
     * @param keyword 검색어 (이름, 이메일)
     * @return 상점관리자 목록
     */
    List<QrManageShopAdmin> findAll(@Param("status") QrManageShopAdminStatus status,
                                     @Param("keyword") String keyword);

    /**
     * 상점관리자 등록
     * @param shopAdmin 상점관리자 정보
     * @return 등록된 행 수
     */
    int insert(QrManageShopAdmin shopAdmin);

    /**
     * 상점관리자 정보 수정
     * @param shopAdmin 상점관리자 정보
     * @return 수정된 행 수
     */
    int update(QrManageShopAdmin shopAdmin);

    /**
     * 상점관리자 상태 변경
     * @param id 상점관리자 ID
     * @param status 변경할 상태
     * @return 수정된 행 수
     */
    int updateStatus(@Param("id") Long id, @Param("status") QrManageShopAdminStatus status);

    /**
     * 사용자명 중복 확인
     * @param username 사용자명
     * @return 존재하면 true
     */
    boolean existsByUsername(@Param("username") String username);

    /**
     * 이메일 중복 확인
     * @param email 이메일
     * @return 존재하면 true
     */
    boolean existsByEmail(@Param("email") String email);
}
