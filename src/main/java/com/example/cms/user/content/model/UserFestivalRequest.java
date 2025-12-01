package com.example.cms.user.content.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자 축제/행사 등록 요청 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFestivalRequest {

    /** 제목 (필수) */
    private String title;

    /** 내용 (필수) - HTML 허용 */
    private String contentHtml;

    /** 출처 URL (선택) */
    private String sourceUrl;

    /** 작성자명 (필수) */
    private String writerName;
}
