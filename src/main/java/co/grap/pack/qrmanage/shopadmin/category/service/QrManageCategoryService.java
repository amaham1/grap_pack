package co.grap.pack.qrmanage.shopadmin.category.service;

import co.grap.pack.qrmanage.shopadmin.category.mapper.QrManageCategoryMapper;
import co.grap.pack.qrmanage.shopadmin.category.model.QrManageCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 카테고리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrManageCategoryService {

    private static final String ACCESS_DENIED_MESSAGE = "접근 권한이 없습니다.";

    private final QrManageCategoryMapper categoryMapper;

    /**
     * 점포의 카테고리 목록 조회
     */
    public List<QrManageCategory> getCategories(Long shopId) {
        return categoryMapper.findByShopId(shopId);
    }

    /**
     * 공개 카테고리 목록 조회
     */
    public List<QrManageCategory> getVisibleCategories(Long shopId) {
        return categoryMapper.findVisibleByShopId(shopId);
    }

    /**
     * 카테고리 조회
     */
    public QrManageCategory getCategory(Long shopId, Long id) {
        validateCategoryOwnership(id, shopId);
        return categoryMapper.findById(id);
    }

    /**
     * 카테고리가 해당 점포 소유인지 확인
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
    public void updateCategory(Long shopId, Long id, String name, String description) {
        validateCategoryOwnership(id, shopId);

        QrManageCategory category = QrManageCategory.builder()
                .id(id)
                .shopId(shopId)
                .name(name)
                .description(description)
                .build();

        categoryMapper.update(category);
        log.info("✅ [CHECK] 카테고리 수정 완료: categoryId={}, shopId={}", id, shopId);
    }

    /**
     * 카테고리 삭제
     */
    @Transactional
    public void deleteCategory(Long shopId, Long id) {
        validateCategoryOwnership(id, shopId);
        categoryMapper.delete(id, shopId);
        log.info("✅ [CHECK] 카테고리 삭제 완료: categoryId={}, shopId={}", id, shopId);
    }

    /**
     * 공개 여부 변경
     */
    @Transactional
    public void updateVisibility(Long shopId, Long id, Boolean isVisible) {
        validateCategoryOwnership(id, shopId);
        categoryMapper.updateVisibility(id, shopId, isVisible);
        log.info("✅ [CHECK] 카테고리 공개 여부 변경: categoryId={}, shopId={}, isVisible={}", id, shopId, isVisible);
    }

    /**
     * 정렬 순서 변경
     */
    @Transactional
    public void updateSortOrder(Long shopId, Long id, Integer sortOrder) {
        validateCategoryOwnership(id, shopId);
        categoryMapper.updateSortOrderForShop(id, shopId, sortOrder);
        log.info("✅ [CHECK] 카테고리 정렬 순서 변경: categoryId={}, shopId={}, sortOrder={}", id, shopId, sortOrder);
    }

    /**
     * 정렬 순서 일괄 변경
     */
    @Transactional
    public void updateSortOrders(Long shopId, List<Long> categoryIds) {
        List<Long> normalizedIds = normalizeIds(categoryIds);
        if (normalizedIds.isEmpty()) {
            return;
        }

        int ownedCount = categoryMapper.countByIdsAndShopId(normalizedIds, shopId);
        if (ownedCount != normalizedIds.size()) {
            throw new SecurityException(ACCESS_DENIED_MESSAGE);
        }

        for (int i = 0; i < normalizedIds.size(); i++) {
            categoryMapper.updateSortOrderForShop(normalizedIds.get(i), shopId, i + 1);
        }
        log.info("✅ [CHECK] 카테고리 정렬 순서 일괄 변경 완료: shopId={}, count={}", shopId, normalizedIds.size());
    }

    private void validateCategoryOwnership(Long categoryId, Long shopId) {
        if (categoryId == null || shopId == null || !categoryMapper.existsByIdAndShopId(categoryId, shopId)) {
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
