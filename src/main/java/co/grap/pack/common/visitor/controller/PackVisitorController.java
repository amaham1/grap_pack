package co.grap.pack.common.visitor.controller;

import co.grap.pack.common.visitor.model.PackVisitorUpdateRequest;
import co.grap.pack.common.visitor.service.PackVisitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 공통 방문자 추적 API 컨트롤러다.
 */
@RestController
@RequestMapping("/api/visitor")
@RequiredArgsConstructor
@Slf4j
public class PackVisitorController {

    private final PackVisitorService packVisitorService;

    /**
     * 체류시간과 클라이언트 정보를 갱신한다.
     */
    @PostMapping("/update")
    public ResponseEntity<Void> updateVisitorDuration(@RequestBody PackVisitorUpdateRequest request) {
        try {
            if (request.getVisitorId() == null) {
                return ResponseEntity.badRequest().build();
            }

            packVisitorService.updatePackVisitorDuration(request);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            log.error("❌ [ERROR] 공통 방문자 체류시간 업데이트 실패: {}", exception.getMessage(), exception);
            return ResponseEntity.internalServerError().build();
        }
    }
}
