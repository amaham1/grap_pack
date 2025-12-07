package co.grap.pack.qrmanage.shopadmin.category.controller;

import co.grap.pack.qrmanage.shopadmin.category.model.QrManageCategory;
import co.grap.pack.qrmanage.shopadmin.category.service.QrManageCategoryService;
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
 * 카테고리 관리 컨트롤러 (상점관리자용)
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/shop/admin/category")
@RequiredArgsConstructor
public class QrManageCategoryController {

    private final QrManageCategoryService categoryService;
    private final QrManageShopService shopService;

    /**
     * 카테고리 목록 페이지
     */
    @GetMapping("/list")
    public String list(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long shopId = getShopId(userDetails);
        if (shopId == null) {
            return "redirect:/qr-manage/shop/admin/info?needShop=true";
        }

        List<QrManageCategory> categories = categoryService.getCategories(shopId);
        model.addAttribute("categories", categories);
        return "qrmanage/shop/category/qr-manage-category-list";
    }

    /**
     * 카테고리 등록 폼
     */
    @GetMapping("/new")
    public String newForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long shopId = getShopId(userDetails);
        if (shopId == null) {
            return "redirect:/qr-manage/shop/admin/info?needShop=true";
        }

        model.addAttribute("category", new QrManageCategory());
        return "qrmanage/shop/category/qr-manage-category-form";
    }

    /**
     * 카테고리 수정 폼
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

        if (!categoryService.isOwnedByShop(id, shopId)) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/qr-manage/shop/admin/category/list";
        }

        QrManageCategory category = categoryService.getCategory(id);
        model.addAttribute("category", category);
        return "qrmanage/shop/category/qr-manage-category-form";
    }

    /**
     * 카테고리 저장 (등록/수정)
     */
    @PostMapping("/save")
    public String save(@RequestParam(value = "id", required = false) Long id,
                       @RequestParam("name") String name,
                       @RequestParam(value = "description", required = false) String description,
                       @AuthenticationPrincipal UserDetails userDetails,
                       RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null) {
            return "redirect:/qr-manage/shop/admin/info?needShop=true";
        }

        try {
            if (id != null) {
                // 수정
                if (!categoryService.isOwnedByShop(id, shopId)) {
                    redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
                    return "redirect:/qr-manage/shop/admin/category/list";
                }
                categoryService.updateCategory(id, name, description);
                redirectAttributes.addFlashAttribute("message", "카테고리가 수정되었습니다.");
            } else {
                // 등록
                categoryService.createCategory(shopId, name, description);
                redirectAttributes.addFlashAttribute("message", "카테고리가 등록되었습니다.");
            }
        } catch (Exception e) {
            log.error("❌ [ERROR] 카테고리 저장 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "카테고리 저장에 실패했습니다.");
        }

        return "redirect:/qr-manage/shop/admin/category/list";
    }

    /**
     * 카테고리 삭제
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id,
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null) {
            return "redirect:/qr-manage/shop/admin/info?needShop=true";
        }

        if (!categoryService.isOwnedByShop(id, shopId)) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/qr-manage/shop/admin/category/list";
        }

        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("message", "카테고리가 삭제되었습니다.");
        } catch (Exception e) {
            log.error("❌ [ERROR] 카테고리 삭제 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "카테고리 삭제에 실패했습니다. 해당 카테고리에 메뉴가 있는지 확인해주세요.");
        }

        return "redirect:/qr-manage/shop/admin/category/list";
    }

    /**
     * 카테고리 공개/비공개 토글
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

        if (!categoryService.isOwnedByShop(id, shopId)) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/qr-manage/shop/admin/category/list";
        }

        categoryService.updateVisibility(id, isVisible);
        String status = isVisible ? "공개" : "비공개";
        redirectAttributes.addFlashAttribute("message", "카테고리가 " + status + "로 변경되었습니다.");

        return "redirect:/qr-manage/shop/admin/category/list";
    }

    /**
     * 카테고리 정렬 순서 변경 (AJAX)
     */
    @PostMapping("/sort")
    @ResponseBody
    public String updateSortOrder(@RequestBody List<Long> categoryIds,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Long shopId = getShopId(userDetails);
        if (shopId == null) {
            return "error";
        }

        categoryService.updateSortOrders(categoryIds);
        return "success";
    }

    /**
     * 현재 로그인한 상점관리자의 상점 ID 조회
     */
    private Long getShopId(UserDetails userDetails) {
        return shopService.getShopIdByAdminUsername(userDetails.getUsername());
    }
}
