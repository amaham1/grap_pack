package co.grap.pack.qrmanage.shopadmin.qrcode.mapper;

import co.grap.pack.qrmanage.shopadmin.qrcode.model.QrManageQrCode;
import co.grap.pack.qrmanage.shopadmin.qrcode.model.QrManageQrCodeType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * QR 코드 Mapper
 */
@Mapper
public interface QrManageQrCodeMapper {

    /**
     * 상점의 QR 코드 목록 조회
     */
    List<QrManageQrCode> findByShopId(@Param("shopId") Long shopId);

    /**
     * 전체 QR 코드 목록 조회 (최고관리자용)
     */
    List<QrManageQrCode> findAll(@Param("qrType") QrManageQrCodeType qrType,
                                  @Param("shopId") Long shopId,
                                  @Param("isActive") Boolean isActive,
                                  @Param("size") int size,
                                  @Param("offset") int offset);

    /**
     * 전체 QR 코드 수
     */
    int countAll(@Param("qrType") QrManageQrCodeType qrType,
                 @Param("shopId") Long shopId,
                 @Param("isActive") Boolean isActive);

    /**
     * ID로 QR 코드 조회
     */
    QrManageQrCode findById(@Param("id") Long id);

    /**
     * QR 코드로 조회
     */
    QrManageQrCode findByQrCode(@Param("qrCode") String qrCode);

    /**
     * 상점의 특정 타입 QR 코드 조회
     */
    QrManageQrCode findByShopIdAndType(@Param("shopId") Long shopId, @Param("qrType") QrManageQrCodeType qrType);

    /**
     * QR 코드 등록
     */
    void insert(QrManageQrCode qrCode);

    /**
     * QR 코드 수정
     */
    void update(QrManageQrCode qrCode);

    /**
     * QR 코드 삭제
     */
    void delete(@Param("id") Long id);

    /**
     * 활성화 여부 변경
     */
    void updateActive(@Param("id") Long id, @Param("isActive") Boolean isActive);

    /**
     * 만료일 변경
     */
    void updateExpiresAt(@Param("id") Long id, @Param("expiresAt") java.time.LocalDateTime expiresAt);
}
