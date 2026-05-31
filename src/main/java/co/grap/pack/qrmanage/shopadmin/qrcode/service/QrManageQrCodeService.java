package co.grap.pack.qrmanage.shopadmin.qrcode.service;

import co.grap.pack.qrmanage.shopadmin.qrcode.mapper.QrManageQrCodeMapper;
import co.grap.pack.qrmanage.shopadmin.qrcode.model.QrManageQrCode;
import co.grap.pack.qrmanage.shopadmin.qrcode.model.QrManageQrCodeType;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * QR 코드 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrManageQrCodeService {

    private final QrManageQrCodeMapper qrCodeMapper;

    @Value("${qr-manage.qr-code.size:300}")
    private int qrCodeSize;

    @Value("${qr-manage.qr-code.save-path:uploads/qrmanage/qrcode}")
    private String savePath;

    @Value("${qrmanage.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * 상점의 QR 코드 목록 조회
     */
    public List<QrManageQrCode> getQrCodes(Long shopId) {
        return qrCodeMapper.findByShopId(shopId);
    }

    /**
     * QR 코드 조회
     */
    public QrManageQrCode getQrCode(Long id) {
        return qrCodeMapper.findById(id);
    }

    /**
     * QR 코드 문자열로 조회
     */
    public QrManageQrCode getByQrCode(String qrCode) {
        return qrCodeMapper.findByQrCode(qrCode);
    }

    /**
     * 상점의 특정 타입 QR 코드 조회
     */
    public QrManageQrCode getByShopIdAndType(Long shopId, QrManageQrCodeType qrType) {
        return qrCodeMapper.findByShopIdAndType(shopId, qrType);
    }

    /**
     * 상점 대표 QR 코드 생성
     */
    @Transactional
    public QrManageQrCode createShopQrCode(Long shopId) throws IOException, WriterException {
        log.info("✅ [CHECK] 상점 대표 QR 코드 생성: shopId={}", shopId);

        // 기존 상점 QR이 있는지 확인
        QrManageQrCode existing = qrCodeMapper.findByShopIdAndType(shopId, QrManageQrCodeType.SHOP);
        if (existing != null) {
            // 기존 QR 재생성
            return regenerateQrCode(existing.getId());
        }

        String qrCode = UUID.randomUUID().toString();
        String targetUrl = baseUrl + "/qr-manage/view/shop/" + qrCode;

        return createQrCode(shopId, QrManageQrCodeType.SHOP, qrCode, targetUrl);
    }

    /**
     * 메뉴 QR 코드 생성
     */
    @Transactional
    public QrManageQrCode createMenuQrCode(Long shopId) throws IOException, WriterException {
        log.info("✅ [CHECK] 메뉴 QR 코드 생성: shopId={}", shopId);

        // 기존 메뉴 QR이 있는지 확인
        QrManageQrCode existing = qrCodeMapper.findByShopIdAndType(shopId, QrManageQrCodeType.MENU);
        if (existing != null) {
            // 기존 QR 재생성
            return regenerateQrCode(existing.getId());
        }

        String qrCode = UUID.randomUUID().toString();
        String targetUrl = baseUrl + "/qr-manage/view/menu/" + qrCode;

        return createQrCode(shopId, QrManageQrCodeType.MENU, qrCode, targetUrl);
    }

    /**
     * QR 코드 생성 공통 로직
     */
    private QrManageQrCode createQrCode(Long shopId, QrManageQrCodeType qrType, String qrCode, String targetUrl)
            throws IOException, WriterException {

        // QR 이미지 생성
        String imagePath = generateQrImage(qrCode, targetUrl);

        QrManageQrCode qr = QrManageQrCode.builder()
                .shopId(shopId)
                .qrType(qrType)
                .qrCode(qrCode)
                .imagePath(imagePath)
                .targetUrl(targetUrl)
                .isActive(true)
                .build();

        qrCodeMapper.insert(qr);
        log.info("✅ [CHECK] QR 코드 생성 완료: id={}, type={}", qr.getId(), qrType);
        return qr;
    }

    /**
     * QR 코드 재생성
     */
    @Transactional
    public QrManageQrCode regenerateQrCode(Long id) throws IOException, WriterException {
        log.info("✅ [CHECK] QR 코드 재생성: id={}", id);

        QrManageQrCode qr = qrCodeMapper.findById(id);
        if (qr == null) {
            throw new IllegalArgumentException("QR 코드를 찾을 수 없습니다.");
        }

        // 기존 이미지 삭제
        deleteQrImage(qr.getImagePath());

        // 새 QR 코드 생성
        String newQrCode = UUID.randomUUID().toString();
        String targetUrl = qr.getQrType() == QrManageQrCodeType.SHOP
                ? baseUrl + "/qr-manage/view/shop/" + newQrCode
                : baseUrl + "/qr-manage/view/menu/" + newQrCode;

        String imagePath = generateQrImage(newQrCode, targetUrl);

        qr.setQrCode(newQrCode);
        qr.setTargetUrl(targetUrl);
        qr.setImagePath(imagePath);
        qrCodeMapper.update(qr);

        log.info("✅ [CHECK] QR 코드 재생성 완료: id={}", id);
        return qr;
    }

    /**
     * QR 코드 활성화/비활성화
     */
    @Transactional
    public void updateActive(Long id, Boolean isActive) {
        qrCodeMapper.updateActive(id, isActive);
        log.info("✅ [CHECK] QR 코드 활성화 상태 변경: id={}, isActive={}", id, isActive);
    }

    /**
     * QR 코드 만료일 설정
     */
    @Transactional
    public void updateExpiresAt(Long id, LocalDateTime expiresAt) {
        qrCodeMapper.updateExpiresAt(id, expiresAt);
        log.info("✅ [CHECK] QR 코드 만료일 변경: id={}, expiresAt={}", id, expiresAt);
    }

    /**
     * QR 코드 삭제
     */
    @Transactional
    public void deleteQrCode(Long id) {
        QrManageQrCode qr = qrCodeMapper.findById(id);
        if (qr != null) {
            deleteQrImage(qr.getImagePath());
            qrCodeMapper.delete(id);
            log.info("✅ [CHECK] QR 코드 삭제 완료: id={}", id);
        }
    }

    /**
     * QR 이미지 생성
     */
    private String generateQrImage(String qrCode, String targetUrl) throws IOException, WriterException {
        // QR 코드 설정
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 2);

        // QR 코드 생성
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(targetUrl, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize, hints);

        // 파일 저장
        Path uploadDir = Paths.get(savePath);
        Files.createDirectories(uploadDir);
        String fileName = qrCode + ".png";
        Path filePath = uploadDir.resolve(fileName);

        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", filePath);

        return "/" + savePath + "/" + fileName;
    }

    /**
     * QR 이미지 삭제
     */
    private void deleteQrImage(String imagePath) {
        if (imagePath != null) {
            try {
                Path path = Paths.get(imagePath.substring(1)); // 앞의 '/' 제거
                Files.deleteIfExists(path);
            } catch (IOException e) {
                log.error("❌ [ERROR] QR 이미지 삭제 실패: {}", e.getMessage());
            }
        }
    }

    /**
     * QR 코드가 해당 상점 소유인지 확인
     */
    public boolean isOwnedByShop(Long qrCodeId, Long shopId) {
        QrManageQrCode qr = qrCodeMapper.findById(qrCodeId);
        return qr != null && qr.getShopId().equals(shopId);
    }

    /**
     * 전체 QR 코드 목록 조회 (최고관리자용)
     */
    public List<QrManageQrCode> getAllQrCodes(Long shopId, String qrType, Boolean isActive, int page, int size) {
        QrManageQrCodeType type = null;
        if (qrType != null && !qrType.isEmpty()) {
            try {
                type = QrManageQrCodeType.valueOf(qrType);
            } catch (IllegalArgumentException e) {
                // 잘못된 타입은 무시
            }
        }
        int offset = (page - 1) * size;
        return qrCodeMapper.findAll(type, shopId, isActive, size, offset);
    }

    /**
     * 전체 QR 코드 수 (최고관리자용)
     */
    public int countAllQrCodes(Long shopId, String qrType, Boolean isActive) {
        QrManageQrCodeType type = null;
        if (qrType != null && !qrType.isEmpty()) {
            try {
                type = QrManageQrCodeType.valueOf(qrType);
            } catch (IllegalArgumentException e) {
                // 잘못된 타입은 무시
            }
        }
        return qrCodeMapper.countAll(type, shopId, isActive);
    }
}
