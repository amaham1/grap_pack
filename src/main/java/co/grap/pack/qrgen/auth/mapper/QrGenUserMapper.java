package co.grap.pack.qrgen.auth.mapper;

import co.grap.pack.qrgen.auth.model.QrGenUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * QR Generator 사용자 Mapper
 */
@Mapper
public interface QrGenUserMapper {

    /**
     * 로그인 ID로 조회
     */
    QrGenUser findQrGenUserByLoginId(@Param("loginId") String loginId);

    /**
     * 이메일로 조회
     */
    QrGenUser findQrGenUserByEmail(@Param("email") String email);

    /**
     * ID로 조회
     */
    QrGenUser findQrGenUserById(@Param("id") Long id);

    /**
     * 사용자 등록
     */
    void insertQrGenUser(QrGenUser user);

    /**
     * 사용자 정보 수정
     */
    void updateQrGenUser(QrGenUser user);

    /**
     * 마지막 로그인 시간 업데이트
     */
    void updateQrGenUserLastLoginAt(@Param("id") Long id);

    /**
     * 로그인 ID 중복 확인
     */
    int countQrGenUserByLoginId(@Param("loginId") String loginId);

    /**
     * 이메일 중복 확인
     */
    int countQrGenUserByEmail(@Param("email") String email);
}
