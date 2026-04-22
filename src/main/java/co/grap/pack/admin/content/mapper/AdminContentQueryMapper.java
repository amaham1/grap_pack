package co.grap.pack.admin.content.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 통합 콘텐츠 조회 전용 Mapper다.
 */
@Mapper
public interface AdminContentQueryMapper {

    /**
     * 동기화 작업 요약을 조회한다.
     */
    List<Map<String, Object>> selectSyncJobSummaries();
}
