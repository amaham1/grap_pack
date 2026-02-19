package co.grap.pack.qrgen.generator.controller;

import co.grap.pack.qrgen.auth.model.QrGenUser;
import co.grap.pack.qrgen.auth.service.QrGenAuthService;
import co.grap.pack.qrgen.generator.model.QrGenContentType;
import co.grap.pack.qrgen.generator.model.QrGenRequest;
import co.grap.pack.qrgen.generator.service.QrGenGeneratorService;
import co.grap.pack.qrgen.generator.service.QrGenRateLimitService;
import co.grap.pack.qrgen.generator.service.QrGenRateLimitService.QrGenRateLimitCheckResult;
import co.grap.pack.qrgen.seo.QrGenSeoHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    private final QrGenRateLimitService rateLimitService;

    /**
     * QR Generator 메인 페이지
     */
    @GetMapping({"", "/"})
    public String home(Model model) {
        model.addAttribute("contentTypes", QrGenContentType.values());
        model.addAttribute("isAuthenticated", isAuthenticated());
        QrGenSeoHelper.setQrGenPublicPageSeo(model, "/qrgen/",
                "무료 QR 코드 생성기",
                "URL, 텍스트, 연락처, Wi-Fi 등 다양한 QR 코드를 무료로 간편하게 생성하세요. 회원가입 없이도 사용 가능합니다.");
        return "qrgen/qrgen-home";
    }

    /**
     * QR 코드 미리보기 API (히스토리 저장 없음, 분당 60회 제한)
     * 폼 입력값 변경 시 실시간 미리보기 제공
     */
    @GetMapping("/preview")
    @ResponseBody
    public ResponseEntity<byte[]> previewQrCode(
            @RequestParam("contentType") String contentType,
            @RequestParam("contentValue") String contentValue,
            @RequestParam(value = "size", defaultValue = "300") Integer size,
            @RequestParam(value = "errorCorrection", defaultValue = "M") String errorCorrection,
            @RequestParam(value = "foregroundColor", defaultValue = "#000000") String foregroundColor,
            @RequestParam(value = "backgroundColor", defaultValue = "#FFFFFF") String backgroundColor,
            HttpServletRequest httpRequest) {
        try {
            // 미리보기 Rate Limit 체크
            String ipAddress = rateLimitService.getClientIpAddress(httpRequest);
            if (rateLimitService.isQrGenPreviewRateLimitExceeded(ipAddress)) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
            }

            QrGenRequest request = QrGenRequest.builder()
                    .contentType(QrGenContentType.valueOf(contentType))
                    .contentValue(contentValue)
                    .size(size)
                    .errorCorrection(errorCorrection)
                    .foregroundColor(foregroundColor)
                    .backgroundColor(backgroundColor)
                    .build();

            byte[] qrImage = generatorService.generateQrCode(request);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=60")
                    .body(qrImage);
        } catch (Exception e) {
            log.warn("QR 미리보기 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * QR 코드 생성 API (익명/로그인 사용자 모두 가능)
     */
    @PostMapping("/generate")
    @ResponseBody
    public ResponseEntity<?> generateQrCode(@RequestBody QrGenRequest request,
                                            HttpServletRequest httpRequest) {
        try {
            // Rate Limit 체크
            ResponseEntity<?> rateLimitResponse = checkQrGenGenerateRateLimit(httpRequest);
            if (rateLimitResponse != null) {
                return rateLimitResponse;
            }

            log.info("✅ [CHECK] QR 생성 요청: type={}, value={}",
                    request.getContentType(),
                    request.getContentValue() != null ?
                        request.getContentValue().substring(0, Math.min(50, request.getContentValue().length())) : "null");

            // QR 코드 생성
            byte[] qrImage = generatorService.generateQrCode(request);

            // 로그인 사용자면 히스토리 저장
            if (isAuthenticated()) {
                QrGenUser user = getCurrentQrGenUser();
                if (user != null) {
                    String imagePath = generatorService.saveQrCodeToFile(qrImage, user.getQrGenUserId());
                    generatorService.saveQrGenHistory(user.getQrGenUserId(), request, imagePath);
                }
            } else {
                // 비로그인 사용자 카운트 증가
                String ipAddress = rateLimitService.getClientIpAddress(httpRequest);
                rateLimitService.incrementQrGenAnonymousCount(ipAddress);
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
     * QR 생성 Rate Limit 체크 (로그인/비로그인 분기)
     * @return 제한 초과 시 429 응답, 정상이면 null
     */
    private ResponseEntity<?> checkQrGenGenerateRateLimit(HttpServletRequest httpRequest) {
        QrGenRateLimitCheckResult result;

        if (isAuthenticated()) {
            QrGenUser user = getCurrentQrGenUser();
            if (user == null) return null;
            result = rateLimitService.checkQrGenAuthenticatedRateLimit(user.getQrGenUserId());
        } else {
            String ipAddress = rateLimitService.getClientIpAddress(httpRequest);
            result = rateLimitService.checkQrGenAnonymousRateLimit(ipAddress);
        }

        if (result.exceeded()) {
            log.info("✅ [CHECK] QR 생성 제한 초과: limit={}, remaining={}", result.limit(), result.remaining());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "error", "RATE_LIMIT_EXCEEDED",
                            "message", result.message(),
                            "remaining", result.remaining()
                    ));
        }
        return null;
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
    private QrGenUser getCurrentQrGenUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return authService.findQrGenUserByLoginId(auth.getName());
        }
        return null;
    }
}
