package co.grap.pack.qrmanage.shopadmin.menu.controller;

import co.grap.pack.qrmanage.shopadmin.category.model.QrManageCategory;
import co.grap.pack.qrmanage.shopadmin.category.service.QrManageCategoryService;
import co.grap.pack.qrmanage.shopadmin.menu.model.QrManageMenu;
import co.grap.pack.qrmanage.shopadmin.menu.model.QrManageMenuOptionGroup;
import co.grap.pack.qrmanage.shopadmin.menu.service.QrManageMenuService;
import co.grap.pack.qrmanage.shopadmin.shop.service.QrManageShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 메뉴 관리 컨트롤러 (상점관리자용)
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/shop/admin/menu")
@RequiredArgsConstructor
public class QrManageMenuController {

    private final QrManageMenuService menuService;
    private final QrManageCategoryService categoryService;
    private final QrManageShopService shopService;

    /**
     * 메뉴 목록 페이지
     */
    @GetMapping("/list")
    public String list(@RequestParam(value = "categoryId", required = false) Long categoryId,
                       @AuthenticationPrincipal UserDetails userDetails,
                       Model model) {
        Long shopId = getShopId(userDetails);
        if (shopId == null) {
            return "redirect:/qr-manage/shop/admin/info?needShop=true";
        }

        List<QrManageCategory> categories = categoryService.getCategories(shopId);
        List<QrManageMenu> menus;

        if (categoryId != null) {
            menus = menuService.getMenusByCategory(categoryId);
        } else {
            menus = menuService.getMenus(shopId);
        }

        model.addAttribute("categories", categories);
        model.addAttribute("menus", menus);
        model.addAttribute("selectedCategoryId", categoryId);
        return "qrmanage/shop/menu/qr-manage-menu-list";
    }

    /**
     * 메뉴 등록 폼
     */
    @GetMapping("/new")
    public String newForm(@RequestParam(value = "categoryId", required = false) Long categoryId,
                          @AuthenticationPrincipal UserDetails userDetails,
                          Model model) {
        Long shopId = getShopId(userDetails);
        if (shopId == null) {
            return "redirect:/qr-manage/shop/admin/info?needShop=true";
        }

        List<QrManageCategory> categories = categoryService.getCategories(shopId);
        if (categories.isEmpty()) {
            return "redirect:/qr-manage/shop/admin/category/list?error=noCategory";
        }

        model.addAttribute("categories", categories);
        model.addAttribute("menu", new QrManageMenu());
        model.addAttribute("selectedCategoryId", categoryId);
        return "qrmanage/shop/menu/qr-manage-menu-form";
    }

