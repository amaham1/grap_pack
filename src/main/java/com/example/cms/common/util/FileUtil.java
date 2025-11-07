package com.example.cms.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 파일 처리 유틸리티
 */
@Component
public class FileUtil {

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 파일 저장
     * @param file 업로드 파일
     * @return 저장된 파일명
     */
    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 저장할 수 없습니다.");
        }

        // 업로드 디렉토리 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 고유한 파일명 생성
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFilename = generateUniqueFilename() + extension;

        // 파일 저장
        Path path = Paths.get(uploadDir, savedFilename);
        Files.write(path, file.getBytes());

        return savedFilename;
    }

    /**
     * 파일 삭제
     * @param filename 파일명
     */
    public void deleteFile(String filename) throws IOException {
        Path path = Paths.get(uploadDir, filename);
        Files.deleteIfExists(path);
    }

    /**
     * 고유한 파일명 생성
     * @return 생성된 파일명
     */
    private String generateUniqueFilename() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + "_" + uuid;
    }

    /**
     * 파일 경로 반환
     * @param filename 파일명
     * @return 파일 경로
     */
    public String getFilePath(String filename) {
        return uploadDir + "/" + filename;
    }
}
