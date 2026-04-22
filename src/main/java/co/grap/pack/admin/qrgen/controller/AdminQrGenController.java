package co.grap.pack.admin.qrgen.controller;

import co.grap.pack.admin.qrgen.service.AdminQrGenPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 통합 운영 포털 QRgen 컨트롤러다.
 */
@Controller
@RequestMapping("/admin/qrgen")
@RequiredArgsConstructor
public class AdminQrGenController {

    private final AdminQrGenPortalService adminQrGenPortalService;

    @GetMapping("/users")
    public String userList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model
    ) {
        model.addAttribute("title", "QRgen 회원");
        model.addAttribute("result", adminQrGenPortalService.getUserList(keyword, isActive, page, 20));
        model.addAttribute("keyword", keyword);
        model.addAttribute("isActive", isActive);
        return "admin/qrgen/admin-user-list";
    }

    @GetMapping("/users/{userId}")
    public String userDetail(@PathVariable("userId") Long userId, Model model) {
        model.addAttribute("title", "QRgen 회원 상세");
        model.addAttribute("detail", adminQrGenPortalService.getUserDetail(userId));
        return "admin/qrgen/admin-user-detail";
    }

    @GetMapping("/histories")
    public String historyList(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "contentType", required = false) String contentType,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model
    ) {
        model.addAttribute("title", "QRgen 생성 이력");
        model.addAttribute("result", adminQrGenPortalService.getHistoryList(userId, contentType, page, 20));
        model.addAttribute("userId", userId);
        model.addAttribute("contentType", contentType);
        model.addAttribute("contentTypes", adminQrGenPortalService.getContentTypes());
        return "admin/qrgen/admin-history-list";
    }

    @GetMapping("/histories/{historyId}")
    public String historyDetail(@PathVariable("historyId") Long historyId, Model model) {
        model.addAttribute("title", "QRgen 이력 상세");
        model.addAttribute("detail", adminQrGenPortalService.getHistoryDetail(historyId));
        return "admin/qrgen/admin-history-detail";
    }
}