    /**
     * 메뉴 수정 폼
     */
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null) {
            return "redirect:/qr-manage/shop/admin/info?needShop=true";
        }

        if (!menuService.isOwnedByShop(id, shopId)) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/qr-manage/shop/admin/menu/list";
        }

        QrManageMenu menu = menuService.getMenu(id);
        List<QrManageCategory> categories = categoryService.getCategories(shopId);

        model.addAttribute("categories", categories);
        model.addAttribute("menu", menu);
        return "qrmanage/shop/menu/qr-manage-menu-form";
    }

    /**
     * 메뉴 저장 (등록/수정)
     */
    @PostMapping("/save")
    public String save(@RequestParam(value = "id", required = false) Long id,
                       @RequestParam("categoryId") Long categoryId,
                       @RequestParam("name") String name,
                       @RequestParam(value = "description", required = false) String description,
                       @RequestParam("price") Integer price,
                       @AuthenticationPrincipal UserDetails userDetails,
                       RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null) {
            return "redirect:/qr-manage/shop/admin/info?needShop=true";
        }

        try {
            if (id != null) {
                // 수정
                if (!menuService.isOwnedByShop(id, shopId)) {
                    redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
                    return "redirect:/qr-manage/shop/admin/menu/list";
                }
                menuService.updateMenu(id, categoryId, name, description, price);
                redirectAttributes.addFlashAttribute("message", "메뉴가 수정되었습니다.");
            } else {
                // 등록
                if (!categoryService.isOwnedByShop(categoryId, shopId)) {
                    redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
                    return "redirect:/qr-manage/shop/admin/menu/list";
                }
                menuService.createMenu(shopId, categoryId, name, description, price);
                redirectAttributes.addFlashAttribute("message", "메뉴가 등록되었습니다.");
            }
        } catch (Exception e) {
            log.error("❌ [ERROR] 메뉴 저장 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "메뉴 저장에 실패했습니다.");
        }

        return "redirect:/qr-manage/shop/admin/menu/list";
    }

    /**
     * 메뉴 삭제
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id,
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null) {
            return "redirect:/qr-manage/shop/admin/info?needShop=true";
        }

        if (!menuService.isOwnedByShop(id, shopId)) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/qr-manage/shop/admin/menu/list";
        }

        try {
            menuService.deleteMenu(id);
            redirectAttributes.addFlashAttribute("message", "메뉴가 삭제되었습니다.");
        } catch (Exception e) {
            log.error("❌ [ERROR] 메뉴 삭제 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "메뉴 삭제에 실패했습니다.");
        }

        return "redirect:/qr-manage/shop/admin/menu/list";
    }

    /**
     * 메뉴 공개/비공개 토글
     */
    @PostMapping("/visibility/{id}")
    public String toggleVisibility(@PathVariable("id") Long id,
                                   @RequestParam("isVisible") Boolean isVisible,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null) {
            return "redirect:/qr-manage/shop/admin/info?needShop=true";
        }

        if (!menuService.isOwnedByShop(id, shopId)) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/qr-manage/shop/admin/menu/list";
        }

        menuService.updateVisibility(id, isVisible);
        String status = isVisible ? "공개" : "비공개";
        redirectAttributes.addFlashAttribute("message", "메뉴가 " + status + "로 변경되었습니다.");

        return "redirect:/qr-manage/shop/admin/menu/list";
    }

    /**
     * 메뉴 품절/판매중 토글
     */
    @PostMapping("/soldout/{id}")
    public String toggleSoldOut(@PathVariable("id") Long id,
                                @RequestParam("isSoldOut") Boolean isSoldOut,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null) {
            return "redirect:/qr-manage/shop/admin/info?needShop=true";
        }

        if (!menuService.isOwnedByShop(id, shopId)) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/qr-manage/shop/admin/menu/list";
        }

        menuService.updateSoldOut(id, isSoldOut);
        String status = isSoldOut ? "품절" : "판매중";
        redirectAttributes.addFlashAttribute("message", "메뉴가 " + status + "으로 변경되었습니다.");

        return "redirect:/qr-manage/shop/admin/menu/list";
    }

    /**
     * 메뉴 정렬 순서 변경 (AJAX)
     */
    @PostMapping("/sort")
    @ResponseBody
    public String updateSortOrder(@RequestBody List<Long> menuIds,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Long shopId = getShopId(userDetails);
        if (shopId == null) {
            return "error";
        }

        menuService.updateSortOrders(menuIds);
        return "success";
    }

    // ========== 옵션 그룹 관리 ==========

    /**
     * 메뉴 옵션 관리 페이지
     */
    @GetMapping("/{menuId}/options")
    public String optionList(@PathVariable("menuId") Long menuId,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null) {
            return "redirect:/qr-manage/shop/admin/info?needShop=true";
        }

        if (!menuService.isOwnedByShop(menuId, shopId)) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/qr-manage/shop/admin/menu/list";
        }

        QrManageMenu menu = menuService.getMenu(menuId);
        List<QrManageMenuOptionGroup> optionGroups = menuService.getOptionGroups(menuId);

        model.addAttribute("menu", menu);
        model.addAttribute("optionGroups", optionGroups);
        return "qrmanage/shop/menu/qr-manage-menu-option";
    }

    /**
     * 옵션 그룹 등록
     */
    @PostMapping("/{menuId}/option-group/add")
    public String addOptionGroup(@PathVariable("menuId") Long menuId,
                                 @RequestParam("name") String name,
                                 @RequestParam(value = "isRequired", defaultValue = "false") Boolean isRequired,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null || !menuService.isOwnedByShop(menuId, shopId)) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/qr-manage/shop/admin/menu/list";
        }

        try {
            menuService.createOptionGroup(menuId, name, isRequired);
            redirectAttributes.addFlashAttribute("message", "옵션 그룹이 추가되었습니다.");
        } catch (Exception e) {
            log.error("❌ [ERROR] 옵션 그룹 추가 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "옵션 그룹 추가에 실패했습니다.");
        }

        return "redirect:/qr-manage/shop/admin/menu/" + menuId + "/options";
    }

    /**
     * 옵션 그룹 수정
     */
    @PostMapping("/{menuId}/option-group/update/{groupId}")
    public String updateOptionGroup(@PathVariable("menuId") Long menuId,
                                    @PathVariable("groupId") Long groupId,
                                    @RequestParam("name") String name,
                                    @RequestParam(value = "isRequired", defaultValue = "false") Boolean isRequired,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null || !menuService.isOwnedByShop(menuId, shopId)) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/qr-manage/shop/admin/menu/list";
        }

        try {
            menuService.updateOptionGroup(groupId, name, isRequired);
            redirectAttributes.addFlashAttribute("message", "옵션 그룹이 수정되었습니다.");
        } catch (Exception e) {
            log.error("❌ [ERROR] 옵션 그룹 수정 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "옵션 그룹 수정에 실패했습니다.");
        }

        return "redirect:/qr-manage/shop/admin/menu/" + menuId + "/options";
    }

    /**
     * 옵션 그룹 삭제
     */
    @PostMapping("/{menuId}/option-group/delete/{groupId}")
    public String deleteOptionGroup(@PathVariable("menuId") Long menuId,
                                    @PathVariable("groupId") Long groupId,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null || !menuService.isOwnedByShop(menuId, shopId)) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/qr-manage/shop/admin/menu/list";
        }

        try {
            menuService.deleteOptionGroup(groupId);
            redirectAttributes.addFlashAttribute("message", "옵션 그룹이 삭제되었습니다.");
        } catch (Exception e) {
            log.error("❌ [ERROR] 옵션 그룹 삭제 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "옵션 그룹 삭제에 실패했습니다.");
        }

        return "redirect:/qr-manage/shop/admin/menu/" + menuId + "/options";
    }

    // ========== 옵션 관리 ==========

    /**
     * 옵션 추가
     */
    @PostMapping("/{menuId}/option-group/{groupId}/option/add")
    public String addOption(@PathVariable("menuId") Long menuId,
                            @PathVariable("groupId") Long groupId,
                            @RequestParam("name") String name,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null || !menuService.isOwnedByShop(menuId, shopId)) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/qr-manage/shop/admin/menu/list";
        }

        try {
            menuService.createOption(groupId, name);
            redirectAttributes.addFlashAttribute("message", "옵션이 추가되었습니다.");
        } catch (Exception e) {
            log.error("❌ [ERROR] 옵션 추가 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "옵션 추가에 실패했습니다.");
        }

        return "redirect:/qr-manage/shop/admin/menu/" + menuId + "/options";
    }

    /**
     * 옵션 삭제
     */
    @PostMapping("/{menuId}/option/delete/{optionId}")
    public String deleteOption(@PathVariable("menuId") Long menuId,
                               @PathVariable("optionId") Long optionId,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null || !menuService.isOwnedByShop(menuId, shopId)) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/qr-manage/shop/admin/menu/list";
        }

        try {
            menuService.deleteOption(optionId);
            redirectAttributes.addFlashAttribute("message", "옵션이 삭제되었습니다.");
        } catch (Exception e) {
            log.error("❌ [ERROR] 옵션 삭제 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "옵션 삭제에 실패했습니다.");
        }

        return "redirect:/qr-manage/shop/admin/menu/" + menuId + "/options";
    }

    /**
     * 현재 로그인한 상점관리자의 상점 ID 조회
     */
    private Long getShopId(UserDetails userDetails) {
        return shopService.getShopIdByAdminUsername(userDetails.getUsername());
    }
}
