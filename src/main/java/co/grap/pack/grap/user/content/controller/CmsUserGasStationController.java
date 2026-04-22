package co.grap.pack.grap.user.content.controller;

import co.grap.pack.grap.seo.CmsPublicSeoService;
import co.grap.pack.grap.user.content.service.CmsUserGasStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 사용자 주유소 컨트롤러다.
 */
@Controller
@RequestMapping("/grap/user/content/gas-stations")
@RequiredArgsConstructor
public class CmsUserGasStationController {

    private final CmsUserGasStationService gasStationService;
    private final CmsPublicSeoService cmsPublicSeoService;

    /**
     * 주유소 목록 페이지를 보여준다.
     *
     * @param keyword 검색어
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param model 화면 모델
     * @return 레이아웃 템플릿
     */
    @GetMapping
    public String gasStationList(@RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "page", required = false) Integer page,
                                 @RequestParam(value = "size", required = false) Integer size,
                                 Model model) {
        Map<String, Object> result = gasStationService.getGasStationList(keyword, page, size);
        model.addAllAttributes(result);
        cmsPublicSeoService.applyGasStationListSeo(model, keyword, page);
        model.addAttribute("content", "grap/user/content/cms-gas-station-list");
        return "grap/user/layout/cms-user-layout";
    }

    /**
     * 주유소 상세 페이지를 보여준다.
     *
     * @param id 주유소 ID
     * @param model 화면 모델
     * @return 레이아웃 템플릿
     */
    @GetMapping("/{id}")
    public String gasStationDetail(@PathVariable("id") Long id, Model model) {
        Map<String, Object> gasStation = gasStationService.getGasStationDetail(id);
        if (gasStation == null) {
            return "redirect:/grap/user/content/gas-stations";
        }

        model.addAttribute("gasStation", gasStation);
        cmsPublicSeoService.applyGasStationDetailSeo(model, gasStation);
        model.addAttribute("content", "grap/user/content/cms-gas-station-detail");
        return "grap/user/layout/cms-user-layout";
    }
}
