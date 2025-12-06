package co.grap.pack.user.content.controller;

import co.grap.pack.user.content.service.UserGasStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 사용자 주유소 컨트롤러
 */
@Controller
@RequestMapping("/grap/user/content/gas-stations")
@RequiredArgsConstructor
public class UserGasStationController {

    private final UserGasStationService gasStationService;

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
        model.addAttribute("content", "user/content/gas-station-list");
        return "user/layout/user-layout";
    }

    /**
     * 주유소 상세 페이지
     */
    @GetMapping("/{id}")
    public String gasStationDetail(@PathVariable("id") Long id, Model model) {
        Map<String, Object> gasStation = gasStationService.getGasStationDetail(id);

        if (gasStation == null) {
            return "redirect:/grap/user/content/gas-stations";
        }

        model.addAttribute("gasStation", gasStation);
        model.addAttribute("content", "user/content/gas-station-detail");
        return "user/layout/user-layout";
    }
}
