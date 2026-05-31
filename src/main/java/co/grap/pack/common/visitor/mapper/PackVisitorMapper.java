package co.grap.pack.common.visitor.mapper;

import co.grap.pack.common.visitor.model.PackVisitor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 공통 방문자 추적 Mapper
 */
@Mapper
public interface PackVisitorMapper {

    /**
     * 방문 기록 저장
     */
    void insertPackVisitor(PackVisitor visitor);

    /**
     * 체류시간 및 클라이언트 정보 업데이트
     */
    void updatePackVisitorDuration(
            @Param("visitorId") Long visitorId,
            @Param("durationSeconds") Integer durationSeconds,
            @Param("visibleDurationSeconds") Integer visibleDurationSeconds,
            @Param("interactionCount") Integer interactionCount,
            @Param("firstInteractionElapsedSeconds") Integer firstInteractionElapsedSeconds,
            @Param("screenResolution") String screenResolution,
            @Param("language") String language
    );
}
