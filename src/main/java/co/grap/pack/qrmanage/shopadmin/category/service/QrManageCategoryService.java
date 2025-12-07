package co.grap.pack.qrmanage.shopadmin.category.service;

import co.grap.pack.qrmanage.shopadmin.category.mapper.QrManageCategoryMapper;
import co.grap.pack.qrmanage.shopadmin.category.model.QrManageCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 카테고리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrManageCategoryService {

    private final QrManageCategoryMapper categoryMapper;

    /**
     * 상점의 카테고리 목록 조회
     */
    public List<QrManageCategory> getCategories(Long shopId) {
        return categoryMapper.findByShopId(shopId);
    }

    /**
     * 공개된 카테고리만 조회
     */
    public List<QrManageCategory> getVisibleCategories(Long shopId) {
        return categoryMapper.findVisibleByShopId(shopId);
    }

    /**
     * 카테고리 조회
     */
    public QrManageCategory getCategory(Long id) {
        return categoryMapper.findById(id);
    }

    /**
     * 카테고리가 해당 상점 소유인지 확인
     */
    public boolean isOwnedByShop(Long categoryId, Long shopId) {
        return categoryMapper.existsByIdAndShopId(categoryId, shopId);
    }

    /**
     * 카테고리 등록
     */
    @Transactional
    public QrManageCategory createCategory(Long shopId, String name, String description) {
        Integer nextSortOrder = categoryMapper.getNextSortOrder(shopId);

        QrManageCategory category = QrManageCategory.builder()
                .shopId(shopId)
                .name(name)
                .description(description)
                .sortOrder(nextSortOrder)
                .isVisible(true)
                .build();

        categoryMapper.insert(category);
        log.info("✅ [CHECK] 카테고리 등록 완료: categoryId={}, shopId={}", category.getId(), shopId);
        return category;
    }

    /**
     * 카테고리 수정
     */
    @Transactional
    public void updateCategory(Long id, String name, String description) {
        QrManageCategory category = QrManageCategory.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();

        categoryMapper.update(category);
        log.info("✅ [CHECK] 카테고리 수정 완료: categoryId={}", id);
    }

    /**
     * 카테고리 삭제
     */
    @Transactional
    public void deleteCategory(Long id) {
        categoryMapper.delete(id);
        log.info("✅ [CHECK] 카테고리 삭제 완료: categoryId={}", id);
    }

    /**
     * 공개 여부 변경
     */
    @Transactional
    public void updateVisibility(Long id, Boolean isVisible) {
        categoryMapper.updateVisibility(id, isVisible);
        log.info("✅ [CHECK] 카테고리 공개 여부 변경: categoryId={}, isVisible={}", id, isVisible);
    }

    /**
     * 정렬 순서 변경
     */
    @Transactional
    public void updateSortOrder(Long id, Integer sortOrder) {
        categoryMapper.updateSortOrder(id, sortOrder);
        log.info("✅ [CHECK] 카테고리 정렬 순서 변경: categoryId={}, sortOrder={}", id, sortOrder);
    }

    /**
     * 정렬 순서 일괄 변경
     */
    @Transactional
    public void updateSortOrders(List<Long> categoryIds) {
        for (int i = 0; i < categoryIds.size(); i++) {
            categoryMapper.updateSortOrder(categoryIds.get(i), i + 1);
        }
        log.info("✅ [CHECK] 카테고리 정렬 순서 일괄 변경 완료");
    }
}
