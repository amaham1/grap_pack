package co.grap.pack.admin.content.service;

import co.grap.pack.admin.content.mapper.AdminContentQueryMapper;
import co.grap.pack.admin.content.model.AdminDatasetType;
import co.grap.pack.common.util.PageInfo;
import co.grap.pack.common.util.PaginationUtil;
import co.grap.pack.grap.admin.content.model.CmsAdminContent;
import co.grap.pack.grap.admin.content.model.CmsAdminContentSearchParam;
import co.grap.pack.grap.admin.content.model.CmsAdminContentType;
import co.grap.pack.grap.admin.content.service.CmsAdminContentService;
import co.grap.pack.grap.admin.content.service.CmsAdminContentTypeService;
import co.grap.pack.grap.admin.external.service.CmsAdminExhibitionService;
import co.grap.pack.grap.admin.external.service.CmsAdminFestivalService;
import co.grap.pack.grap.admin.external.service.CmsAdminGasStationService;
import co.grap.pack.grap.admin.external.service.CmsAdminWelfareService;
import co.grap.pack.grap.admin.sync.service.CmsSyncManager;
import co.grap.pack.grap.external.api.service.CmsJejuExhibitionApiService;
import co.grap.pack.grap.external.api.service.CmsJejuFestivalApiService;
import co.grap.pack.grap.external.api.service.CmsJejuGasPriceApiService;
import co.grap.pack.grap.external.api.service.CmsJejuRealEstateApiService;
import co.grap.pack.grap.external.api.service.CmsJejuWelfareApiService;
import co.grap.pack.grap.user.content.service.CmsUserRealEstateService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 통합 운영 포털 콘텐츠 서비스다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminContentPortalService {

    private final CmsAdminContentService cmsAdminContentService;
    private final CmsAdminContentTypeService cmsAdminContentTypeService;
    private final CmsAdminFestivalService cmsAdminFestivalService;
    private final CmsAdminExhibitionService cmsAdminExhibitionService;
    private final CmsAdminWelfareService cmsAdminWelfareService;
    private final CmsAdminGasStationService cmsAdminGasStationService;
    private final CmsUserRealEstateService cmsUserRealEstateService;
    private final CmsJejuFestivalApiService cmsJejuFestivalApiService;
    private final CmsJejuExhibitionApiService cmsJejuExhibitionApiService;
    private final CmsJejuWelfareApiService cmsJejuWelfareApiService;
    private final CmsJejuGasPriceApiService cmsJejuGasPriceApiService;
    private final CmsJejuRealEstateApiService cmsJejuRealEstateApiService;
    private final CmsSyncManager cmsSyncManager;
    private final AdminContentQueryMapper adminContentQueryMapper;

    /**
     * 수기 콘텐츠 목록을 조회한다.
     */
    public Map<String, Object> getArticleList(CmsAdminContentSearchParam searchParam) {
        Map<String, Object> result = new HashMap<>(cmsAdminContentService.getContentList(searchParam));
        int currentPage = (int) result.get("currentPage");
        int totalPages = (int) result.get("totalPages");
        result.put("pageInfo", PaginationUtil.calculatePageInfo(currentPage, totalPages));
        return result;
    }

    /**
     * 수기 콘텐츠를 조회한다.
     */
    public CmsAdminContent getArticle(Long contentId) {
        return cmsAdminContentService.getContent(contentId);
    }

    /**
     * 수기 콘텐츠를 저장한다.
     */
    @Transactional
    public void saveArticle(CmsAdminContent content, String operatorName) {
        if (content.getContentId() == null) {
            content.setCreateName(operatorName);
            cmsAdminContentService.createContent(content);
            return;
        }

        content.setUpdateName(operatorName);
        cmsAdminContentService.updateContent(content);
    }

    /**
     * 수기 콘텐츠를 삭제한다.
     */
    @Transactional
    public void deleteArticle(Long contentId) throws Exception {
        cmsAdminContentService.deleteContent(contentId);
    }

    /**
     * 수기 콘텐츠 공개 상태를 바꾼다.
     */
    @Transactional
    public void updateArticlePublishStatus(Long contentId, Boolean isPublished) {
        cmsAdminContentService.updatePublishStatus(contentId, isPublished);
    }

    /**
     * 콘텐츠 타입 목록을 조회한다.
     */
    public List<CmsAdminContentType> getContentTypes() {
        return cmsAdminContentTypeService.getAllContentTypeList();
    }

    /**
     * 활성 콘텐츠 타입 목록을 조회한다.
     */
    public List<CmsAdminContentType> getActiveContentTypes() {
        return cmsAdminContentTypeService.getActiveContentTypeList();
    }

    /**
     * 콘텐츠 타입을 조회한다.
     */
    public CmsAdminContentType getContentType(Long contentTypeId) {
        return cmsAdminContentTypeService.getContentType(contentTypeId);
    }

    /**
     * 콘텐츠 타입을 저장한다.
     */
    @Transactional
    public void saveContentType(CmsAdminContentType contentType) {
        if (contentType.getContentTypeId() == null) {
            cmsAdminContentTypeService.createContentType(contentType);
            return;
        }
        cmsAdminContentTypeService.updateContentType(contentType);
    }

    /**
     * 콘텐츠 타입을 삭제한다.
     */
    @Transactional
    public void deleteContentType(Long contentTypeId) {
        cmsAdminContentTypeService.deleteContentType(contentTypeId);
    }

    /**
     * 데이터셋 목록을 표준 형태로 조회한다.
     */
    public Map<String, Object> getDatasetList(
            AdminDatasetType datasetType,
            String keyword,
            String dealYearMonth,
            String sort,
            Integer page
    ) {
        return switch (datasetType) {
            case FESTIVALS -> standardizeDatasetList(cmsAdminFestivalService.getFestivalList(keyword, page, 20), "festivalList");
            case EXHIBITIONS -> standardizeDatasetList(cmsAdminExhibitionService.getExhibitionList(keyword, page, 20), "exhibitionList");
            case WELFARE -> standardizeDatasetList(cmsAdminWelfareService.getWelfareList(keyword, page, 20), "welfareList");
            case GAS_STATIONS -> standardizeGasStationList(cmsAdminGasStationService.getGasStationList(keyword, page, 20));
            case REAL_ESTATE -> standardizeRealEstateList(cmsUserRealEstateService.getRealEstateList(keyword, dealYearMonth, sort, page));
        };
    }

    /**
     * 데이터셋 상세를 조회한다.
     */
    public Map<String, Object> getDatasetDetail(AdminDatasetType datasetType, Long id, Integer year) {
        return switch (datasetType) {
            case FESTIVALS -> cmsAdminFestivalService.getFestivalDetail(id);
            case EXHIBITIONS -> cmsAdminExhibitionService.getExhibitionDetail(id);
            case WELFARE -> cmsAdminWelfareService.getWelfareDetail(id);
            case GAS_STATIONS -> cmsAdminGasStationService.getGasStationDetail(id);
            case REAL_ESTATE -> cmsUserRealEstateService.getRealEstateDetail(id, year);
        };
    }

    /**
     * 데이터셋 노출 상태를 바꾼다.
     */
    @Transactional
    public void updateDatasetVisibility(AdminDatasetType datasetType, Long id, Boolean isVisible) {
        switch (datasetType) {
            case FESTIVALS -> cmsAdminFestivalService.updateIsShow(id, isVisible);
            case EXHIBITIONS -> cmsAdminExhibitionService.updateIsShow(id, isVisible);
            case WELFARE -> cmsAdminWelfareService.updateIsShow(id, isVisible);
            default -> throw new IllegalStateException("해당 데이터셋은 노출 상태 변경을 지원하지 않습니다.");
        }
    }

    /**
     * 데이터셋 메모를 저장한다.
     */
    @Transactional
    public void updateDatasetMemo(AdminDatasetType datasetType, Long id, String adminMemo) {
        switch (datasetType) {
            case FESTIVALS -> cmsAdminFestivalService.updateAdminMemo(id, adminMemo);
            case EXHIBITIONS -> cmsAdminExhibitionService.updateAdminMemo(id, adminMemo);
            case WELFARE -> cmsAdminWelfareService.updateAdminMemo(id, adminMemo);
            default -> throw new IllegalStateException("해당 데이터셋은 메모 저장을 지원하지 않습니다.");
        }
    }

    /**
     * 데이터셋 검수 상태를 바꾼다.
     */
    @Transactional
    public void updateDatasetConfirmStatus(
            AdminDatasetType datasetType,
            Long id,
            String confirmStatus,
            String confirmedBy,
            String confirmMemo
    ) {
        switch (datasetType) {
            case FESTIVALS -> cmsAdminFestivalService.updateConfirmStatus(id, confirmStatus, confirmedBy, confirmMemo);
            case EXHIBITIONS -> cmsAdminExhibitionService.updateConfirmStatus(id, confirmStatus, confirmedBy, confirmMemo);
            case WELFARE -> cmsAdminWelfareService.updateConfirmStatus(id, confirmStatus, confirmedBy, confirmMemo);
            default -> throw new IllegalStateException("해당 데이터셋은 검수 상태 변경을 지원하지 않습니다.");
        }
    }

    /**
     * 데이터셋을 삭제한다.
     */
    @Transactional
    public void deleteDataset(AdminDatasetType datasetType, Long id) {
        switch (datasetType) {
            case FESTIVALS -> cmsAdminFestivalService.deleteFestival(id);
            case EXHIBITIONS -> cmsAdminExhibitionService.deleteExhibition(id);
            case WELFARE -> cmsAdminWelfareService.deleteWelfare(id);
            case GAS_STATIONS -> cmsAdminGasStationService.deleteGasStation(id);
            default -> throw new IllegalStateException("해당 데이터셋은 삭제를 지원하지 않습니다.");
        }
    }

    /**
     * 동기화 작업 요약을 조회한다.
     */
    public List<Map<String, Object>> getSyncJobSummaries() {
        return adminContentQueryMapper.selectSyncJobSummaries();
    }

    /**
     * 동기화 작업을 실행한다.
     */
    @Transactional
    public void runSyncJob(String jobKey, HttpSession session) throws Exception {
        String sessionId = session.getId();

        switch (jobKey) {
            case "festivals" -> cmsJejuFestivalApiService.syncFestivalsFromExternalApi();
            case "exhibitions" -> cmsJejuExhibitionApiService.syncExhibitionsFromExternalApi();
            case "welfare" -> cmsJejuWelfareApiService.syncWelfareServicesFromExternalApi();
            case "gas-stations" -> cmsJejuGasPriceApiService.syncGasPricesFromExternalApi();
            case "real-estate" -> cmsJejuRealEstateApiService.syncRecentMonths();
            case "real-estate-bootstrap" -> {
                cmsSyncManager.startSync(sessionId);
                try {
                    cmsJejuRealEstateApiService.bootstrapAllHistory(sessionId, cmsSyncManager);
                    cmsSyncManager.completeSync(sessionId);
                } catch (Exception exception) {
                    cmsSyncManager.completeSync(sessionId);
                    throw exception;
                }
            }
            case "all" -> {
                cmsSyncManager.startSync(sessionId);
                try {
                    cmsJejuFestivalApiService.syncFestivalsFromExternalApi(sessionId, cmsSyncManager);
                    cmsSyncManager.checkCancellation(sessionId);
                    cmsJejuExhibitionApiService.syncExhibitionsFromExternalApi(sessionId, cmsSyncManager);
                    cmsSyncManager.checkCancellation(sessionId);
                    cmsJejuWelfareApiService.syncWelfareServicesFromExternalApi(sessionId, cmsSyncManager);
                    cmsSyncManager.checkCancellation(sessionId);
                    cmsJejuGasPriceApiService.syncGasPricesFromExternalApi(sessionId, cmsSyncManager);
                    cmsSyncManager.checkCancellation(sessionId);
                    cmsJejuRealEstateApiService.syncRecentMonths(sessionId, cmsSyncManager);
                    cmsSyncManager.completeSync(sessionId);
                } catch (Exception exception) {
                    cmsSyncManager.completeSync(sessionId);
                    throw exception;
                }
            }
            default -> throw new IllegalArgumentException("지원하지 않는 동기화 작업입니다: " + jobKey);
        }
    }

    private Map<String, Object> standardizeDatasetList(Map<String, Object> raw, String listKey) {
        Map<String, Object> result = new HashMap<>(raw);
        result.put("items", raw.get(listKey));
        return result;
    }

    private Map<String, Object> standardizeGasStationList(Map<String, Object> raw) {
        Map<String, Object> result = new HashMap<>(raw);
        int currentPage = (int) raw.get("currentPage");
        int totalPages = (int) raw.get("totalPages");
        result.put("items", raw.get("gasStationList"));
        result.put("pageInfo", PaginationUtil.calculatePageInfo(currentPage, totalPages));
        return result;
    }

    private Map<String, Object> standardizeRealEstateList(Map<String, Object> raw) {
        Map<String, Object> result = new HashMap<>(raw);
        result.put("items", raw.get("propertyList"));
        result.put("keyword", ((Map<?, ?>) raw.get("filters")).get("keyword"));
        result.put("dealYearMonth", ((Map<?, ?>) raw.get("filters")).get("dealYearMonth"));
        result.put("sort", ((Map<?, ?>) raw.get("filters")).get("sort"));
        return result;
    }
}
