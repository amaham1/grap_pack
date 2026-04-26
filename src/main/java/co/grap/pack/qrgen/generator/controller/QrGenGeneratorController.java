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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * QR Generator 공개 컨트롤러
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
    public String home(Model model, HttpServletRequest httpRequest) {
        model.addAttribute("contentTypes", QrGenContentType.values());
        model.addAttribute("isAuthenticated", isAuthenticated());

        QrGenRateLimitCheckResult rateLimitInfo = getQrGenRateLimitInfo(httpRequest);
        model.addAttribute("qrGenRemaining", rateLimitInfo.remaining());
        model.addAttribute("qrGenDailyLimit", rateLimitInfo.limit());
        QrGenSeoHelper.setQrGenHomeSeo(model);
        return "qrgen/qrgen-home";
    }

    /**
     * QR 코드 미리보기 API
     */
    @GetMapping("/preview")
    @ResponseBody
    public ResponseEntity<byte[]> previewQrCode(@RequestParam("contentType") String contentType,
                                                @RequestParam("contentValue") String contentValue,
                                                @RequestParam(value = "size", defaultValue = "300") Integer size,
                                                @RequestParam(value = "errorCorrection", defaultValue = "M") String errorCorrection,
                                                @RequestParam(value = "foregroundColor", defaultValue = "#000000") String foregroundColor,
                                                @RequestParam(value = "backgroundColor", defaultValue = "#FFFFFF") String backgroundColor,
                                                HttpServletRequest httpRequest) {
        try {
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
        } catch (Exception exception) {
            log.warn("QR 미리보기 실패: {}", exception.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * QR 코드 생성 API
     */
    @PostMapping("/generate")
    @ResponseBody
    public ResponseEntity<?> generateQrCode(@RequestBody QrGenRequest request,
                                            HttpServletRequest httpRequest) {
        try {
            ResponseEntity<?> rateLimitResponse = checkQrGenGenerateRateLimit(httpRequest);
            if (rateLimitResponse != null) {
                return rateLimitResponse;
            }

            log.info("✅ [CHECK] QR 생성 요청: type={}, valueLength={}",
                    request.getContentType(),
                    request.getContentValue() != null ? request.getContentValue().length() : 0);

            byte[] qrImage = generatorService.generateQrCode(request);

            if (isAuthenticated()) {
                QrGenUser user = getCurrentQrGenUser();
                if (user != null) {
                    String imagePath = generatorService.saveQrCodeToFile(qrImage, user.getQrGenUserId());
                    generatorService.saveQrGenHistory(user.getQrGenUserId(), request, imagePath);
                }
            } else {
                String ipAddress = rateLimitService.getClientIpAddress(httpRequest);
                rateLimitService.incrementQrGenAnonymousCount(ipAddress);
            }

            QrGenRateLimitCheckResult rateLimitInfo = getQrGenRateLimitInfo(httpRequest);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"qrcode.png\"")
                    .header("X-QrGen-Remaining", String.valueOf(rateLimitInfo.remaining()))
                    .header("X-QrGen-Limit", String.valueOf(rateLimitInfo.limit()))
                    .body(qrImage);
        } catch (Exception exception) {
            log.error("❌ [ERROR] QR 코드 생성 실패: {}", exception.getMessage(), exception);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * QR 생성 제한을 확인한다.
     */
    private ResponseEntity<?> checkQrGenGenerateRateLimit(HttpServletRequest httpRequest) {
        QrGenRateLimitCheckResult result;

        if (isAuthenticated()) {
            QrGenUser user = getCurrentQrGenUser();
            if (user == null) {
                return null;
            }
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
     * 현재 요청 기준 QR 생성 제한 정보를 조회한다.
     */
    private QrGenRateLimitCheckResult getQrGenRateLimitInfo(HttpServletRequest httpRequest) {
        if (isAuthenticated()) {
            QrGenUser user = getCurrentQrGenUser();
            if (user != null) {
                return rateLimitService.checkQrGenAuthenticatedRateLimit(user.getQrGenUserId());
            }
        }
        String ipAddress = rateLimitService.getClientIpAddress(httpRequest);
        return rateLimitService.checkQrGenAnonymousRateLimit(ipAddress);
    }

    /**
     * 로그인 여부 확인
     */
    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null
                && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal());
    }

    /**
     * 현재 로그인한 QRgen 사용자 조회
     */
    private QrGenUser getCurrentQrGenUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return authService.findQrGenUserByLoginId(auth.getName());
        }
        return null;
    }
}
