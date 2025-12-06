package co.grap.pack.user.image.service;

import co.grap.pack.common.util.ImageCompressUtil;
import co.grap.pack.user.image.mapper.ExhibitionImageMapper;
import co.grap.pack.user.image.mapper.FestivalImageMapper;
import co.grap.pack.user.image.mapper.WelfareServiceImageMapper;
import co.grap.pack.user.image.model.ExhibitionImage;
import co.grap.pack.user.image.model.FestivalImage;
import co.grap.pack.user.image.model.WelfareServiceImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 사용자 이미지 서비스
 * - 축제/행사, 공연/전시, 복지서비스 이미지 공통 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserImageService {

    private final ImageCompressUtil imageCompressUtil;
    private final FestivalImageMapper festivalImageMapper;
    private final ExhibitionImageMapper exhibitionImageMapper;
    private final WelfareServiceImageMapper welfareServiceImageMapper;

    @Value("${file.max-image-count:10}")
    private int maxImageCount;

    // ==================== 축제/행사 이미지 ====================

    /**
     * 축제/행사 이미지 업로드
     */
    @Transactional
    public FestivalImage uploadFestivalImage(Long festivalId, MultipartFile file, Integer displayOrder) throws IOException {
        // 최대 개수 체크
        int currentCount = festivalImageMapper.selectImageCountByFestivalId(festivalId);
        if (currentCount >= maxImageCount) {
            throw new IllegalStateException("이미지는 최대 " + maxImageCount + "개까지 등록 가능합니다.");
        }

        // 이미지 압축 및 저장
        String savedName = imageCompressUtil.compressAndSave(file);
        String filePath = imageCompressUtil.getFilePath(savedName);
        long fileSize = imageCompressUtil.getFileSize(savedName);

        // DB 저장
        FestivalImage image = FestivalImage.builder()
                .festivalId(festivalId)
                .originalName(file.getOriginalFilename())
                .savedName(savedName)
                .filePath(filePath)
                .fileSize(fileSize)
                .isThumbnail(false)
                .displayOrder(displayOrder != null ? displayOrder : 0)
                .build();

        festivalImageMapper.insertImage(image);
        log.info("✅ [CHECK] 축제/행사 이미지 업로드 완료: festivalId={}, imageId={}", festivalId, image.getImageId());

        return image;
    }

    /**
     * 축제/행사 이미지 목록 조회
     */
    public List<FestivalImage> getFestivalImages(Long festivalId) {
        return festivalImageMapper.selectImagesByFestivalId(festivalId);
    }

    /**
     * 축제/행사 썸네일 설정
     */
    @Transactional
    public void setFestivalThumbnail(Long festivalId, Long imageId) {
        festivalImageMapper.updateThumbnail(festivalId, imageId);
        log.info("✅ [CHECK] 축제/행사 썸네일 설정: festivalId={}, imageId={}", festivalId, imageId);
    }

    /**
     * 축제/행사 이미지 삭제
     */
    @Transactional
    public void deleteFestivalImage(Long imageId) throws IOException {
        FestivalImage image = festivalImageMapper.selectImageById(imageId);
        if (image != null) {
            imageCompressUtil.deleteFile(image.getSavedName());
            festivalImageMapper.deleteImage(imageId);
            log.info("✅ [CHECK] 축제/행사 이미지 삭제: imageId={}", imageId);
        }
    }

    /**
     * 축제/행사 이미지 일괄 업로드
     * @param festivalId 축제/행사 ID
     * @param files 이미지 파일 목록
     * @param thumbnailIndex 썸네일로 설정할 이미지 인덱스 (null이면 썸네일 없음)
     */
    @Transactional
    public void uploadFestivalImages(Long festivalId, List<MultipartFile> files, Integer thumbnailIndex) throws IOException {
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            FestivalImage image = uploadFestivalImage(festivalId, file, i);

            // 썸네일 설정
            if (thumbnailIndex != null && thumbnailIndex == i) {
                setFestivalThumbnail(festivalId, image.getImageId());
            }
        }
        log.info("✅ [CHECK] 축제/행사 이미지 일괄 업로드 완료: festivalId={}, 총 {}개", festivalId, files.size());
    }

    // ==================== 공연/전시 이미지 ====================

    /**
     * 공연/전시 이미지 업로드
     */
    @Transactional
    public ExhibitionImage uploadExhibitionImage(Long exhibitionId, MultipartFile file, Integer displayOrder) throws IOException {
        // 최대 개수 체크
        int currentCount = exhibitionImageMapper.selectImageCountByExhibitionId(exhibitionId);
        if (currentCount >= maxImageCount) {
            throw new IllegalStateException("이미지는 최대 " + maxImageCount + "개까지 등록 가능합니다.");
        }

        // 이미지 압축 및 저장
        String savedName = imageCompressUtil.compressAndSave(file);
        String filePath = imageCompressUtil.getFilePath(savedName);
        long fileSize = imageCompressUtil.getFileSize(savedName);

        // DB 저장
        ExhibitionImage image = ExhibitionImage.builder()
                .exhibitionId(exhibitionId)
                .originalName(file.getOriginalFilename())
                .savedName(savedName)
                .filePath(filePath)
                .fileSize(fileSize)
                .isThumbnail(false)
                .displayOrder(displayOrder != null ? displayOrder : 0)
                .build();

        exhibitionImageMapper.insertImage(image);
        log.info("✅ [CHECK] 공연/전시 이미지 업로드 완료: exhibitionId={}, imageId={}", exhibitionId, image.getImageId());

        return image;
    }

    /**
     * 공연/전시 이미지 목록 조회
     */
    public List<ExhibitionImage> getExhibitionImages(Long exhibitionId) {
        return exhibitionImageMapper.selectImagesByExhibitionId(exhibitionId);
    }

    /**
     * 공연/전시 썸네일 설정
     */
    @Transactional
    public void setExhibitionThumbnail(Long exhibitionId, Long imageId) {
        exhibitionImageMapper.updateThumbnail(exhibitionId, imageId);
        log.info("✅ [CHECK] 공연/전시 썸네일 설정: exhibitionId={}, imageId={}", exhibitionId, imageId);
    }

    /**
     * 공연/전시 이미지 삭제
     */
    @Transactional
    public void deleteExhibitionImage(Long imageId) throws IOException {
        ExhibitionImage image = exhibitionImageMapper.selectImageById(imageId);
        if (image != null) {
            imageCompressUtil.deleteFile(image.getSavedName());
            exhibitionImageMapper.deleteImage(imageId);
            log.info("✅ [CHECK] 공연/전시 이미지 삭제: imageId={}", imageId);
        }
    }

    /**
     * 공연/전시 이미지 일괄 업로드
     * @param exhibitionId 공연/전시 ID
     * @param files 이미지 파일 목록
     * @param thumbnailIndex 썸네일로 설정할 이미지 인덱스 (null이면 썸네일 없음)
     */
    @Transactional
    public void uploadExhibitionImages(Long exhibitionId, List<MultipartFile> files, Integer thumbnailIndex) throws IOException {
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            ExhibitionImage image = uploadExhibitionImage(exhibitionId, file, i);

            // 썸네일 설정
            if (thumbnailIndex != null && thumbnailIndex == i) {
                setExhibitionThumbnail(exhibitionId, image.getImageId());
            }
        }
        log.info("✅ [CHECK] 공연/전시 이미지 일괄 업로드 완료: exhibitionId={}, 총 {}개", exhibitionId, files.size());
    }

    // ==================== 복지서비스 이미지 ====================

    /**
     * 복지서비스 이미지 업로드
     */
    @Transactional
    public WelfareServiceImage uploadWelfareServiceImage(Long welfareServiceId, MultipartFile file, Integer displayOrder) throws IOException {
        // 최대 개수 체크
        int currentCount = welfareServiceImageMapper.selectImageCountByWelfareServiceId(welfareServiceId);
        if (currentCount >= maxImageCount) {
            throw new IllegalStateException("이미지는 최대 " + maxImageCount + "개까지 등록 가능합니다.");
        }

        // 이미지 압축 및 저장
        String savedName = imageCompressUtil.compressAndSave(file);
        String filePath = imageCompressUtil.getFilePath(savedName);
        long fileSize = imageCompressUtil.getFileSize(savedName);

        // DB 저장
        WelfareServiceImage image = WelfareServiceImage.builder()
                .welfareServiceId(welfareServiceId)
                .originalName(file.getOriginalFilename())
                .savedName(savedName)
                .filePath(filePath)
                .fileSize(fileSize)
                .isThumbnail(false)
                .displayOrder(displayOrder != null ? displayOrder : 0)
                .build();

        welfareServiceImageMapper.insertImage(image);
        log.info("✅ [CHECK] 복지서비스 이미지 업로드 완료: welfareServiceId={}, imageId={}", welfareServiceId, image.getImageId());

        return image;
    }

    /**
     * 복지서비스 이미지 목록 조회
     */
    public List<WelfareServiceImage> getWelfareServiceImages(Long welfareServiceId) {
        return welfareServiceImageMapper.selectImagesByWelfareServiceId(welfareServiceId);
    }

    /**
     * 복지서비스 썸네일 설정
     */
    @Transactional
    public void setWelfareServiceThumbnail(Long welfareServiceId, Long imageId) {
        welfareServiceImageMapper.updateThumbnail(welfareServiceId, imageId);
        log.info("✅ [CHECK] 복지서비스 썸네일 설정: welfareServiceId={}, imageId={}", welfareServiceId, imageId);
    }

    /**
     * 복지서비스 이미지 삭제
     */
    @Transactional
    public void deleteWelfareServiceImage(Long imageId) throws IOException {
        WelfareServiceImage image = welfareServiceImageMapper.selectImageById(imageId);
        if (image != null) {
            imageCompressUtil.deleteFile(image.getSavedName());
            welfareServiceImageMapper.deleteImage(imageId);
            log.info("✅ [CHECK] 복지서비스 이미지 삭제: imageId={}", imageId);
        }
    }

    /**
     * 복지서비스 이미지 일괄 업로드
     * @param welfareServiceId 복지서비스 ID
     * @param files 이미지 파일 목록
     * @param thumbnailIndex 썸네일로 설정할 이미지 인덱스 (null이면 썸네일 없음)
     */
    @Transactional
    public void uploadWelfareServiceImages(Long welfareServiceId, List<MultipartFile> files, Integer thumbnailIndex) throws IOException {
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            WelfareServiceImage image = uploadWelfareServiceImage(welfareServiceId, file, i);

            // 썸네일 설정
            if (thumbnailIndex != null && thumbnailIndex == i) {
                setWelfareServiceThumbnail(welfareServiceId, image.getImageId());
            }
        }
        log.info("✅ [CHECK] 복지서비스 이미지 일괄 업로드 완료: welfareServiceId={}, 총 {}개", welfareServiceId, files.size());
    }
}
