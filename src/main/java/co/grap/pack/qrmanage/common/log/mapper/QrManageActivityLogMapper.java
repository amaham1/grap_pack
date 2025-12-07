package co.grap.pack.qrmanage.common.log.mapper;

import co.grap.pack.qrmanage.common.log.model.QrManageActivityLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 활동 로그 Mapper
 */
@Mapper
public interface QrManageActivityLogMapper {

    /**
     * 로그 등록
     */
    void insert(QrManageActivityLog log);

    /**
     * 전체 로그 조회 (최고관리자용)
     */
    List<QrManageActivityLog> findAll(
            @Param("userType") String userType,
            @Param("activityType") String activityType,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * 전체 로그 수
     */
    int countAll(
            @Param("userType") String userType,
            @Param("activityType") String activityType
    );

    /**
     * 상점관리자별 로그 조회
     */
    List<QrManageActivityLog> findByUserId(
            @Param("userId") Long userId,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * 오래된 로그 삭제 (90일 이상)
     */
    void deleteOldLogs();
}
