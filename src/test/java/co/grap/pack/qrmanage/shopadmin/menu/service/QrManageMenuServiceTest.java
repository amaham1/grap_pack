package co.grap.pack.qrmanage.shopadmin.menu.service;

import co.grap.pack.qrmanage.shopadmin.category.mapper.QrManageCategoryMapper;
import co.grap.pack.qrmanage.shopadmin.menu.mapper.QrManageMenuMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QrManageMenuServiceTest {

    @Mock
    private QrManageMenuMapper menuMapper;

    @Mock
    private QrManageCategoryMapper categoryMapper;

    private QrManageMenuService menuService;

    @BeforeEach
    void setUp() {
        menuService = new QrManageMenuService(menuMapper, categoryMapper);
    }

    @Test
    void updateMenuRejectsCategoryOwnedByAnotherShop() {
        when(menuMapper.existsByIdAndShopId(10L, 1L)).thenReturn(true);
        when(categoryMapper.existsByIdAndShopId(20L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> menuService.updateMenu(1L, 10L, 20L, "name", "desc", 1000))
                .isInstanceOf(SecurityException.class);

        verify(menuMapper, never()).update(any());
    }

    @Test
    void updateOptionGroupRejectsGroupOwnedByAnotherShop() {
        when(menuMapper.existsOptionGroupByIdAndMenuIdAndShopId(30L, 10L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> menuService.updateOptionGroup(1L, 10L, 30L, "size", true))
                .isInstanceOf(SecurityException.class);

        verify(menuMapper, never()).updateOptionGroupForShop(any(), any(), any(), any(), any());
    }

    @Test
    void deleteOptionRejectsOptionOwnedByAnotherShop() {
        when(menuMapper.existsOptionByIdAndMenuIdAndShopId(40L, 10L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> menuService.deleteOption(1L, 10L, 40L))
                .isInstanceOf(SecurityException.class);

        verify(menuMapper, never()).deleteOptionForShop(any(), any(), any());
    }

    @Test
    void updateSortOrdersRejectsIdsNotAllOwnedByShop() {
        List<Long> menuIds = List.of(10L, 11L);
        when(menuMapper.countByIdsAndShopId(menuIds, 1L)).thenReturn(1);

        assertThatThrownBy(() -> menuService.updateSortOrders(1L, menuIds))
                .isInstanceOf(SecurityException.class);

        verify(menuMapper, never()).updateSortOrder(any(), any(), any());
    }
}
