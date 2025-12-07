package co.grap.pack.qrmanage.shopadmin.shop.controller;

import co.grap.pack.qrmanage.shopadmin.auth.model.QrManageShopAdmin;
import co.grap.pack.qrmanage.shopadmin.auth.service.QrManageShopAuthService;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageBusinessHours;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShop;
import co.grap.pack.qrmanage.shopadmin.shop.service.QrManageShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 상점 정보 관리 컨트롤러 (상점관리자용)
 */
@Slf4j
@Controller
@RequestMapping("/qr-manage/shop/admin/info")
@RequiredArgsConstructor
public class QrManageShopInfoController {

    private final QrManageShopService shopService;
    private final QrManageShopAuthService shopAuthService;

    /**
     * 상점 정보 페이지
     */
    @GetMapping
    public String info(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        log.info("✅ [CHECK] 상점 정보 페이지 요청: {}", userDetails.getUsername());

        QrManageShopAdmin shopAdmin = shopAuthService.findByUsername(userDetails.getUsername());
        QrManageShop shop = shopService.findByShopAdminId(shopAdmin.getId());
        List<QrManageBusinessHours> businessHours = new ArrayList<>();

        if (shop != null) {
            businessHours = shopService.findBusinessHours(shop.getId());
        } else {
            shop = new QrManageShop();
            shop.setShopAdminId(shopAdmin.getId());
        }

        // 영업시간이 없으면 기본값 설정
        if (businessHours.isEmpty()) {
            for (int i = 0; i < 7; i++) {
                QrManageBusinessHours hours = new QrManageBusinessHours();
                hours.setDayOfWeek(i);
                hours.setIsHoliday(false);
                hours.setOpenTime(LocalTime.of(9, 0));
                hours.setCloseTime(LocalTime.of(22, 0));
                businessHours.add(hours);
            }
        }

        model.addAttribute("shop", shop);
        model.addAttribute("businessHours", businessHours);

        return "qrmanage/shop/info/qr-manage-shop-info";
    }

    /**
     * 상점 정보 저장
     */
    @PostMapping
    public String save(@AuthenticationPrincipal UserDetails userDetails,
                      @ModelAttribute QrManageShop shop,
                      @RequestParam(value = "dayOfWeek", required = false) List<Integer> dayOfWeeks,
                      @RequestParam(value = "isHoliday", required = false) List<Boolean> isHolidays,
                      @RequestParam(value = "openTime", required = false) List<String> openTimes,
                      @RequestParam(value = "closeTime", required = false) List<String> closeTimes,
                      RedirectAttributes redirectAttributes) {
        log.info("✅ [CHECK] 상점 정보 저장 요청: {}", userDetails.getUsername());

        try {
            QrManageShopAdmin shopAdmin = shopAuthService.findByUsername(userDetails.getUsername());
            QrManageShop existingShop = shopService.findByShopAdminId(shopAdmin.getId());

            if (existingShop == null) {
                // 신규 등록
                shop.setShopAdminId(shopAdmin.getId());
                shop = shopService.create(shop);
            } else {
                // 수정
                shop.setId(existingShop.getId());
                shop.setShopAdminId(shopAdmin.getId());
                shop = shopService.update(shop);
            }

            // 영업시간 저장
            if (dayOfWeeks != null) {
                List<QrManageBusinessHours> businessHoursList = new ArrayList<>();
                for (int i = 0; i < dayOfWeeks.size(); i++) {
                    QrManageBusinessHours hours = new QrManageBusinessHours();
                    hours.setDayOfWeek(dayOfWeeks.get(i));
                    hours.setIsHoliday(isHolidays != null && i < isHolidays.size() ? isHolidays.get(i) : false);

                    if (openTimes != null && i < openTimes.size() && !openTimes.get(i).isEmpty()) {
                        hours.setOpenTime(LocalTime.parse(openTimes.get(i)));
                    }
                    if (closeTimes != null && i < closeTimes.size() && !closeTimes.get(i).isEmpty()) {
                        hours.setCloseTime(LocalTime.parse(closeTimes.get(i)));
                    }

                    businessHoursList.add(hours);
                }
                shopService.saveBusinessHours(shop.getId(), businessHoursList);
            }

            redirectAttributes.addFlashAttribute("message", "상점 정보가 저장되었습니다. 검수 후 노출됩니다.");
        } catch (Exception e) {
            log.error("❌ [ERROR] 상점 정보 저장 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "상점 정보 저장에 실패했습니다.");
        }

        return "redirect:/qr-manage/shop/admin/info";
    }
}
