package co.grap.pack.admin.dashboard.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * 통합 대시보드 기본 DB 조회 Mapper다.
 */
@Mapper
public interface AdminDashboardMapper {

    /**
     * 공개 콘텐츠 수를 조회한다.
     */
    Long countPublishedContents();

    /**
     * 비공개 콘텐츠 수를 조회한다.
     */
    Long countUnpublishedContents();

    /**
     * 외부 데이터 미검수 건수를 조회한다.
     */
    Long countPendingExternalData();
}
