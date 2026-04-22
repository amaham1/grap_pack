package co.grap.pack.admin.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

/**
 * 통합 운영자 모델이다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Alias("AdminOperator")
public class AdminOperator {

    private Long id;
    private String loginId;
    private String password;
    private String name;
    private String email;
    private AdminOperatorRole role;
    private Boolean isActive;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
