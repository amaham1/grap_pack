package co.grap.pack.qrmanage.shopadmin.image.service;

import co.grap.pack.qrmanage.shopadmin.image.mapper.QrManageMenuImageMapper;
import co.grap.pack.qrmanage.shopadmin.image.model.QrManageMenuImage;
import co.grap.pack.qrmanage.shopadmin.menu.mapper.QrManageMenuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

/**
 * 메뉴 이미지 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrManageMenuImageService {

    private static final String ACCESS_DENIED_MESSAGE = "접근 권한이 없습니다.";

    private final QrManageMenuImageMapper imageMapper;
    private final QrManageMenuMapper menuMapper;

    @Value("${qrmanage.upload.path:uploads/qrmanage}")
    private String uploadPath;

    @Value("${qrmanage.upload.max-width:800}")
    private int maxWidth;

    @Value("${qrmanage.upload.compression-quality:0.8}")
    private float compressionQuality;

    /**
     * 메뉴의 이미지 목록 조회
     */
    public List<QrManageMenuImage> getImages(Long shopId, Long menuId) {
        validateMenuOwnership(menuId, shopId);
        return imageMapper.findByMenuId(menuId);
    }

    /**
     * 이미지 조회
     */
    public QrManageMenuImage getImage(Long id) {
        return imageMapper.findById(id);
    }

    /**
     * 이미지 업로드
     */
    @Transactional
    public QrManageMenuImage uploadImage(Long shopId, Long menuId, MultipartFile file) throws IOException {
        validateMenuOwnership(menuId, shopId);
        log.info("✅ [CHECK] 이미지 업로드 시작: menuId={}, shopId={}, fileName={}",
                menuId, shopId, file.getOriginalFilename());

        String originalFileName = file.getOriginalFilename();
        String extension = getFileExtension(originalFileName).toLowerCase();
        if (!isValidImageExtension(extension)) {
            throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다.");
        }

        String storedFileName = UUID.randomUUID() + "." + extension;
        Path uploadDir = Paths.get(uploadPath, "menu", String.valueOf(menuId));
        Files.createDirectories(uploadDir);
        Path filePath = uploadDir.resolve(storedFileName);

        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new IllegalArgumentException("이미지 파일을 읽을 수 없습니다.");
        }
        BufferedImage processedImage = resizeAndCompress(originalImage);
        saveCompressedImage(processedImage, filePath.toFile(), extension);

        Integer nextSortOrder = imageMapper.getNextSortOrder(menuId);
        QrManageMenuImage image = QrManageMenuImage.builder()
                .menuId(menuId)
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .filePath("/" + uploadPath + "/menu/" + menuId + "/" + storedFileName)
                .fileSize(Files.size(filePath))
                .fileType(file.getContentType())
                .sortOrder(nextSortOrder)
                .build();

        imageMapper.insert(image);
        log.info("✅ [CHECK] 이미지 업로드 완료: imageId={}, menuId={}, shopId={}", image.getId(), menuId, shopId);

        if (nextSortOrder == 1) {
            menuMapper.updatePrimaryImage(menuId, image.getId(), shopId);
        }

        return image;
    }

    /**
     * 이미지 삭제
     */
    @Transactional
    public void deleteImage(Long shopId, Long id, Long menuId) {
        validateMenuOwnership(menuId, shopId);
        QrManageMenuImage image = imageMapper.findByIdAndMenuId(id, menuId);
        if (image == null) {
            throw new SecurityException(ACCESS_DENIED_MESSAGE);
        }

        try {
            Path filePath = Paths.get(image.getFilePath().substring(1));
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("❌ [ERROR] 이미지 파일 삭제 실패: {}", e.getMessage());
        }

        imageMapper.deleteByIdAndMenuId(id, menuId);

        List<QrManageMenuImage> remainingImages = imageMapper.findByMenuId(menuId);
        if (!remainingImages.isEmpty()) {
            menuMapper.updatePrimaryImage(menuId, remainingImages.get(0).getId(), shopId);
        } else {
            menuMapper.updatePrimaryImage(menuId, null, shopId);
        }

        log.info("✅ [CHECK] 이미지 삭제 완료: imageId={}, menuId={}, shopId={}", id, menuId, shopId);
    }

    /**
     * 대표 이미지 설정
     */
    @Transactional
    public void setPrimaryImage(Long shopId, Long menuId, Long imageId) {
        validateMenuOwnership(menuId, shopId);
        validateImageOwnership(imageId, menuId);
        menuMapper.updatePrimaryImage(menuId, imageId, shopId);
        log.info("✅ [CHECK] 대표 이미지 설정: menuId={}, shopId={}, imageId={}", menuId, shopId, imageId);
    }

    /**
     * 이미지 순서 변경
     */
    @Transactional
    public void updateSortOrders(Long shopId, Long menuId, List<Long> imageIds) {
        validateMenuOwnership(menuId, shopId);
        List<Long> normalizedIds = normalizeIds(imageIds);
        if (normalizedIds.isEmpty()) {
            return;
        }

        int ownedCount = imageMapper.countByIdsAndMenuId(normalizedIds, menuId);
        if (ownedCount != normalizedIds.size()) {
            throw new SecurityException(ACCESS_DENIED_MESSAGE);
        }

        for (int i = 0; i < normalizedIds.size(); i++) {
            imageMapper.updateSortOrderForMenu(normalizedIds.get(i), menuId, i + 1);
        }
        log.info("✅ [CHECK] 이미지 순서 변경 완료: menuId={}, shopId={}, count={}",
                menuId, shopId, normalizedIds.size());
    }

    /**
     * 이미지 리사이즈 및 압축
     */
    private BufferedImage resizeAndCompress(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();

        if (width > maxWidth) {
            double ratio = (double) maxWidth / width;
            int newWidth = maxWidth;
            int newHeight = (int) (height * ratio);

            BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resized.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(original, 0, 0, newWidth, newHeight, null);
            g.dispose();
            return resized;
        }

        BufferedImage rgb = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgb.createGraphics();
        g.drawImage(original, 0, 0, Color.WHITE, null);
        g.dispose();
        return rgb;
    }

    /**
     * 압축된 이미지 저장
     */
    private void saveCompressedImage(BufferedImage image, File output, String extension) throws IOException {
        String formatName = extension.equals("png") ? "png" : "jpg";

        if (formatName.equals("jpg")) {
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            if (writers.hasNext()) {
                ImageWriter writer = writers.next();
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(compressionQuality);

                try (ImageOutputStream ios = ImageIO.createImageOutputStream(output)) {
                    writer.setOutput(ios);
                    writer.write(null, new IIOImage(image, null, null), param);
                } finally {
                    writer.dispose();
                }
            }
        } else {
            ImageIO.write(image, formatName, output);
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 유효한 이미지 확장자인지 확인
     */
    private boolean isValidImageExtension(String extension) {
        return extension.equals("jpg")
                || extension.equals("jpeg")
                || extension.equals("png")
                || extension.equals("gif")
                || extension.equals("webp");
    }

    private void validateMenuOwnership(Long menuId, Long shopId) {
        if (menuId == null || shopId == null || !menuMapper.existsByIdAndShopId(menuId, shopId)) {
            throw new SecurityException(ACCESS_DENIED_MESSAGE);
        }
    }

    private void validateImageOwnership(Long imageId, Long menuId) {
        if (imageId == null || menuId == null || !imageMapper.existsByIdAndMenuId(imageId, menuId)) {
            throw new SecurityException(ACCESS_DENIED_MESSAGE);
        }
    }

    private List<Long> normalizeIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        if (ids.stream().anyMatch(id -> id == null)) {
            throw new SecurityException(ACCESS_DENIED_MESSAGE);
        }

        LinkedHashSet<Long> distinctIds = new LinkedHashSet<>(ids);
        if (distinctIds.size() != ids.size()) {
            throw new SecurityException(ACCESS_DENIED_MESSAGE);
        }
        return new ArrayList<>(distinctIds);
    }
}
