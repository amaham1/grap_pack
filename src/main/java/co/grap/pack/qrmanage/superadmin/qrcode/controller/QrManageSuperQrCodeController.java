package co.grap.pack.qrmanage.superadmin.qrcode.controller;

import co.grap.pack.qrmanage.shopadmin.qrcode.model.QrManageQrCode;
import co.grap.pack.qrmanage.shopadmin.qrcode.service.QrManageQrCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * QR 코드 관리 컨트롤러 (최고관리자용)
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/super/admin/qrcode")
@RequiredArgsConstructor
public class QrManageSuperQrCodeController {

    private final QrManageQrCodeService qrCodeService;

    /**
     * QR 코드 목록 페이지
     */
    @GetMapping("/list")
    public String list(@RequestParam(value = "shopId", required = false) Long shopId,
                       @RequestParam(value = "qrType", required = false) String qrType,
                       @RequestParam(value = "isActive", required = false) Boolean isActive,
                       @RequestParam(value = "page", defaultValue = "1") int page,
                       Model model) {
        log.info("✅ [CHECK] QR 코드 목록 조회: shopId={}, qrType={}, isActive={}", shopId, qrType, isActive);

        int size = 20;
        List<QrManageQrCode> qrCodes = qrCodeService.getAllQrCodes(shopId, qrType, isActive, page, size);
        int totalCount = qrCodeService.countAllQrCodes(shopId, qrType, isActive);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        model.addAttribute("title", "QR 코드 관리");
        model.addAttribute("qrCodes", qrCodes);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("shopId", shopId);
        model.addAttribute("qrType", qrType);
        model.addAttribute("isActive", isActive);

        return "qrmanage/super/qrcode/qr-manage-super-qrcode-list";
    }

    /**
     * QR 코드 활성화/비활성화
     */
    @PostMapping("/toggle/{id}")
    public String toggleActive(@PathVariable("id") Long id,
                               @RequestParam("isActive") Boolean isActive,
                               RedirectAttributes redirectAttributes) {
        log.info("✅ [CHECK] QR 코드 상태 변경: id={}, isActive={}", id, isActive);

        qrCodeService.updateActive(id, isActive);
        String status = isActive ? "활성화" : "비활성화";
        redirectAttributes.addFlashAttribute("message", "QR 코드가 " + status + "되었습니다.");

        return "redirect:/qr-manage/super/admin/qrcode/list";
    }
}
