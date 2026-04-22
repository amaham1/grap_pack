package co.grap.pack.grap.user.content.controller;

import co.grap.pack.grap.user.content.service.CmsUserRealEstateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 사용자 부동산 실거래 컨트롤러.
 */
@Controller
@RequestMapping("/grap/user/content/real-estate")
@RequiredArgsConstructor
public class CmsUserRealEstateController {

    private final CmsUserRealEstateService realEstateService;

    @GetMapping
    public String realEstateList(@RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "dealYearMonth", required = false) String dealYearMonth,
                                 @RequestParam(value = "sort", required = false) String sort,
                                 @RequestParam(value = "page", required = false) Integer page,
                                 Model model) {
        Map<String, Object> result = realEstateService.getRealEstateList(keyword, dealYearMonth, sort, page);
        model.addAllAttributes(result);
        model.addAttribute("content", "grap/user/content/cms-real-estate-list");
        return "grap/user/layout/cms-user-layout";
    }

    @GetMapping("/{id}")
    public String realEstateDetail(@PathVariable("id") Long id,
                                   @RequestParam(value = "year", required = false) Integer year,
                                   Model model) {
        Map<String, Object> result = realEstateService.getRealEstateDetail(id, year);
        if (result == null) {
            return "redirect:/grap/user/content/real-estate";
        }

        model.addAllAttributes(result);
        model.addAttribute("content", "grap/user/content/cms-real-estate-detail");
        return "grap/user/layout/cms-user-layout";
    }
}
