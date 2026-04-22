package co.grap.pack.grap.realestate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 부동산 실거래 전체 적재 체크포인트.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealEstateSyncCheckpoint {

    private String datasetId;
    private String lawdCode;
    private LocalDateTime bootstrapStartedAt;
    private String lastSyncedYearMonth;
    private LocalDateTime bootstrapCompletedAt;
    private String lastResultMessage;
    private LocalDateTime updatedAt;
}
