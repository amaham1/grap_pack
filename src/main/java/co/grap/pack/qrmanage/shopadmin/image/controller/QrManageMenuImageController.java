package co.grap.pack.qrmanage.shopadmin.image.controller;

import co.grap.pack.qrmanage.shopadmin.image.model.QrManageMenuImage;
import co.grap.pack.qrmanage.shopadmin.image.service.QrManageMenuImageService;
import co.grap.pack.qrmanage.shopadmin.menu.model.QrManageMenu;
import co.grap.pack.qrmanage.shopadmin.menu.service.QrManageMenuService;
import co.grap.pack.qrmanage.shopadmin.shop.service.QrManageShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 메뉴 이미지 관리 컨트롤러 (상점관리자용)
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/shop/admin/menu/{menuId}/image")
@RequiredArgsConstructor
public class QrManageMenuImageController {

    private final QrManageMenuImageService imageService;
    private final QrManageMenuService menuService;
    private final QrManageShopService shopService;

    /**
     * 이미지 관리 페이지
     */
    @GetMapping
    public String imageList(@PathVariable("menuId") Long menuId,
                            @AuthenticationPrincipal UserDetails userDetails,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null) {
            return "redirect:/qr-manage/shop/admin/info";
        }

        if (!menuService.isOwnedByShop(menuId, shopId)) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/qr-manage/shop/admin/menu/list";
        }

        QrManageMenu menu = menuService.getMenu(menuId);
        List<QrManageMenuImage> images = imageService.getImages(menuId);

        model.addAttribute("menu", menu);
        model.addAttribute("images", images);
        return "qrmanage/shop/menu/qr-manage-menu-image";
    }

    /**
     * 이미지 업로드
     */
    @PostMapping("/upload")
    public String uploadImage(@PathVariable("menuId") Long menuId,
                              @RequestParam("file") MultipartFile file,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null || !menuService.isOwnedByShop(menuId, shopId)) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/qr-manage/shop/admin/menu/list";
        }

        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "파일을 선택해주세요.");
                return "redirect:/qr-manage/shop/admin/menu/" + menuId + "/image";
            }

            imageService.uploadImage(menuId, file);
            redirectAttributes.addFlashAttribute("message", "이미지가 업로드되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            log.error("❌ [ERROR] 이미지 업로드 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "이미지 업로드에 실패했습니다.");
        }

        return "redirect:/qr-manage/shop/admin/menu/" + menuId + "/image";
    }

    /**
     * 이미지 삭제
     */
    @PostMapping("/delete/{imageId}")
    public String deleteImage(@PathVariable("menuId") Long menuId,
                              @PathVariable("imageId") Long imageId,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null || !menuService.isOwnedByShop(menuId, shopId)) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/qr-manage/shop/admin/menu/list";
        }

        try {
            imageService.deleteImage(imageId, menuId);
            redirectAttributes.addFlashAttribute("message", "이미지가 삭제되었습니다.");
        } catch (Exception e) {
            log.error("❌ [ERROR] 이미지 삭제 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "이미지 삭제에 실패했습니다.");
        }

        return "redirect:/qr-manage/shop/admin/menu/" + menuId + "/image";
    }

    /**
     * 대표 이미지 설정
     */
    @PostMapping("/primary/{imageId}")
    public String setPrimaryImage(@PathVariable("menuId") Long menuId,
                                  @PathVariable("imageId") Long imageId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null || !menuService.isOwnedByShop(menuId, shopId)) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/qr-manage/shop/admin/menu/list";
        }

        try {
            imageService.setPrimaryImage(menuId, imageId);
            redirectAttributes.addFlashAttribute("message", "대표 이미지가 설정되었습니다.");
        } catch (Exception e) {
            log.error("❌ [ERROR] 대표 이미지 설정 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "대표 이미지 설정에 실패했습니다.");
        }

        return "redirect:/qr-manage/shop/admin/menu/" + menuId + "/image";
    }

    /**
     * 이미지 순서 변경 (AJAX)
     */
    @PostMapping("/sort")
    @ResponseBody
    public String updateSortOrder(@PathVariable("menuId") Long menuId,
                                  @RequestBody List<Long> imageIds,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Long shopId = getShopId(userDetails);
        if (shopId == null || !menuService.isOwnedByShop(menuId, shopId)) {
            return "error";
        }

        imageService.updateSortOrders(imageIds);
        return "success";
    }

    /**
     * 현재 로그인한 상점관리자의 상점 ID 조회
     */
    private Long getShopId(UserDetails userDetails) {
        return shopService.getShopIdByAdminEmail(userDetails.getUsername());
    }
}
