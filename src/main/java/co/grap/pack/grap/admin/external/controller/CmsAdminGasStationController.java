package co.grap.pack.grap.admin.external.controller;

import co.grap.pack.grap.admin.external.service.CmsAdminGasStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

/**
 * 관리자 주유소 컨트롤러
 */
@Controller
@RequestMapping("/grap/admin/external/gas-stations")
@RequiredArgsConstructor
public class CmsAdminGasStationController {

    private final CmsAdminGasStationService gasStationService;

    /**
     * 주유소 목록 페이지
     */
    @GetMapping
    public String gasStationList(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "page", required = false) Integer page,
                                  @RequestParam(value = "size", required = false) Integer size,
                                  Model model) {
        Map<String, Object> result = gasStationService.getGasStationList(keyword, page, size);

        model.addAllAttributes(result);
        model.addAttribute("title", "주유소 관리");
        model.addAttribute("content", "grap/admin/external/cms-gas-station-list");
        return "grap/admin/layout/cms-admin-layout";
    }

    /**
     * 주유소 상세 페이지
     */
    @GetMapping("/{id}")
    public String gasStationDetail(@PathVariable("id") Long id, Model model) {
        Map<String, Object> gasStation = gasStationService.getGasStationDetail(id);

        if (gasStation == null) {
            return "redirect:/grap/admin/external/gas-stations";
        }

        model.addAttribute("gasStation", gasStation);
        model.addAttribute("title", "주유소 상세");
        model.addAttribute("content", "grap/admin/external/cms-gas-station-detail");
        return "grap/admin/layout/cms-admin-layout";
    }

    /**
     * 주유소 삭제
     */
    @PostMapping("/{id}/delete")
    public String deleteGasStation(@PathVariable("id") Long id,
                                     RedirectAttributes redirectAttributes) {
        try {
            gasStationService.deleteGasStation(id);
            redirectAttributes.addFlashAttribute("message", "주유소가 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "삭제 실패: " + e.getMessage());
        }
        return "redirect:/grap/admin/external/gas-stations";
    }
}
