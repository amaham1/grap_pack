package co.grap.pack.admin.qrgen.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QRgen 콘텐츠 타입별 집계 모델이다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminQrGenTypeStat {

    private String contentType;
    private Long count;
}
