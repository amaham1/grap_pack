package co.grap.pack.qrmanage.customer.controller;

import co.grap.pack.qrmanage.customer.service.QrManageCustomerService;
import co.grap.pack.qrmanage.seo.QrManagePublicSeoService;
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
 * QR 고객용 뷰 컨트롤러다.
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/view")
@RequiredArgsConstructor
public class QrManageCustomerViewController {

    private final QrManageCustomerService customerService;
    private final QrManageQrCodeService qrCodeService;
    private final QrManagePublicSeoService qrManagePublicSeoService;

    /**
     * 상점 소개 페이지를 보여준다.
     *
     * @param qrCode QR 코드 값
     * @param request HTTP 요청
     * @param model 화면 모델
     * @return 템플릿 경로
     */
    @GetMapping("/shop/{qrCode}")
    public String shopInfo(@PathVariable("qrCode") String qrCode,
                           HttpServletRequest request,
                           Model model) {
        QrManageQrCode qr = qrCodeService.getByQrCode(qrCode);
        if (qr == null || !qr.getIsActive() || qr.isExpired()) {
            return "qrmanage/customer/qr-manage-invalid-qr";
        }

        customerService.logQrScan(qr.getId(), request);

        QrManageShop shop = customerService.getShopInfo(qr.getShopId());
        if (shop == null || !shop.getIsVisible()) {
            return "qrmanage/customer/qr-manage-not-found";
        }

        List<QrManageBusinessHours> businessHours = customerService.getBusinessHours(shop.getId());

        model.addAttribute("shop", shop);
        model.addAttribute("businessHours", businessHours);
        qrManagePublicSeoService.applyShopSeo(model, qrCode, shop);
        return "qrmanage/customer/qr-manage-shop-info";
    }

    /**
     * 메뉴 목록 페이지를 보여준다.
     *
     * @param qrCode QR 코드 값
     * @param request HTTP 요청
     * @param model 화면 모델
     * @return 템플릿 경로
     */
    @GetMapping("/menu/{qrCode}")
    public String menuList(@PathVariable("qrCode") String qrCode,
                           HttpServletRequest request,
                           Model model) {
        QrManageQrCode qr = qrCodeService.getByQrCode(qrCode);
        if (qr == null || !qr.getIsActive() || qr.isExpired()) {
            return "qrmanage/customer/qr-manage-invalid-qr";
        }

        customerService.logQrScan(qr.getId(), request);

        QrManageShop shop = customerService.getShopInfo(qr.getShopId());
        if (shop == null || !shop.getIsVisible()) {
            return "qrmanage/customer/qr-manage-not-found";
        }

        Map<QrManageCategory, List<QrManageMenu>> menusByCategory = customerService.getMenusByCategory(shop.getId());

        model.addAttribute("shop", shop);
        model.addAttribute("menusByCategory", menusByCategory);
        qrManagePublicSeoService.applyMenuListSeo(model, qrCode, shop);
        return "qrmanage/customer/qr-manage-menu-list";
    }

    /**
     * 메뉴 상세 페이지를 보여준다.
     *
     * @param qrCode QR 코드 값
     * @param menuId 메뉴 ID
     * @param model 화면 모델
     * @return 템플릿 경로
     */
    @GetMapping("/menu/{qrCode}/{menuId}")
    public String menuDetail(@PathVariable("qrCode") String qrCode,
                             @PathVariable("menuId") Long menuId,
                             Model model) {
        QrManageQrCode qr = qrCodeService.getByQrCode(qrCode);
        if (qr == null || !qr.getIsActive() || qr.isExpired()) {
            return "qrmanage/customer/qr-manage-invalid-qr";
        }

        QrManageMenu menu = customerService.getMenuDetail(menuId);
        if (menu == null || !menu.getIsVisible()) {
            return "qrmanage/customer/qr-manage-not-found";
        }

        QrManageShop shop = customerService.getShopInfo(qr.getShopId());

        model.addAttribute("shop", shop);
        model.addAttribute("menu", menu);
        model.addAttribute("qrCode", qrCode);
        qrManagePublicSeoService.applyMenuDetailSeo(model, qrCode, shop, menu);
        return "qrmanage/customer/qr-manage-menu-detail";
    }
}
