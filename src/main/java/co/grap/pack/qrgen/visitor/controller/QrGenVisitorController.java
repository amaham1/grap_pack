package co.grap.pack.qrgen.visitor.controller;

import co.grap.pack.qrgen.visitor.model.QrGenVisitorUpdateRequest;
import co.grap.pack.qrgen.visitor.service.QrGenVisitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 방문자 추적 API 컨트롤러
 */
@RestController
@RequestMapping("/qrgen/visitor")
@RequiredArgsConstructor
@Slf4j
public class QrGenVisitorController {

    private final QrGenVisitorService qrGenVisitorService;

    /**
     * 체류시간 및 클라이언트 정보 업데이트
     * 페이지 이탈 시 Beacon API로 호출됨
     */
    @PostMapping("/update")
    public ResponseEntity<Void> updateVisitorDuration(@RequestBody QrGenVisitorUpdateRequest request) {
        try {
            if (request.getVisitorId() == null) {
                return ResponseEntity.badRequest().build();
            }

            qrGenVisitorService.updateQrGenVisitorDuration(request);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("❌ [ERROR] 방문자 정보 업데이트 실패: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
