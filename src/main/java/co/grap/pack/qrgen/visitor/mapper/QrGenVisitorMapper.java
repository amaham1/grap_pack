package co.grap.pack.qrgen.visitor.mapper;

import co.grap.pack.qrgen.visitor.model.QrGenVisitor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 방문자 추적 Mapper 인터페이스
 */
@Mapper
public interface QrGenVisitorMapper {

    /**
     * 방문자 기록 추가
     * @param visitor 방문자 정보
     */
    void insertQrGenVisitor(QrGenVisitor visitor);

    /**
     * 방문자 ID로 조회
     * @param visitorId 방문자 ID
     * @return 방문자 정보
     */
    QrGenVisitor findQrGenVisitorById(@Param("visitorId") Long visitorId);

    /**
     * 체류시간 및 클라이언트 정보 업데이트
     * @param visitorId 방문자 ID
     * @param durationSeconds 체류 시간 (초)
     * @param screenResolution 화면 해상도
     * @param language 브라우저 언어
     */
    void updateQrGenVisitorDuration(
            @Param("visitorId") Long visitorId,
            @Param("durationSeconds") Integer durationSeconds,
            @Param("screenResolution") String screenResolution,
            @Param("language") String language
    );

    /**
     * 세션 ID로 방문자 목록 조회
     * @param sessionId 세션 ID
     * @return 방문자 목록
     */
    List<QrGenVisitor> findQrGenVisitorsBySessionId(@Param("sessionId") String sessionId);

    /**
     * 사용자 ID로 방문자 목록 조회
     * @param userId 사용자 ID
     * @return 방문자 목록
     */
    List<QrGenVisitor> findQrGenVisitorsByUserId(@Param("userId") Long userId);
}
