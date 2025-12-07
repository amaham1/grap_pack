package co.grap.pack.qrmanage.shopadmin.qrcode.controller;

import co.grap.pack.qrmanage.shopadmin.qrcode.model.QrManageQrCode;
import co.grap.pack.qrmanage.shopadmin.qrcode.service.QrManageQrCodeService;
import co.grap.pack.qrmanage.shopadmin.shop.service.QrManageShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * QR 코드 관리 컨트롤러 (상점관리자용)
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/shop/admin/qrcode")
@RequiredArgsConstructor
public class QrManageQrCodeController {

    private final QrManageQrCodeService qrCodeService;
    private final QrManageShopService shopService;

    /**
     * QR 코드 관리 페이지
     */
    @GetMapping({"", "/list"})
    public String list(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long shopId = getShopId(userDetails);
        if (shopId == null) {
            return "redirect:/qr-manage/shop/admin/info?needShop=true";
        }

        List<QrManageQrCode> qrCodes = qrCodeService.getQrCodes(shopId);
        model.addAttribute("qrCodes", qrCodes);
        return "qrmanage/shop/qrcode/qr-manage-qrcode-list";
    }

    /**
     * 상점 대표 QR 코드 생성
     */
    @PostMapping("/create/shop")
    public String createShopQrCode(@AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null) {
            return "redirect:/qr-manage/shop/admin/info?needShop=true";
        }

        try {
            qrCodeService.createShopQrCode(shopId);
            redirectAttributes.addFlashAttribute("message", "상점 대표 QR 코드가 생성되었습니다.");
        } catch (Exception e) {
            log.error("❌ [ERROR] 상점 QR 코드 생성 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "QR 코드 생성에 실패했습니다.");
        }

        return "redirect:/qr-manage/shop/admin/qrcode";
    }

    /**
     * 메뉴 QR 코드 생성
     */
    @PostMapping("/create/menu")
    public String createMenuQrCode(@AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null) {
            return "redirect:/qr-manage/shop/admin/info?needShop=true";
        }

        try {
            qrCodeService.createMenuQrCode(shopId);
            redirectAttributes.addFlashAttribute("message", "메뉴 QR 코드가 생성되었습니다.");
        } catch (Exception e) {
            log.error("❌ [ERROR] 메뉴 QR 코드 생성 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "QR 코드 생성에 실패했습니다.");
        }

        return "redirect:/qr-manage/shop/admin/qrcode";
    }

    /**
     * QR 코드 재생성
     */
    @PostMapping("/regenerate/{id}")
    public String regenerateQrCode(@PathVariable("id") Long id,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null || !qrCodeService.isOwnedByShop(id, shopId)) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/qr-manage/shop/admin/qrcode";
        }

        try {
            qrCodeService.regenerateQrCode(id);
            redirectAttributes.addFlashAttribute("message", "QR 코드가 재생성되었습니다.");
        } catch (Exception e) {
            log.error("❌ [ERROR] QR 코드 재생성 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "QR 코드 재생성에 실패했습니다.");
        }

        return "redirect:/qr-manage/shop/admin/qrcode";
    }

    /**
     * QR 코드 활성화/비활성화
     */
    @PostMapping("/toggle/{id}")
    public String toggleActive(@PathVariable("id") Long id,
                               @RequestParam("isActive") Boolean isActive,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null || !qrCodeService.isOwnedByShop(id, shopId)) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/qr-manage/shop/admin/qrcode";
        }

        qrCodeService.updateActive(id, isActive);
        String status = isActive ? "활성화" : "비활성화";
        redirectAttributes.addFlashAttribute("message", "QR 코드가 " + status + "되었습니다.");

        return "redirect:/qr-manage/shop/admin/qrcode";
    }

    /**
     * QR 코드 만료일 설정
     */
    @PostMapping("/expires/{id}")
    public String setExpiresAt(@PathVariable("id") Long id,
                               @RequestParam(value = "expiresAt", required = false) String expiresAtStr,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        Long shopId = getShopId(userDetails);
        if (shopId == null || !qrCodeService.isOwnedByShop(id, shopId)) {
            redirectAttributes.addFlashAttribute("error", "접근 권한이 없습니다.");
            return "redirect:/qr-manage/shop/admin/qrcode";
        }

        LocalDateTime expiresAt = null;
        if (expiresAtStr != null && !expiresAtStr.isEmpty()) {
            expiresAt = LocalDateTime.parse(expiresAtStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        }

        qrCodeService.updateExpiresAt(id, expiresAt);
        redirectAttributes.addFlashAttribute("message", "만료일이 설정되었습니다.");

        return "redirect:/qr-manage/shop/admin/qrcode";
    }

    /**
     * QR 코드 다운로드
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadQrCode(@PathVariable("id") Long id,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        Long shopId = getShopId(userDetails);
        if (shopId == null || !qrCodeService.isOwnedByShop(id, shopId)) {
            return ResponseEntity.notFound().build();
        }

        QrManageQrCode qrCode = qrCodeService.getQrCode(id);
        if (qrCode == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path filePath = Paths.get(qrCode.getImagePath().substring(1)); // 앞의 '/' 제거
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String fileName = qrCode.getQrType().name().toLowerCase() + "_qr_" + qrCode.getShopId() + ".png";
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                        .body(resource);
            }
        } catch (MalformedURLException e) {
            log.error("❌ [ERROR] QR 코드 다운로드 실패: {}", e.getMessage());
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * 현재 로그인한 상점관리자의 상점 ID 조회
     */
    private Long getShopId(UserDetails userDetails) {
        return shopService.getShopIdByAdminUsername(userDetails.getUsername());
    }
}
