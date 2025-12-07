package co.grap.pack.qrmanage.superadmin.shop.controller;

import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShop;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShopStatus;
import co.grap.pack.qrmanage.superadmin.auth.model.QrManageSuperAdmin;
import co.grap.pack.qrmanage.superadmin.auth.service.QrManageSuperAuthService;
import co.grap.pack.qrmanage.superadmin.shop.model.QrManageShopMemo;
import co.grap.pack.qrmanage.superadmin.shop.model.QrManageShopReviewHistory;
import co.grap.pack.qrmanage.superadmin.shop.model.QrManageShopSearchParam;
import co.grap.pack.qrmanage.superadmin.shop.service.QrManageSuperShopService;
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
 * 최고관리자용 상점 관리 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/super/admin/shop")
@RequiredArgsConstructor
public class QrManageSuperShopController {

    private final QrManageSuperShopService shopService;
    private final QrManageSuperAuthService superAuthService;

    /**
     * 상점 목록 페이지
     */
    @GetMapping("/list")
    public String list(@RequestParam(value = "status", required = false) QrManageShopStatus status,
                       @RequestParam(value = "isVisible", required = false) Boolean isVisible,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "page", defaultValue = "1") int page,
                       Model model) {
        log.info("✅ [CHECK] 상점 목록 조회: status={}, isVisible={}, keyword={}", status, isVisible, keyword);

        QrManageShopSearchParam param = QrManageShopSearchParam.builder()
                .status(status)
                .isVisible(isVisible)
                .keyword(keyword)
                .page(page)
                .size(20)
                .build();

        List<QrManageShop> shops = shopService.findAll(param);
        int totalCount = shopService.countAll(param);
        int totalPages = (int) Math.ceil((double) totalCount / param.getSize());

        model.addAttribute("title", "상점 관리");
        model.addAttribute("shops", shops);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("status", status);
        model.addAttribute("isVisible", isVisible);
        model.addAttribute("keyword", keyword);
        model.addAttribute("statuses", QrManageShopStatus.values());

        return "qrmanage/super/shop/qr-manage-super-shop-list";
    }

    /**
     * 상점 상세 페이지
     */
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        log.info("✅ [CHECK] 상점 상세 조회: id={}", id);

        QrManageShop shop = shopService.findById(id);
        if (shop == null) {
            return "redirect:/qr-manage/super/admin/shop/list";
        }

        List<QrManageShopReviewHistory> reviewHistory = shopService.findReviewHistory(id);
        List<QrManageShopMemo> memos = shopService.findMemos(id);

        model.addAttribute("title", "상점 상세");
        model.addAttribute("shop", shop);
        model.addAttribute("reviewHistory", reviewHistory);
        model.addAttribute("memos", memos);

        return "qrmanage/super/shop/qr-manage-super-shop-detail";
    }

    /**
     * 상점 승인
     */
    @PostMapping("/approve/{id}")
    public String approve(@PathVariable("id") Long id,
                         @RequestParam(value = "comment", required = false) String comment,
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes redirectAttributes) {
        log.info("✅ [CHECK] 상점 승인: id={}", id);

        QrManageSuperAdmin admin = superAuthService.findByUsername(userDetails.getUsername());
        shopService.approve(id, admin.getId(), comment);
        redirectAttributes.addFlashAttribute("message", "상점이 승인되었습니다.");

        return "redirect:/qr-manage/super/admin/shop/detail/" + id;
    }

    /**
     * 상점 반려
     */
    @PostMapping("/reject/{id}")
    public String reject(@PathVariable("id") Long id,
                        @RequestParam(value = "comment", required = false) String comment,
                        @AuthenticationPrincipal UserDetails userDetails,
                        RedirectAttributes redirectAttributes) {
        log.info("✅ [CHECK] 상점 반려: id={}", id);

        QrManageSuperAdmin admin = superAuthService.findByUsername(userDetails.getUsername());
        shopService.reject(id, admin.getId(), comment);
        redirectAttributes.addFlashAttribute("message", "상점이 반려되었습니다.");

        return "redirect:/qr-manage/super/admin/shop/detail/" + id;
    }

    /**
     * 상점 노출 설정
     */
    @PostMapping("/visibility/{id}")
    public String setVisibility(@PathVariable("id") Long id,
                               @RequestParam("isVisible") Boolean isVisible,
                               RedirectAttributes redirectAttributes) {
        log.info("✅ [CHECK] 상점 노출 설정: id={}, isVisible={}", id, isVisible);

        shopService.setVisibility(id, isVisible);
        redirectAttributes.addFlashAttribute("message", isVisible ? "상점이 노출되었습니다." : "상점이 비노출 처리되었습니다.");

        return "redirect:/qr-manage/super/admin/shop/detail/" + id;
    }

    /**
     * 메모 추가
     */
    @PostMapping("/memo/{shopId}")
    public String addMemo(@PathVariable("shopId") Long shopId,
                         @RequestParam("content") String content,
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes redirectAttributes) {
        log.info("✅ [CHECK] 상점 메모 추가: shopId={}", shopId);

        QrManageSuperAdmin admin = superAuthService.findByUsername(userDetails.getUsername());
        shopService.addMemo(shopId, admin.getId(), content);
        redirectAttributes.addFlashAttribute("message", "메모가 추가되었습니다.");

        return "redirect:/qr-manage/super/admin/shop/detail/" + shopId;
    }

    /**
     * 메모 삭제
     */
    @PostMapping("/memo/delete/{memoId}")
    public String deleteMemo(@PathVariable("memoId") Long memoId,
                            @RequestParam("shopId") Long shopId,
                            RedirectAttributes redirectAttributes) {
        log.info("✅ [CHECK] 상점 메모 삭제: memoId={}", memoId);

        shopService.deleteMemo(memoId);
        redirectAttributes.addFlashAttribute("message", "메모가 삭제되었습니다.");

        return "redirect:/qr-manage/super/admin/shop/detail/" + shopId;
    }
}
