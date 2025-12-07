package co.grap.pack.qrmanage.shopadmin.menu.service;

import co.grap.pack.qrmanage.shopadmin.menu.mapper.QrManageMenuMapper;
import co.grap.pack.qrmanage.shopadmin.menu.model.QrManageMenu;
import co.grap.pack.qrmanage.shopadmin.menu.model.QrManageMenuOption;
import co.grap.pack.qrmanage.shopadmin.menu.model.QrManageMenuOptionGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 메뉴 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrManageMenuService {

    private final QrManageMenuMapper menuMapper;

    // ========== 메뉴 ==========

    /**
     * 상점의 메뉴 목록 조회
     */
    public List<QrManageMenu> getMenus(Long shopId) {
        return menuMapper.findByShopId(shopId);
    }

    /**
     * 카테고리별 메뉴 목록 조회
     */
    public List<QrManageMenu> getMenusByCategory(Long categoryId) {
        return menuMapper.findByCategoryId(categoryId);
    }

    /**
     * 공개된 메뉴 조회 (고객용)
     */
    public List<QrManageMenu> getVisibleMenus(Long shopId) {
        return menuMapper.findVisibleByShopId(shopId);
    }

    /**
     * 메뉴 조회
     */
    public QrManageMenu getMenu(Long id) {
        QrManageMenu menu = menuMapper.findById(id);
        if (menu != null) {
            // 옵션 그룹과 옵션 로드
            List<QrManageMenuOptionGroup> optionGroups = menuMapper.findOptionGroupsByMenuId(id);
            for (QrManageMenuOptionGroup group : optionGroups) {
                group.setOptions(menuMapper.findOptionsByGroupId(group.getId()));
            }
            menu.setOptionGroups(optionGroups);
        }
        return menu;
    }

    /**
     * 메뉴가 해당 상점 소유인지 확인
     */
    public boolean isOwnedByShop(Long menuId, Long shopId) {
        return menuMapper.existsByIdAndShopId(menuId, shopId);
    }

    /**
     * 메뉴 등록
     */
    @Transactional
    public QrManageMenu createMenu(Long shopId, Long categoryId, String name, String description, Integer price) {
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
    public void updateMenu(Long id, Long categoryId, String name, String description, Integer price) {
        QrManageMenu menu = QrManageMenu.builder()
                .id(id)
                .categoryId(categoryId)
                .name(name)
                .description(description)
                .price(price)
                .build();

        menuMapper.update(menu);
        log.info("✅ [CHECK] 메뉴 수정 완료: menuId={}", id);
    }

    /**
     * 메뉴 삭제
     */
    @Transactional
    public void deleteMenu(Long id) {
        // 옵션 그룹과 옵션 삭제
        List<QrManageMenuOptionGroup> optionGroups = menuMapper.findOptionGroupsByMenuId(id);
        for (QrManageMenuOptionGroup group : optionGroups) {
            menuMapper.deleteOptionsByGroupId(group.getId());
            menuMapper.deleteOptionGroup(group.getId());
        }
        menuMapper.delete(id);
        log.info("✅ [CHECK] 메뉴 삭제 완료: menuId={}", id);
    }

    /**
     * 공개 여부 변경
     */
    @Transactional
    public void updateVisibility(Long id, Boolean isVisible) {
        menuMapper.updateVisibility(id, isVisible);
        log.info("✅ [CHECK] 메뉴 공개 여부 변경: menuId={}, isVisible={}", id, isVisible);
    }

    /**
     * 품절 여부 변경
     */
    @Transactional
    public void updateSoldOut(Long id, Boolean isSoldOut) {
        menuMapper.updateSoldOut(id, isSoldOut);
        log.info("✅ [CHECK] 메뉴 품절 여부 변경: menuId={}, isSoldOut={}", id, isSoldOut);
    }

    /**
     * 대표 이미지 변경
     */
    @Transactional
    public void updatePrimaryImage(Long id, Long primaryImageId) {
        menuMapper.updatePrimaryImage(id, primaryImageId);
        log.info("✅ [CHECK] 메뉴 대표 이미지 변경: menuId={}, imageId={}", id, primaryImageId);
    }

    /**
     * 정렬 순서 일괄 변경
     */
    @Transactional
    public void updateSortOrders(List<Long> menuIds) {
        for (int i = 0; i < menuIds.size(); i++) {
            menuMapper.updateSortOrder(menuIds.get(i), i + 1);
        }
        log.info("✅ [CHECK] 메뉴 정렬 순서 일괄 변경 완료");
    }

    // ========== 옵션 그룹 ==========

    /**
     * 메뉴의 옵션 그룹 목록 조회
     */
    public List<QrManageMenuOptionGroup> getOptionGroups(Long menuId) {
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
    public QrManageMenuOptionGroup createOptionGroup(Long menuId, String name, Boolean isRequired) {
        Integer nextSortOrder = menuMapper.getNextOptionGroupSortOrder(menuId);

        QrManageMenuOptionGroup group = QrManageMenuOptionGroup.builder()
                .menuId(menuId)
                .name(name)
                .isRequired(isRequired)
                .sortOrder(nextSortOrder)
                .build();

        menuMapper.insertOptionGroup(group);
        log.info("✅ [CHECK] 옵션 그룹 등록 완료: groupId={}, menuId={}", group.getId(), menuId);
        return group;
    }

    /**
     * 옵션 그룹 수정
     */
    @Transactional
    public void updateOptionGroup(Long id, String name, Boolean isRequired) {
        QrManageMenuOptionGroup group = QrManageMenuOptionGroup.builder()
                .id(id)
                .name(name)
                .isRequired(isRequired)
                .build();

        menuMapper.updateOptionGroup(group);
        log.info("✅ [CHECK] 옵션 그룹 수정 완료: groupId={}", id);
    }

    /**
     * 옵션 그룹 삭제
     */
    @Transactional
    public void deleteOptionGroup(Long id) {
        menuMapper.deleteOptionsByGroupId(id);
        menuMapper.deleteOptionGroup(id);
        log.info("✅ [CHECK] 옵션 그룹 삭제 완료: groupId={}", id);
    }

    /**
     * 옵션 그룹 정렬 순서 일괄 변경
     */
    @Transactional
    public void updateOptionGroupSortOrders(List<Long> groupIds) {
        for (int i = 0; i < groupIds.size(); i++) {
            menuMapper.updateOptionGroupSortOrder(groupIds.get(i), i + 1);
        }
        log.info("✅ [CHECK] 옵션 그룹 정렬 순서 일괄 변경 완료");
    }

    // ========== 옵션 ==========

    /**
     * 옵션 등록
     */
    @Transactional
    public QrManageMenuOption createOption(Long optionGroupId, String name) {
        Integer nextSortOrder = menuMapper.getNextOptionSortOrder(optionGroupId);

        QrManageMenuOption option = QrManageMenuOption.builder()
                .optionGroupId(optionGroupId)
                .name(name)
                .sortOrder(nextSortOrder)
                .build();

        menuMapper.insertOption(option);
        log.info("✅ [CHECK] 옵션 등록 완료: optionId={}, groupId={}", option.getId(), optionGroupId);
        return option;
    }

    /**
     * 옵션 수정
     */
    @Transactional
    public void updateOption(Long id, String name) {
        QrManageMenuOption option = QrManageMenuOption.builder()
                .id(id)
                .name(name)
                .build();

        menuMapper.updateOption(option);
        log.info("✅ [CHECK] 옵션 수정 완료: optionId={}", id);
    }

    /**
     * 옵션 삭제
     */
    @Transactional
    public void deleteOption(Long id) {
        menuMapper.deleteOption(id);
        log.info("✅ [CHECK] 옵션 삭제 완료: optionId={}", id);
    }

    /**
     * 옵션 정렬 순서 일괄 변경
     */
    @Transactional
    public void updateOptionSortOrders(List<Long> optionIds) {
        for (int i = 0; i < optionIds.size(); i++) {
            menuMapper.updateOptionSortOrder(optionIds.get(i), i + 1);
        }
        log.info("✅ [CHECK] 옵션 정렬 순서 일괄 변경 완료");
    }
}
