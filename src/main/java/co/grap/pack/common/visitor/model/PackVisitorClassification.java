package co.grap.pack.common.visitor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 방문 페이지 분류 결과다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackVisitorClassification {

    /** 서비스 코드 */
    private PackVisitorServiceCode serviceCode;

    /** 메뉴 코드 */
    private PackVisitorMenuCode menuCode;

    /** 정규화한 라우트 키 */
    private String routeKey;
}
