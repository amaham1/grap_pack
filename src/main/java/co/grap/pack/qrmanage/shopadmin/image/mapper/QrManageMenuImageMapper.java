package co.grap.pack.qrmanage.shopadmin.image.mapper;

import co.grap.pack.qrmanage.shopadmin.image.model.QrManageMenuImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 메뉴 이미지 Mapper
 */
@Mapper
public interface QrManageMenuImageMapper {

    /**
     * 메뉴의 이미지 목록 조회
     */
    List<QrManageMenuImage> findByMenuId(@Param("menuId") Long menuId);

    /**
     * 이미지 조회
     */
    QrManageMenuImage findById(@Param("id") Long id);

    /**
     * 이미지 등록
     */
    void insert(QrManageMenuImage image);

    /**
     * 이미지 삭제
     */
    void delete(@Param("id") Long id);

    /**
     * 메뉴의 모든 이미지 삭제
     */
    void deleteByMenuId(@Param("menuId") Long menuId);

    /**
     * 정렬 순서 변경
     */
    void updateSortOrder(@Param("id") Long id, @Param("sortOrder") Integer sortOrder);

    /**
     * 다음 정렬 순서 조회
     */
    Integer getNextSortOrder(@Param("menuId") Long menuId);
}
