package co.grap.pack.grap.external.api.mapper;

import co.grap.pack.grap.realestate.model.RealEstateSyncCheckpoint;
import co.grap.pack.grap.realestate.model.RealEstateTransactionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 제주 부동산 외부 API 적재용 Mapper.
 */
@Mapper
public interface CmsExternalRealEstateMapper {

    void upsertRealEstateTransaction(RealEstateTransactionRecord record);

    RealEstateSyncCheckpoint selectSyncCheckpoint(@Param("datasetId") String datasetId,
                                                  @Param("lawdCode") String lawdCode);

    void startCheckpoint(@Param("datasetId") String datasetId,
                         @Param("lawdCode") String lawdCode,
                         @Param("startedAt") LocalDateTime startedAt,
                         @Param("message") String message,
                         @Param("updatedAt") LocalDateTime updatedAt);

    void updateCheckpointProgress(@Param("datasetId") String datasetId,
                                  @Param("lawdCode") String lawdCode,
                                  @Param("lastSyncedYearMonth") String lastSyncedYearMonth,
                                  @Param("message") String message,
                                  @Param("updatedAt") LocalDateTime updatedAt);

    void completeCheckpoint(@Param("datasetId") String datasetId,
                            @Param("lawdCode") String lawdCode,
                            @Param("lastSyncedYearMonth") String lastSyncedYearMonth,
                            @Param("completedAt") LocalDateTime completedAt,
                            @Param("message") String message,
                            @Param("updatedAt") LocalDateTime updatedAt);
}
