package co.grap.pack.qrgen.generator.mapper;

import co.grap.pack.qrgen.generator.model.QrGenHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * QR Generator 히스토리 Mapper
 */
@Mapper
public interface QrGenHistoryMapper {

    /**
     * 사용자 ID로 히스토리 목록 조회 (페이징)
     */
    List<QrGenHistory> findQrGenHistoryByUserId(@Param("userId") Long userId,
                                                 @Param("limit") int limit,
                                                 @Param("offset") int offset);

    /**
     * 사용자 ID로 히스토리 개수 조회
     */
    int countQrGenHistoryByUserId(@Param("userId") Long userId);

    /**
     * ID로 히스토리 조회
     */
    QrGenHistory findQrGenHistoryById(@Param("id") Long id);

    /**
     * 히스토리 저장
     */
    void insertQrGenHistory(QrGenHistory history);

    /**
     * 히스토리 삭제
     */
    void deleteQrGenHistory(@Param("id") Long id);

    /**
     * 이미지 경로 업데이트
     */
    void updateQrGenHistoryImagePath(@Param("id") Long id, @Param("imagePath") String imagePath);
}
