package co.grap.pack.qrgen.generator.service;

import co.grap.pack.qrgen.generator.mapper.QrGenHistoryMapper;
import co.grap.pack.qrgen.generator.model.QrGenContentType;
import co.grap.pack.qrgen.generator.model.QrGenHistory;
import co.grap.pack.qrgen.generator.model.QrGenRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;

/**
 * QR Generator 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrGenGeneratorService {

    private final QrGenHistoryMapper historyMapper;

    @Value("${qrgen.qr-image.save-path:${user.dir}/uploads/qrgen/images}")
    private String savePath;

    @Value("${qrgen.qr-code.default-size:300}")
    private int defaultSize;

    @Value("${qrgen.qr-code.max-size:1000}")
    private int maxSize;

    @Value("${qrgen.qr-code.min-size:100}")
    private int minSize;

    /**
     * QR 코드 이미지 생성 (byte[] 반환)
     */
    public byte[] generateQrCode(QrGenRequest request) throws WriterException, IOException {
        log.info("✅ [CHECK] QR 코드 생성 시작: type={}, size={}",
                 request.getContentType(), request.getSize());

        // 크기 유효성 검사
        int size = request.getSize() != null ? request.getSize() : defaultSize;
        size = Math.max(minSize, Math.min(maxSize, size));

        // QR 코드 설정
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, getErrorCorrectionLevel(request.getErrorCorrection()));
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 0);

        // QR 코드 생성
        QRCodeWriter writer = new QRCodeWriter();
        String content = formatContent(request);
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints);

        // 색상 적용
        int fgColor = parseColor(request.getForegroundColor(), 0xFF000000);
        int bgColor = parseColor(request.getBackgroundColor(), 0xFFFFFFFF);
        MatrixToImageConfig config = new MatrixToImageConfig(fgColor, bgColor);

        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix, config);

        // ZXing 나머지 픽셀 패딩 제거: 실제 QR 영역만 크롭 후 요청 크기로 리사이즈
        image = cropAndResizeQrImage(bitMatrix, image, size, bgColor);

        // byte[] 변환
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);

        log.info("✅ [CHECK] QR 코드 생성 완료");
        return baos.toByteArray();
    }

    /**
     * QR 코드 이미지 파일로 저장
     */
    public String saveQrCodeToFile(byte[] qrImage, Long userId) throws IOException {
        // 디렉토리 생성
        Path uploadDir = Paths.get(savePath);
        Files.createDirectories(uploadDir);

        // 파일명 생성: {userId}_{timestamp}_{uuid}.png
        String fileName = String.format("%d_%d_%s.png",
            userId,
            System.currentTimeMillis(),
            UUID.randomUUID().toString().substring(0, 8)
        );

        Path filePath = uploadDir.resolve(fileName);
        Files.write(filePath, qrImage);

        // 상대 경로 반환
        String relativePath = "/qrgen/images/" + fileName;
        log.info("✅ [CHECK] QR 이미지 저장 완료: {}", relativePath);
        return relativePath;
    }

    /**
     * 히스토리 저장
     */
    @Transactional
    public QrGenHistory saveQrGenHistory(Long userId, QrGenRequest request, String imagePath) {
        log.info("✅ [CHECK] QR 생성 히스토리 저장: userId={}", userId);

        QrGenHistory history = QrGenHistory.fromRequest(request, userId);
        history.setQrGenHistoryImagePath(imagePath);

        historyMapper.insertQrGenHistory(history);
        return history;
    }

    /**
     * 사용자 히스토리 목록 조회
     */
    public List<QrGenHistory> findQrGenHistoryByUserId(Long userId, int page, int size) {
        int offset = (page - 1) * size;
        return historyMapper.findQrGenHistoryByUserId(userId, size, offset);
    }

    /**
     * 사용자 히스토리 개수 조회
     */
    public int countQrGenHistoryByUserId(Long userId) {
        return historyMapper.countQrGenHistoryByUserId(userId);
    }

    /**
     * 히스토리 상세 조회
     */
    public QrGenHistory findQrGenHistoryById(Long id) {
        return historyMapper.findQrGenHistoryById(id);
    }

    /**
     * 히스토리 삭제
     */
    @Transactional
    public void deleteQrGenHistory(Long id, Long userId) {
        QrGenHistory history = historyMapper.findQrGenHistoryById(id);
        if (history == null || !history.getQrGenHistoryUserId().equals(userId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        // 이미지 파일 삭제
        deleteImageFile(history.getQrGenHistoryImagePath());

        historyMapper.deleteQrGenHistory(id);
        log.info("✅ [CHECK] 히스토리 삭제 완료: id={}", id);
    }

    /**
     * ZXing BitMatrix의 나머지 픽셀 패딩 제거
     * QR 모듈 수가 요청 크기로 나누어떨어지지 않을 때 발생하는 여백을 크롭 후 리사이즈
     */
    private BufferedImage cropAndResizeQrImage(BitMatrix bitMatrix, BufferedImage image, int targetSize, int bgColor) {
        int[] enclosingRect = bitMatrix.getEnclosingRectangle();
        if (enclosingRect == null) {
            return image;
        }

        int x = enclosingRect[0];
        int y = enclosingRect[1];
        int w = enclosingRect[2];
        int h = enclosingRect[3];

        // 패딩이 없으면 원본 반환
        if (x == 0 && y == 0 && w == image.getWidth() && h == image.getHeight()) {
            return image;
        }

        // 실제 QR 영역만 크롭
        BufferedImage cropped = image.getSubimage(x, y, w, h);

        // 요청 크기로 리사이즈
        BufferedImage resized = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.drawImage(cropped, 0, 0, targetSize, targetSize, null);
        g2d.dispose();

        return resized;
    }

    /**
     * 콘텐츠 포맷팅
     */
    private String formatContent(QrGenRequest request) {
        QrGenContentType type = request.getContentType();
        String value = request.getContentValue();

        if (type == null || value == null) {
            return value != null ? value : "";
        }

        return switch (type) {
            case URL, TEXT -> value;
            case EMAIL -> "mailto:" + value;
            case SMS -> "sms:" + value;
            case PHONE -> "tel:" + value;
            case GEO -> "geo:" + value;
            case WIFI -> formatWifiContent(value);
            case VCARD -> value; // vCard 형식은 클라이언트에서 완성하여 전달
        };
    }

    /**
     * WiFi QR 포맷
     * 입력: SSID:password:WPA (또는 WEP, nopass)
     */
    private String formatWifiContent(String value) {
        String[] parts = value.split(":");
        if (parts.length >= 2) {
            String ssid = parts[0];
            String password = parts.length > 1 ? parts[1] : "";
            String type = parts.length > 2 ? parts[2].toUpperCase() : "WPA";
            return String.format("WIFI:T:%s;S:%s;P:%s;;", type, ssid, password);
        }
        return value;
    }

    /**
     * 에러 보정 레벨 파싱
     */
    private ErrorCorrectionLevel getErrorCorrectionLevel(String level) {
        if (level == null) return ErrorCorrectionLevel.M;
        return switch (level.toUpperCase()) {
            case "L" -> ErrorCorrectionLevel.L;
            case "Q" -> ErrorCorrectionLevel.Q;
            case "H" -> ErrorCorrectionLevel.H;
            default -> ErrorCorrectionLevel.M;
        };
    }

    /**
     * HEX 색상 파싱
     */
    private int parseColor(String hexColor, int defaultColor) {
        if (hexColor == null || hexColor.isEmpty()) {
            return defaultColor;
        }
        try {
            String hex = hexColor.startsWith("#") ? hexColor.substring(1) : hexColor;
            return 0xFF000000 | Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            return defaultColor;
        }
    }

    /**
     * 이미지 파일 삭제
     */
    private void deleteImageFile(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) return;

        try {
            // /qrgen/images/xxx.png → uploads/qrgen/images/xxx.png
            String fileName = imagePath.replace("/qrgen/images/", "");
            Path filePath = Paths.get(savePath, fileName);
            Files.deleteIfExists(filePath);
            log.info("✅ [CHECK] 이미지 파일 삭제: {}", filePath);
        } catch (IOException e) {
            log.error("❌ [ERROR] 이미지 파일 삭제 실패: {}", e.getMessage());
        }
    }
}
