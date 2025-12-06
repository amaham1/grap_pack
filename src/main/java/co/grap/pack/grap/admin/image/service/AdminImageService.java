package co.grap.pack.grap.admin.image.service;

import co.grap.pack.grap.admin.image.mapper.AdminImageMapper;
import co.grap.pack.grap.admin.image.model.AdminImage;
import co.grap.pack.common.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 관리자 이미지 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminImageService {

    private final AdminImageMapper adminImageMapper;
    private final FileUtil fileUtil;

    @Value("${file.max-image-count}")
    private int maxImageCount;

    /**
     * 콘텐츠별 이미지 목록 조회
     */
    public List<AdminImage> getImageList(Long contentId) {
        return adminImageMapper.selectImageListByContentId(contentId);
    }

    /**
     * 이미지 상세 조회
     */
    public AdminImage getImage(Long imageId) {
        return adminImageMapper.selectImageById(imageId);
    }

    /**
     * 이미지 업로드
     */
    @Transactional
    public void uploadImage(Long contentId, MultipartFile file, Integer displayOrder) throws IOException {
        // 최대 이미지 개수 체크
        int currentCount = adminImageMapper.selectImageCountByContentId(contentId);
        if (currentCount >= maxImageCount) {
            throw new IllegalStateException("이미지는 최대 " + maxImageCount + "개까지 등록 가능합니다.");
        }

        // 파일 저장
        String savedName = fileUtil.saveFile(file);
        String filePath = fileUtil.getFilePath(savedName);

        // 이미지 정보 저장
        AdminImage image = AdminImage.builder()
                .contentId(contentId)
                .originalName(file.getOriginalFilename())
                .savedName(savedName)
                .filePath(filePath)
                .fileSize(file.getSize())
                .displayOrder(displayOrder != null ? displayOrder : 0)
                .build();

        adminImageMapper.insertImage(image);
    }

    /**
     * 이미지 삭제
     */
    @Transactional
    public void deleteImage(Long imageId) throws IOException {
        // 이미지 정보 조회
        AdminImage image = adminImageMapper.selectImageById(imageId);
        if (image == null) {
            throw new IllegalArgumentException("이미지를 찾을 수 없습니다.");
        }

        // 파일 삭제
        fileUtil.deleteFile(image.getSavedName());

        // DB에서 삭제
        adminImageMapper.deleteImage(imageId);
    }

    /**
     * 콘텐츠의 이미지 전체 삭제
     */
    @Transactional
    public void deleteImagesByContentId(Long contentId) throws IOException {
        // 모든 이미지 조회
        List<AdminImage> imageList = adminImageMapper.selectImageListByContentId(contentId);

        // 파일 삭제
        for (AdminImage image : imageList) {
            fileUtil.deleteFile(image.getSavedName());
        }

        // DB에서 삭제
        adminImageMapper.deleteImagesByContentId(contentId);
    }
}
