package co.grap.pack.qrmanage.superadmin.visitorstats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * IP 기준 접속 기록 페이지 모델이다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageSuperVisitorIpAccessLogPage {

    /** 현재 페이지에 노출할 접속 기록 목록 */
    private List<QrManageSuperVisitorIpAccessLog> items;

    /** 전체 접속 기록 수 */
    private long totalCount;

    /** 현재 페이지 번호 */
    private int currentPage;

    /** 페이지당 노출 개수 */
    private int pageSize;

    /** 전체 페이지 수 */
    private int totalPages;

    /** 페이지네이션 시작 번호 */
    private int startPage;

    /** 페이지네이션 종료 번호 */
    private int endPage;

    /** BOT 기록 포함 여부 */
    private boolean includeBots;
}
