package co.grap.pack.user.content.mapper;

import co.grap.pack.user.content.model.UserContent;
import co.grap.pack.user.content.model.UserContentSearchParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 사용자 콘텐츠 Mapper
 */
@Mapper
public interface UserContentMapper {

    /**
     * 공개된 콘텐츠 목록 조회
     */
    List<UserContent> selectPublishedContentList(UserContentSearchParam searchParam);

    /**
     * 공개된 콘텐츠 전체 개수 조회
     */
    int selectPublishedContentCount(UserContentSearchParam searchParam);

    /**
     * 콘텐츠 상세 조회 (공개된 것만)
     */
    UserContent selectPublishedContentById(Long contentId);

    /**
     * 조회수 증가
     */
    void updateViewCount(Long contentId);
}
