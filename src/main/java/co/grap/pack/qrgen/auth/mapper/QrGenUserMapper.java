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
     * 사용자명으로 조회
     */
    QrGenUser findByUsername(@Param("username") String username);

    /**
     * 이메일로 조회
     */
    QrGenUser findByEmail(@Param("email") String email);

    /**
     * ID로 조회
     */
    QrGenUser findById(@Param("id") Long id);

    /**
     * 사용자 등록
     */
    void insert(QrGenUser user);

    /**
     * 사용자 정보 수정
     */
    void update(QrGenUser user);

    /**
     * 마지막 로그인 시간 업데이트
     */
    void updateLastLoginAt(@Param("id") Long id);

    /**
     * 사용자명 중복 확인
     */
    int countByUsername(@Param("username") String username);

    /**
     * 이메일 중복 확인
     */
    int countByEmail(@Param("email") String email);
}
