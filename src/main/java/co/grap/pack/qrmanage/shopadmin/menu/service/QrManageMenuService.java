package co.grap.pack.qrmanage.shopadmin.menu.service;

import co.grap.pack.qrmanage.shopadmin.category.mapper.QrManageCategoryMapper;
import co.grap.pack.qrmanage.shopadmin.menu.mapper.QrManageMenuMapper;
import co.grap.pack.qrmanage.shopadmin.menu.model.QrManageMenu;
import co.grap.pack.qrmanage.shopadmin.menu.model.QrManageMenuOption;
import co.grap.pack.qrmanage.shopadmin.menu.model.QrManageMenuOptionGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 메뉴 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrManageMenuService {

    private static final String ACCESS_DENIED_MESSAGE = "접근 권한이 없습니다.";

    private final QrManageMenuMapper menuMapper;
    private final QrManageCategoryMapper categoryMapper;

    // ========== 메뉴 ==========

    /**
     * 점포의 메뉴 목록 조회
     */
    public List<QrManageMenu> getMenus(Long shopId) {
        return menuMapper.findByShopId(shopId);
    }

    /**
     * 점포 소유 카테고리의 메뉴 목록 조회
     */
    public List<QrManageMenu> getMenusByCategory(Long shopId, Long categoryId) {
        validateCategoryOwnership(categoryId, shopId);
        return menuMapper.findByCategoryIdAndShopId(categoryId, shopId);
    }

    /**
     * 공개 메뉴 조회
     */
    public List<QrManageMenu> getVisibleMenus(Long shopId) {
        return menuMapper.findVisibleByShopId(shopId);
    }

    /**
     * 메뉴 조회
     */
    public QrManageMenu getMenu(Long shopId, Long id) {
        validateMenuOwnership(id, shopId);
        QrManageMenu menu = menuMapper.findById(id);
        if (menu != null) {
            List<QrManageMenuOptionGroup> optionGroups = menuMapper.findOptionGroupsByMenuId(id);
            for (QrManageMenuOptionGroup group : optionGroups) {
                group.setOptions(menuMapper.findOptionsByGroupId(group.getId()));
            }
            menu.setOptionGroups(optionGroups);
        }
        return menu;
    }

    /**
     * 메뉴가 해당 점포 소유인지 확인
     */
    public boolean isOwnedByShop(Long menuId, Long shopId) {
        return menuMapper.existsByIdAndShopId(menuId, shopId);
    }

    /**
     * 메뉴 등록
     */
    @Transactional
    public QrManageMenu createMenu(Long shopId, Long categoryId, String name, String description, Integer price) {
        validateCategoryOwnership(categoryId, shopId);
        Integer nextSortOrder = menuMapper.getNextSortOrder(categoryId);

        QrManageMenu menu = QrManageMenu.builder()
                .shopId(shopId)
                .categoryId(categoryId)
                .name(name)
                .description(description)
                .price(price)
                .sortOrder(nextSortOrder)
                .isVisible(true)
                .isSoldOut(false)
                .build();

        menuMapper.insert(menu);
        log.info("✅ [CHECK] 메뉴 등록 완료: menuId={}, shopId={}", menu.getId(), shopId);
        return menu;
    }

    /**
     * 메뉴 수정
     */
    @Transactional
    public void updateMenu(Long shopId, Long id, Long categoryId, String name, String description, Integer price) {
        validateMenuOwnership(id, shopId);
        validateCategoryOwnership(categoryId, shopId);

        QrManageMenu menu = QrManageMenu.builder()
                .id(id)
                .shopId(shopId)
                .categoryId(categoryId)
                .name(name)
                .description(description)
                .price(price)
                .build();

        menuMapper.update(menu);
        log.info("✅ [CHECK] 메뉴 수정 완료: menuId={}, shopId={}", id, shopId);
    }

    /**
     * 메뉴 삭제
     */
    @Transactional
    public void deleteMenu(Long shopId, Long id) {
        validateMenuOwnership(id, shopId);

        List<QrManageMenuOptionGroup> optionGroups = menuMapper.findOptionGroupsByMenuId(id);
        for (QrManageMenuOptionGroup group : optionGroups) {
            menuMapper.deleteOptionsByGroupIdForShop(group.getId(), id, shopId);
            menuMapper.deleteOptionGroupForShop(group.getId(), id, shopId);
        }
        menuMapper.delete(id, shopId);
        log.info("✅ [CHECK] 메뉴 삭제 완료: menuId={}, shopId={}", id, shopId);
    }

    /**
     * 공개 여부 변경
     */
    @Transactional
    public void updateVisibility(Long shopId, Long id, Boolean isVisible) {
        validateMenuOwnership(id, shopId);
        menuMapper.updateVisibility(id, shopId, isVisible);
        log.info("✅ [CHECK] 메뉴 공개 여부 변경: menuId={}, shopId={}, isVisible={}", id, shopId, isVisible);
    }

    /**
     * 품절 여부 변경
     */
    @Transactional
    public void updateSoldOut(Long shopId, Long id, Boolean isSoldOut) {
        validateMenuOwnership(id, shopId);
        menuMapper.updateSoldOut(id, shopId, isSoldOut);
        log.info("✅ [CHECK] 메뉴 품절 여부 변경: menuId={}, shopId={}, isSoldOut={}", id, shopId, isSoldOut);
    }

    /**
     * 대표 이미지 변경
     */
    @Transactional
    public void updatePrimaryImage(Long shopId, Long id, Long primaryImageId) {
        validateMenuOwnership(id, shopId);
        menuMapper.updatePrimaryImage(id, primaryImageId, shopId);
        log.info("✅ [CHECK] 메뉴 대표 이미지 변경: menuId={}, shopId={}, imageId={}", id, shopId, primaryImageId);
    }

    /**
     * 정렬 순서 일괄 변경
     */
    @Transactional
    public void updateSortOrders(Long shopId, List<Long> menuIds) {
        List<Long> normalizedIds = normalizeIds(menuIds);
        if (normalizedIds.isEmpty()) {
            return;
        }

        int ownedCount = menuMapper.countByIdsAndShopId(normalizedIds, shopId);
        if (ownedCount != normalizedIds.size()) {
            throw new SecurityException(ACCESS_DENIED_MESSAGE);
        }

        for (int i = 0; i < normalizedIds.size(); i++) {
            menuMapper.updateSortOrder(normalizedIds.get(i), shopId, i + 1);
        }
        log.info("✅ [CHECK] 메뉴 정렬 순서 일괄 변경 완료: shopId={}, count={}", shopId, normalizedIds.size());
    }

    // ========== 옵션 그룹 ==========

    /**
     * 메뉴의 옵션 그룹 목록 조회
     */
    public List<QrManageMenuOptionGroup> getOptionGroups(Long shopId, Long menuId) {
        validateMenuOwnership(menuId, shopId);
        List<QrManageMenuOptionGroup> groups = menuMapper.findOptionGroupsByMenuId(menuId);
        for (QrManageMenuOptionGroup group : groups) {
            group.setOptions(menuMapper.findOptionsByGroupId(group.getId()));
        }
        return groups;
    }

    /**
     * 옵션 그룹 조회
     */
    public QrManageMenuOptionGroup getOptionGroup(Long id) {
        QrManageMenuOptionGroup group = menuMapper.findOptionGroupById(id);
        if (group != null) {
            group.setOptions(menuMapper.findOptionsByGroupId(id));
        }
        return group;
    }

    /**
     * 옵션 그룹 등록
     */
    @Transactional
    public QrManageMenuOptionGroup createOptionGroup(Long shopId, Long menuId, String name, Boolean isRequired) {
        validateMenuOwnership(menuId, shopId);
        Integer nextSortOrder = menuMapper.getNextOptionGroupSortOrder(menuId);

        QrManageMenuOptionGroup group = QrManageMenuOptionGroup.builder()
                .menuId(menuId)
                .name(name)
                .isRequired(isRequired)
                .sortOrder(nextSortOrder)
                .build();

        menuMapper.insertOptionGroup(group);
        log.info("✅ [CHECK] 옵션 그룹 등록 완료: groupId={}, menuId={}, shopId={}", group.getId(), menuId, shopId);
        return group;
    }

    /**
     * 옵션 그룹 수정
     */
    @Transactional
    public void updateOptionGroup(Long shopId, Long menuId, Long id, String name, Boolean isRequired) {
        validateOptionGroupOwnership(id, menuId, shopId);
        menuMapper.updateOptionGroupForShop(id, menuId, shopId, name, isRequired);
        log.info("✅ [CHECK] 옵션 그룹 수정 완료: groupId={}, menuId={}, shopId={}", id, menuId, shopId);
    }

    /**
     * 옵션 그룹 삭제
     */
    @Transactional
    public void deleteOptionGroup(Long shopId, Long menuId, Long id) {
        validateOptionGroupOwnership(id, menuId, shopId);
        menuMapper.deleteOptionsByGroupIdForShop(id, menuId, shopId);
        menuMapper.deleteOptionGroupForShop(id, menuId, shopId);
        log.info("✅ [CHECK] 옵션 그룹 삭제 완료: groupId={}, menuId={}, shopId={}", id, menuId, shopId);
    }

    /**
     * 옵션 그룹 정렬 순서 일괄 변경
     */
    @Transactional
    public void updateOptionGroupSortOrders(Long shopId, Long menuId, List<Long> groupIds) {
        validateMenuOwnership(menuId, shopId);
        List<Long> normalizedIds = normalizeIds(groupIds);
        for (int i = 0; i < normalizedIds.size(); i++) {
            validateOptionGroupOwnership(normalizedIds.get(i), menuId, shopId);
            menuMapper.updateOptionGroupSortOrderForShop(normalizedIds.get(i), menuId, shopId, i + 1);
        }
        log.info("✅ [CHECK] 옵션 그룹 정렬 순서 일괄 변경 완료: menuId={}, shopId={}", menuId, shopId);
    }

    // ========== 옵션 ==========

    /**
     * 옵션 등록
     */
    @Transactional
    public QrManageMenuOption createOption(Long shopId, Long menuId, Long optionGroupId, String name) {
        validateOptionGroupOwnership(optionGroupId, menuId, shopId);
        Integer nextSortOrder = menuMapper.getNextOptionSortOrder(optionGroupId);

        QrManageMenuOption option = QrManageMenuOption.builder()
                .optionGroupId(optionGroupId)
                .name(name)
                .sortOrder(nextSortOrder)
                .build();

        menuMapper.insertOption(option);
        log.info("✅ [CHECK] 옵션 등록 완료: optionId={}, groupId={}, menuId={}, shopId={}",
                option.getId(), optionGroupId, menuId, shopId);
        return option;
    }

    /**
     * 옵션 수정
     */
    @Transactional
    public void updateOption(Long shopId, Long menuId, Long id, String name) {
        validateOptionOwnership(id, menuId, shopId);
        menuMapper.updateOptionForShop(id, menuId, shopId, name);
        log.info("✅ [CHECK] 옵션 수정 완료: optionId={}, menuId={}, shopId={}", id, menuId, shopId);
    }

    /**
     * 옵션 삭제
     */
    @Transactional
    public void deleteOption(Long shopId, Long menuId, Long id) {
        validateOptionOwnership(id, menuId, shopId);
        menuMapper.deleteOptionForShop(id, menuId, shopId);
        log.info("✅ [CHECK] 옵션 삭제 완료: optionId={}, menuId={}, shopId={}", id, menuId, shopId);
    }

    /**
     * 옵션 정렬 순서 일괄 변경
     */
    @Transactional
    public void updateOptionSortOrders(Long shopId, Long menuId, List<Long> optionIds) {
        validateMenuOwnership(menuId, shopId);
        List<Long> normalizedIds = normalizeIds(optionIds);
        for (int i = 0; i < normalizedIds.size(); i++) {
            validateOptionOwnership(normalizedIds.get(i), menuId, shopId);
            menuMapper.updateOptionSortOrderForShop(normalizedIds.get(i), menuId, shopId, i + 1);
        }
        log.info("✅ [CHECK] 옵션 정렬 순서 일괄 변경 완료: menuId={}, shopId={}", menuId, shopId);
    }

    private void validateMenuOwnership(Long menuId, Long shopId) {
        if (menuId == null || shopId == null || !menuMapper.existsByIdAndShopId(menuId, shopId)) {
            throw new SecurityException(ACCESS_DENIED_MESSAGE);
        }
    }

    private void validateCategoryOwnership(Long categoryId, Long shopId) {
        if (categoryId == null || shopId == null || !categoryMapper.existsByIdAndShopId(categoryId, shopId)) {
            throw new SecurityException(ACCESS_DENIED_MESSAGE);
        }
    }

    private void validateOptionGroupOwnership(Long groupId, Long menuId, Long shopId) {
        if (groupId == null
                || menuId == null
                || shopId == null
                || !menuMapper.existsOptionGroupByIdAndMenuIdAndShopId(groupId, menuId, shopId)) {
            throw new SecurityException(ACCESS_DENIED_MESSAGE);
        }
    }

    private void validateOptionOwnership(Long optionId, Long menuId, Long shopId) {
        if (optionId == null
                || menuId == null
                || shopId == null
                || !menuMapper.existsOptionByIdAndMenuIdAndShopId(optionId, menuId, shopId)) {
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
