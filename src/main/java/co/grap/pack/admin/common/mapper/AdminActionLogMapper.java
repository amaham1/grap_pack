package co.grap.pack.admin.common.mapper;

import co.grap.pack.admin.common.model.AdminActionLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 통합 운영 액션 로그 Mapper다.
 */
@Mapper
public interface AdminActionLogMapper {

    /**
     * 액션 로그를 저장한다.
     */
    void insert(AdminActionLog adminActionLog);
}
