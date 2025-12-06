package co.grap.pack.admin.content.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

/**
 * 콘텐츠 종류 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Alias("AdminContentType")
public class AdminContentType {

    /**
     * 콘텐츠 종류 ID
     */
    private Long contentTypeId;

    /**
     * 종류명
     */
    private String typeName;

    /**
     * 종류 설명
     */
    private String typeDesc;

    /**
     * 활성화 여부
     */
    private Boolean isActive;

    /**
     * 생성일시
     */
    private LocalDateTime createDt;

    /**
     * 수정일시
     */
    private LocalDateTime updateDt;
}
