package co.grap.pack.qrmanage.shopadmin.shop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * 영업시간 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageBusinessHours {

    /** 영업시간 ID */
    private Long id;

    /** 상점 ID */
    private Long shopId;

    /** 요일 (0=일, 1=월, ..., 6=토) */
    private Integer dayOfWeek;

    /** 휴무일 여부 */
    private Boolean isHoliday;

    /** 오픈 시간 */
    private LocalTime openTime;

    /** 마감 시간 */
    private LocalTime closeTime;

    /**
     * 요일명 반환
     */
    public String getDayName() {
        String[] days = {"일", "월", "화", "수", "목", "금", "토"};
        if (dayOfWeek >= 0 && dayOfWeek <= 6) {
            return days[dayOfWeek];
        }
        return "";
    }
}
