package co.grap.pack.grap.user.content.controller;

import co.grap.pack.grap.seo.CmsPublicSeoService;
import co.grap.pack.grap.user.content.model.CmsUserRealEstateSearchParam;
import co.grap.pack.grap.user.content.service.CmsUserRealEstateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 사용자 부동산 실거래가 컨트롤러다.
 */
@Controller
@RequestMapping("/grap/user/content/real-estate")
@RequiredArgsConstructor
public class CmsUserRealEstateController {

    private final CmsUserRealEstateService realEstateService;
    private final CmsPublicSeoService cmsPublicSeoService;

    /**
     * 부동산 목록 페이지를 보여준다.
     *
     * @param searchParam 검색 조건
     * @param model 화면 모델
     * @return 레이아웃 템플릿
     */
    @GetMapping
    public String realEstateList(@ModelAttribute CmsUserRealEstateSearchParam searchParam,
                                 Model model) {
        Map<String, Object> result = realEstateService.getRealEstateList(searchParam);
        model.addAllAttributes(result);
        cmsPublicSeoService.applyRealEstateListSeo(
                model,
                searchParam.getKeyword(),
                searchParam.getDealYearMonth(),
                searchParam.getSort(),
                searchParam.getPage(),
                (String) result.get("currentPropertyMonth")
        );
        model.addAttribute("content", "grap/user/content/cms-real-estate-list");
        return "grap/user/layout/cms-user-layout";
    }

    /**
     * 부동산 상세 페이지를 보여준다.
     *
     * @param id 거래 ID
     * @param year 조회 연도
     * @param model 화면 모델
     * @return 레이아웃 템플릿
     */
    @GetMapping("/{id}")
    public String realEstateDetail(@PathVariable("id") Long id,
                                   @RequestParam(value = "year", required = false) Integer year,
                                   Model model) {
        Map<String, Object> result = realEstateService.getRealEstateDetail(id, year);
        if (result == null) {
            return "redirect:/grap/user/content/real-estate";
        }

        model.addAllAttributes(result);
        cmsPublicSeoService.applyRealEstateDetailSeo(model, toPropertySeoMap(result.get("property")), year);
        model.addAttribute("content", "grap/user/content/cms-real-estate-detail");
        return "grap/user/layout/cms-user-layout";
    }

    private Map<String, Object> toPropertySeoMap(Object propertyValue) {
        Map<String, Object> propertyMap = new LinkedHashMap<>();
        if (propertyValue instanceof Map<?, ?> sourceMap) {
            for (Map.Entry<?, ?> entry : sourceMap.entrySet()) {
                propertyMap.put(String.valueOf(entry.getKey()), entry.getValue());
            }
        }
        return propertyMap;
    }
}
