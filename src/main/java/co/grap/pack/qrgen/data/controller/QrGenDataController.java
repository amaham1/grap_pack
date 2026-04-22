package co.grap.pack.qrgen.data.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 旧 QRgen ?곗씠??酉곗뼱瑜??듯빀 ?댁쁺 ?ы꽭濡??대룞?쒗궎??媛濡?而⑦듃濡ㅻ윭
 */
@Slf4j
@Controller
@RequestMapping("/qrgen/data")
public class QrGenDataController {

    @GetMapping
    public String showQrGenData() {
        log.info("✅ [CHECK] QRgen data viewer redirected to /admin/login");
        return "redirect:/admin/login";
    }

    @PostMapping("/verify")
    public String verifyQrGenDataPassword() {
        log.info("✅ [CHECK] QRgen data password verification redirected to /admin/login");
        return "redirect:/admin/login";
    }
}
