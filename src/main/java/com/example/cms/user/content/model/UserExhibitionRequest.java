package com.example.cms.user.content.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 사용자 공연/전시 등록 요청 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserExhibitionRequest {

    /** 제목 (필수) */
    private String title;

    /** 카테고리명 (필수) - 전시, 공연, 행사 등 */
    private String categoryName;

    /** 작성자명 (필수) */
    private String writerName;

    /** 시작일 (선택) */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;

    /** 종료일 (선택) */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;

    /** 운영 시간 (선택) */
    private String timeInfo;

    /** 장소명 (선택) */
    private String locationName;

    /** 주최/주관 정보 (선택) */
    private String organizerInfo;

    /** 문의 전화번호 (선택) */
    private String telNumber;

    /** 요금 정보 (선택) */
    private String payInfo;
}
