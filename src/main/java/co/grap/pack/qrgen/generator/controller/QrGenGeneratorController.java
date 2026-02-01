package co.grap.pack.qrgen.generator.controller;

import co.grap.pack.qrgen.auth.model.QrGenUser;
import co.grap.pack.qrgen.auth.service.QrGenAuthService;
import co.grap.pack.qrgen.generator.model.QrGenContentType;
import co.grap.pack.qrgen.generator.model.QrGenRequest;
import co.grap.pack.qrgen.generator.service.QrGenGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * QR Generator 컨트롤러
 * 익명/로그인 사용자 모두 QR 생성 가능
 */
@Slf4j
@Controller
@RequestMapping("/qrgen")
@RequiredArgsConstructor
public class QrGenGeneratorController {

    private final QrGenGeneratorService generatorService;
    private final QrGenAuthService authService;

    /**
     * QR Generator 메인 페이지
     */
    @GetMapping({"", "/"})
    public String home(Model model) {
        model.addAttribute("contentTypes", QrGenContentType.values());
        model.addAttribute("isAuthenticated", isAuthenticated());
        return "qrgen/qrgen-home";
    }

    /**
     * QR 코드 생성 API (익명/로그인 사용자 모두 가능)
     */
    @PostMapping("/generate")
    @ResponseBody
    public ResponseEntity<byte[]> generateQrCode(@RequestBody QrGenRequest request) {
        try {
            log.info("✅ [CHECK] QR 생성 요청: type={}, value={}",
                    request.getContentType(),
                    request.getContentValue() != null ?
                        request.getContentValue().substring(0, Math.min(50, request.getContentValue().length())) : "null");

            // QR 코드 생성
            byte[] qrImage = generatorService.generateQrCode(request);

            // 로그인 사용자면 히스토리 저장
            if (isAuthenticated()) {
                QrGenUser user = getCurrentUser();
                if (user != null) {
                    // 파일 저장
                    String imagePath = generatorService.saveQrCodeToFile(qrImage, user.getQrGenUserId());
                    // 히스토리 저장
                    generatorService.saveHistory(user.getQrGenUserId(), request, imagePath);
                }
            }

            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"qrcode.png\"")
                .body(qrImage);

        } catch (Exception e) {
            log.error("❌ [ERROR] QR 코드 생성 실패: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * QR 코드 다운로드 API
     */
    @PostMapping("/download")
    @ResponseBody
    public ResponseEntity<byte[]> downloadQrCode(@RequestBody QrGenRequest request) {
        try {
            byte[] qrImage = generatorService.generateQrCode(request);

            String filename = "qrcode_" + System.currentTimeMillis() + ".png";

            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(qrImage);

        } catch (Exception e) {
            log.error("❌ [ERROR] QR 코드 다운로드 실패: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 인증 여부 확인
     */
    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null
            && auth.isAuthenticated()
            && !"anonymousUser".equals(auth.getPrincipal());
    }

    /**
     * 현재 로그인 사용자 조회
     */
    private QrGenUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return authService.findByUsername(auth.getName());
        }
        return null;
    }
}
