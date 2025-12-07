package co.grap.pack.qrmanage.superadmin.shopadmin.controller;

import co.grap.pack.qrmanage.shopadmin.auth.model.QrManageShopAdmin;
import co.grap.pack.qrmanage.shopadmin.auth.model.QrManageShopAdminStatus;
import co.grap.pack.qrmanage.superadmin.shopadmin.model.QrManageShopAdminSearchParam;
import co.grap.pack.qrmanage.superadmin.shopadmin.service.QrManageSuperShopAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 최고관리자용 상점관리자 관리 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/super/admin/shop-admin")
@RequiredArgsConstructor
public class QrManageSuperShopAdminController {

    private final QrManageSuperShopAdminService shopAdminService;

    /**
     * 상점관리자 목록 페이지
     */
    @GetMapping("/list")
    public String list(@RequestParam(value = "status", required = false) QrManageShopAdminStatus status,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "page", defaultValue = "1") int page,
                       Model model) {
        log.info("✅ [CHECK] 상점관리자 목록 조회: status={}, keyword={}, page={}", status, keyword, page);

        QrManageShopAdminSearchParam param = QrManageShopAdminSearchParam.builder()
                .status(status)
                .keyword(keyword)
                .page(page)
                .size(20)
                .build();

        List<QrManageShopAdmin> shopAdmins = shopAdminService.findAll(param);
        int totalCount = shopAdminService.countAll(param);
        int totalPages = (int) Math.ceil((double) totalCount / param.getSize());

        model.addAttribute("title", "상점관리자 관리");
        model.addAttribute("shopAdmins", shopAdmins);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("status", status);
        model.addAttribute("keyword", keyword);
        model.addAttribute("statuses", QrManageShopAdminStatus.values());

        return "qrmanage/super/shopadmin/qr-manage-super-shopadmin-list";
    }

    /**
     * 상점관리자 상세 페이지
     */
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        log.info("✅ [CHECK] 상점관리자 상세 조회: id={}", id);

        QrManageShopAdmin shopAdmin = shopAdminService.findById(id);
        if (shopAdmin == null) {
            return "redirect:/qr-manage/super/admin/shop-admin/list";
        }

        model.addAttribute("title", "상점관리자 상세");
        model.addAttribute("shopAdmin", shopAdmin);

        return "qrmanage/super/shopadmin/qr-manage-super-shopadmin-detail";
    }

    /**
     * 상점관리자 생성 페이지
     */
    @GetMapping("/create")
    public String createForm(Model model) {
        log.info("✅ [CHECK] 상점관리자 생성 폼 요청");

        model.addAttribute("title", "상점관리자 등록");
        model.addAttribute("shopAdmin", new QrManageShopAdmin());

        return "qrmanage/super/shopadmin/qr-manage-super-shopadmin-form";
    }

    /**
     * 상점관리자 생성 처리
     */
    @PostMapping("/create")
    public String create(@ModelAttribute QrManageShopAdmin shopAdmin,
                        RedirectAttributes redirectAttributes) {
        log.info("✅ [CHECK] 상점관리자 생성: {}", shopAdmin.getUsername());

        try {
            shopAdminService.create(shopAdmin);
            redirectAttributes.addFlashAttribute("message", "상점관리자가 등록되었습니다.");
            return "redirect:/qr-manage/super/admin/shop-admin/list";
        } catch (Exception e) {
            log.error("❌ [ERROR] 상점관리자 생성 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "상점관리자 등록에 실패했습니다: " + e.getMessage());
            return "redirect:/qr-manage/super/admin/shop-admin/create";
        }
    }

    /**
     * 상점관리자 승인
     */
    @PostMapping("/approve/{id}")
    public String approve(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        log.info("✅ [CHECK] 상점관리자 승인: id={}", id);

        shopAdminService.approve(id);
        redirectAttributes.addFlashAttribute("message", "상점관리자가 승인되었습니다.");

        return "redirect:/qr-manage/super/admin/shop-admin/detail/" + id;
    }

    /**
     * 상점관리자 정지
     */
    @PostMapping("/suspend/{id}")
    public String suspend(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        log.info("✅ [CHECK] 상점관리자 정지: id={}", id);

        shopAdminService.suspend(id);
        redirectAttributes.addFlashAttribute("message", "상점관리자가 정지되었습니다.");

        return "redirect:/qr-manage/super/admin/shop-admin/detail/" + id;
    }

    /**
     * 상점관리자 활성화
     */
    @PostMapping("/activate/{id}")
    public String activate(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        log.info("✅ [CHECK] 상점관리자 활성화: id={}", id);

        shopAdminService.activate(id);
        redirectAttributes.addFlashAttribute("message", "상점관리자가 활성화되었습니다.");

        return "redirect:/qr-manage/super/admin/shop-admin/detail/" + id;
    }
}
