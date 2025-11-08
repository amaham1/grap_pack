package com.example.cms.admin.sync.controller;

import com.example.cms.external.api.service.JejuExhibitionApiService;
import com.example.cms.external.api.service.JejuFestivalApiService;
import com.example.cms.external.api.service.JejuGasPriceApiService;
import com.example.cms.external.api.service.JejuWelfareApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 관리자 - 외부 API 수동 동기화 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/admin/sync")
@RequiredArgsConstructor
public class AdminExternalApiSyncController {

    private final JejuFestivalApiService festivalService;
    private final JejuExhibitionApiService exhibitionService;
    private final JejuWelfareApiService welfareService;
    private final JejuGasPriceApiService gasPriceService;

    /**
     * 동기화 관리 페이지
     */
    @GetMapping
    public String syncPage(Model model) {
        model.addAttribute("title", "외부 API 동기화");
        model.addAttribute("content", "admin/sync/sync-page");
        return "admin/layout/admin-layout";
    }

    /**
     * 축제/행사 수동 동기화
     */
    @PostMapping("/festivals")
    public String syncFestivals(RedirectAttributes redirectAttributes) {
        try {
            log.info("관리자 요청: 축제/행사 데이터 수동 동기화 시작");
            festivalService.syncFestivalsFromExternalApi();
            redirectAttributes.addFlashAttribute("message", "축제/행사 데이터 동기화가 완료되었습니다.");
        } catch (Exception e) {
            log.error("축제/행사 데이터 동기화 실패", e);
            redirectAttributes.addFlashAttribute("error", "축제/행사 데이터 동기화 실패: " + e.getMessage());
        }
        return "redirect:/admin/sync";
    }

    /**
     * 공연/전시 수동 동기화
     */
    @PostMapping("/exhibitions")
    public String syncExhibitions(RedirectAttributes redirectAttributes) {
        try {
            log.info("관리자 요청: 공연/전시 데이터 수동 동기화 시작");
            exhibitionService.syncExhibitionsFromExternalApi();
            redirectAttributes.addFlashAttribute("message", "공연/전시 데이터 동기화가 완료되었습니다.");
        } catch (Exception e) {
            log.error("공연/전시 데이터 동기화 실패", e);
            redirectAttributes.addFlashAttribute("error", "공연/전시 데이터 동기화 실패: " + e.getMessage());
        }
        return "redirect:/admin/sync";
    }

    /**
     * 복지서비스 수동 동기화
     */
    @PostMapping("/welfare-services")
    public String syncWelfareServices(RedirectAttributes redirectAttributes) {
        try {
            log.info("관리자 요청: 복지서비스 데이터 수동 동기화 시작");
            welfareService.syncWelfareServicesFromExternalApi();
            redirectAttributes.addFlashAttribute("message", "복지서비스 데이터 동기화가 완료되었습니다.");
        } catch (Exception e) {
            log.error("복지서비스 데이터 동기화 실패", e);
            redirectAttributes.addFlashAttribute("error", "복지서비스 데이터 동기화 실패: " + e.getMessage());
        }
        return "redirect:/admin/sync";
    }

    /**
     * 주유소 가격 수동 동기화
     */
    @PostMapping("/gas-prices")
    public String syncGasPrices(RedirectAttributes redirectAttributes) {
        try {
            log.info("관리자 요청: 주유소 가격 데이터 수동 동기화 시작");
            gasPriceService.syncGasPricesFromExternalApi();
            redirectAttributes.addFlashAttribute("message", "주유소 가격 데이터 동기화가 완료되었습니다.");
        } catch (Exception e) {
            log.error("주유소 가격 데이터 동기화 실패", e);
            redirectAttributes.addFlashAttribute("error", "주유소 가격 데이터 동기화 실패: " + e.getMessage());
        }
        return "redirect:/admin/sync";
    }

    /**
     * 전체 데이터 수동 동기화
     */
    @PostMapping("/all")
    public String syncAll(RedirectAttributes redirectAttributes) {
        try {
            log.info("관리자 요청: 전체 데이터 수동 동기화 시작");

            // 순차적으로 모든 서비스 동기화
            festivalService.syncFestivalsFromExternalApi();
            exhibitionService.syncExhibitionsFromExternalApi();
            welfareService.syncWelfareServicesFromExternalApi();
            gasPriceService.syncGasPricesFromExternalApi();

            redirectAttributes.addFlashAttribute("message", "전체 데이터 동기화가 완료되었습니다.");
        } catch (Exception e) {
            log.error("전체 데이터 동기화 실패", e);
            redirectAttributes.addFlashAttribute("error", "전체 데이터 동기화 실패: " + e.getMessage());
        }
        return "redirect:/admin/sync";
    }
}
