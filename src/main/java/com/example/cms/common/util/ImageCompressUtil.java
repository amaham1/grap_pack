package com.example.cms.common.util;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 이미지 압축 유틸리티
 * - 품질 유지하면서 용량 최소화
 * - 최대 해상도 제한 (1920x1920)
 * - JPEG 품질 85%로 압축
 */
@Slf4j
@Component
public class ImageCompressUtil {

    @Value("${file.upload-dir}")
    private String uploadDir;

    /** JPEG 압축 품질 (0.0 ~ 1.0) */
    private static final double JPEG_QUALITY = 0.85;

    /** 최대 해상도 (가로/세로 중 큰 값 기준) */
    private static final int MAX_RESOLUTION = 1920;

    /** 지원하는 이미지 확장자 */
    private static final String[] SUPPORTED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};

    /**
     * 이미지 파일 압축 후 저장
     * @param file 업로드된 이미지 파일
     * @return 저장된 파일명
     */
    public String compressAndSave(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 저장할 수 없습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isImageFile(originalFilename)) {
            throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다.");
        }

        // 업로드 디렉토리 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 원본 이미지 읽기
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new IllegalArgumentException("이미지 파일을 읽을 수 없습니다.");
        }

        // 고유 파일명 생성 (항상 .jpg로 저장)
        String savedFilename = generateUniqueFilename() + ".jpg";
        Path outputPath = Paths.get(uploadDir, savedFilename);

        // 이미지 압축 및 저장
        compressImage(originalImage, outputPath);

        long originalSize = file.getSize();
        long compressedSize = Files.size(outputPath);
        double compressionRatio = (1 - (double) compressedSize / originalSize) * 100;

        log.info("✅ [CHECK] 이미지 압축 완료: {} -> {} (원본: {}KB, 압축: {}KB, 압축률: {}%)",
                originalFilename, savedFilename,
                originalSize / 1024, compressedSize / 1024, String.format("%.1f", compressionRatio));

        return savedFilename;
    }

    /**
     * 이미지 압축 처리
     * @param image 원본 이미지
     * @param outputPath 출력 경로
     */
    private void compressImage(BufferedImage image, Path outputPath) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();

        // 최대 해상도 초과 시 리사이징
        if (width > MAX_RESOLUTION || height > MAX_RESOLUTION) {
            double scale = Math.min((double) MAX_RESOLUTION / width, (double) MAX_RESOLUTION / height);
            Thumbnails.of(image)
                    .scale(scale)
                    .outputQuality(JPEG_QUALITY)
                    .outputFormat("jpg")
                    .toFile(outputPath.toFile());
        } else {
            // 리사이징 없이 품질만 압축
            Thumbnails.of(image)
                    .scale(1.0)
                    .outputQuality(JPEG_QUALITY)
                    .outputFormat("jpg")
                    .toFile(outputPath.toFile());
        }
    }

    /**
     * 이미지 파일 여부 확인
     * @param filename 파일명
     * @return 이미지 파일 여부
     */
    public boolean isImageFile(String filename) {
        if (filename == null) {
            return false;
        }
        String lowerFilename = filename.toLowerCase();
        for (String ext : SUPPORTED_EXTENSIONS) {
            if (lowerFilename.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 파일 삭제
     * @param filename 파일명
     */
    public void deleteFile(String filename) throws IOException {
        Path path = Paths.get(uploadDir, filename);
        Files.deleteIfExists(path);
        log.info("✅ [CHECK] 이미지 삭제: {}", filename);
    }

    /**
     * 고유한 파일명 생성
     * @return 생성된 파일명 (확장자 제외)
     */
    private String generateUniqueFilename() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + "_" + uuid;
    }

    /**
     * 파일 경로 반환
     * @param filename 파일명
     * @return 파일 전체 경로
     */
    public String getFilePath(String filename) {
        return Paths.get(uploadDir, filename).toString();
    }

    /**
     * 파일 크기 반환
     * @param filename 파일명
     * @return 파일 크기 (bytes)
     */
    public long getFileSize(String filename) throws IOException {
        Path path = Paths.get(uploadDir, filename);
        return Files.size(path);
    }
}
