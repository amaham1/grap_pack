package co.grap.pack.qrmanage.customer.controller;

import co.grap.pack.qrmanage.customer.service.QrManageCustomerService;
import co.grap.pack.qrmanage.shopadmin.category.model.QrManageCategory;
import co.grap.pack.qrmanage.shopadmin.menu.model.QrManageMenu;
import co.grap.pack.qrmanage.shopadmin.qrcode.model.QrManageQrCode;
import co.grap.pack.qrmanage.shopadmin.qrcode.service.QrManageQrCodeService;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageBusinessHours;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShop;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * 고객용 뷰 컨트롤러 (QR 코드 스캔 시 접근)
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/view")
@RequiredArgsConstructor
public class QrManageCustomerViewController {

    private final QrManageCustomerService customerService;
    private final QrManageQrCodeService qrCodeService;

    /**
     * 상점 정보 페이지 (상점 대표 QR 스캔 시)
     */
    @GetMapping("/shop/{qrCode}")
    public String shopInfo(@PathVariable("qrCode") String qrCode,
                           HttpServletRequest request,
                           Model model) {
        // QR 코드 유효성 검증
        QrManageQrCode qr = qrCodeService.getByQrCode(qrCode);
        if (qr == null || !qr.getIsActive() || qr.isExpired()) {
            return "qrmanage/customer/qr-manage-invalid-qr";
        }

        // 스캔 로그 기록
        customerService.logQrScan(qr.getId(), request);

        // 상점 정보 조회
        QrManageShop shop = customerService.getShopInfo(qr.getShopId());
        if (shop == null || !shop.getIsVisible()) {
            return "qrmanage/customer/qr-manage-not-found";
        }

        // 영업시간 조회
        List<QrManageBusinessHours> businessHours = customerService.getBusinessHours(shop.getId());

        model.addAttribute("shop", shop);
        model.addAttribute("businessHours", businessHours);
        return "qrmanage/customer/qr-manage-shop-info";
    }

    /**
     * 메뉴 목록 페이지 (메뉴 QR 스캔 시)
     */
    @GetMapping("/menu/{qrCode}")
    public String menuList(@PathVariable("qrCode") String qrCode,
                           HttpServletRequest request,
                           Model model) {
        // QR 코드 유효성 검증
        QrManageQrCode qr = qrCodeService.getByQrCode(qrCode);
        if (qr == null || !qr.getIsActive() || qr.isExpired()) {
            return "qrmanage/customer/qr-manage-invalid-qr";
        }

        // 스캔 로그 기록
        customerService.logQrScan(qr.getId(), request);

        // 상점 정보 조회
        QrManageShop shop = customerService.getShopInfo(qr.getShopId());
        if (shop == null || !shop.getIsVisible()) {
            return "qrmanage/customer/qr-manage-not-found";
        }

        // 카테고리별 메뉴 조회
        Map<QrManageCategory, List<QrManageMenu>> menusByCategory = customerService.getMenusByCategory(shop.getId());

        model.addAttribute("shop", shop);
        model.addAttribute("menusByCategory", menusByCategory);
        return "qrmanage/customer/qr-manage-menu-list";
    }

    /**
     * 메뉴 상세 페이지
     */
    @GetMapping("/menu/{qrCode}/{menuId}")
    public String menuDetail(@PathVariable("qrCode") String qrCode,
                             @PathVariable("menuId") Long menuId,
                             Model model) {
        // QR 코드 유효성 검증
        QrManageQrCode qr = qrCodeService.getByQrCode(qrCode);
        if (qr == null || !qr.getIsActive() || qr.isExpired()) {
            return "qrmanage/customer/qr-manage-invalid-qr";
        }

        // 메뉴 상세 조회
        QrManageMenu menu = customerService.getMenuDetail(menuId);
        if (menu == null || !menu.getIsVisible()) {
            return "qrmanage/customer/qr-manage-not-found";
        }

        // 상점 정보 조회
        QrManageShop shop = customerService.getShopInfo(qr.getShopId());

        model.addAttribute("shop", shop);
        model.addAttribute("menu", menu);
        model.addAttribute("qrCode", qrCode);
        return "qrmanage/customer/qr-manage-menu-detail";
    }
}
