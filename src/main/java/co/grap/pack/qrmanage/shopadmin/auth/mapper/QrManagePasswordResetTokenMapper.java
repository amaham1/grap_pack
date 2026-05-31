package co.grap.pack.qrmanage.shopadmin.auth.mapper;

import co.grap.pack.qrmanage.shopadmin.auth.model.QrManagePasswordResetToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 비밀번호 재설정 토큰 Mapper
 */
@Mapper
public interface QrManagePasswordResetTokenMapper {

    /**
     * 토큰으로 조회
     * @param token 토큰 문자열
     * @return 토큰 정보
     */
    QrManagePasswordResetToken findByToken(@Param("token") String token);

    /**
     * 상점관리자 ID로 미사용 토큰 조회
     * @param shopAdminId 상점관리자 ID
     * @return 토큰 정보
     */
    QrManagePasswordResetToken findValidTokenByShopAdminId(@Param("shopAdminId") Long shopAdminId);

    /**
     * 토큰 생성
     * @param resetToken 토큰 정보
     * @return 생성된 행 수
     */
    int insert(QrManagePasswordResetToken resetToken);

    /**
     * 토큰 사용 처리
     * @param token 토큰 문자열
     * @return 수정된 행 수
     */
    int markAsUsed(@Param("token") String token);

    /**
     * 상점관리자의 기존 토큰 무효화
     * @param shopAdminId 상점관리자 ID
     * @return 수정된 행 수
     */
    int invalidateByShopAdminId(@Param("shopAdminId") Long shopAdminId);

    /**
     * 만료된 토큰 삭제
     * @return 삭제된 행 수
     */
    int deleteExpiredTokens();
}
