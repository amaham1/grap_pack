package co.grap.pack.admin.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

/**
 * 통합 운영 액션 로그 모델이다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Alias("AdminActionLog")
public class AdminActionLog {

    private Long id;
    private Long operatorId;
    private String domainCode;
    private String actionCode;
    private String targetType;
    private Long targetId;
    private String summary;
    private String ipAddress;
    private LocalDateTime createdAt;
}
