package co.grap.pack.admin.content.service;

import co.grap.pack.admin.content.mapper.AdminContentQueryMapper;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminContentPortalServiceTest {

    private static final String SESSION_ID = "session-1";

    @Mock
    private CmsAdminContentService cmsAdminContentService;

    @Mock
    private CmsAdminContentTypeService cmsAdminContentTypeService;

    @Mock
    private CmsAdminFestivalService cmsAdminFestivalService;

    @Mock
    private CmsAdminExhibitionService cmsAdminExhibitionService;

    @Mock
    private CmsAdminWelfareService cmsAdminWelfareService;

    @Mock
    private CmsAdminGasStationService cmsAdminGasStationService;

    @Mock
    private CmsUserRealEstateService cmsUserRealEstateService;

    @Mock
    private CmsJejuFestivalApiService cmsJejuFestivalApiService;

    @Mock
    private CmsJejuExhibitionApiService cmsJejuExhibitionApiService;

    @Mock
    private CmsJejuWelfareApiService cmsJejuWelfareApiService;

    @Mock
    private CmsJejuGasPriceApiService cmsJejuGasPriceApiService;

    @Mock
    private CmsJejuRealEstateApiService cmsJejuRealEstateApiService;

    @Mock
    private CmsSyncManager cmsSyncManager;

    @Mock
    private AdminContentQueryMapper adminContentQueryMapper;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AdminContentPortalService adminContentPortalService;

    @BeforeEach
    void setUp() {
        when(session.getId()).thenReturn(SESSION_ID);
    }

    @Test
    void realEstateJobRunsRecentSync() throws Exception {
        adminContentPortalService.runSyncJob("real-estate", session);

        verify(cmsJejuRealEstateApiService).syncRecentMonths();
        verify(cmsJejuRealEstateApiService, never()).bootstrapAllHistory(SESSION_ID, cmsSyncManager);
        verifyNoInteractions(cmsSyncManager);
    }

    @Test
    void realEstateBootstrapJobStillUsesSyncManager() throws Exception {
        adminContentPortalService.runSyncJob("real-estate-bootstrap", session);

        verify(cmsSyncManager).startSync(SESSION_ID);
        verify(cmsJejuRealEstateApiService).bootstrapAllHistory(SESSION_ID, cmsSyncManager);
        verify(cmsSyncManager).completeSync(SESSION_ID);
    }

    @Test
    void allJobRunsEveryExternalSyncThroughIntegratedAdminPath() throws Exception {
        adminContentPortalService.runSyncJob("all", session);

        verify(cmsSyncManager).startSync(SESSION_ID);
        verify(cmsJejuFestivalApiService).syncFestivalsFromExternalApi(SESSION_ID, cmsSyncManager);
        verify(cmsJejuExhibitionApiService).syncExhibitionsFromExternalApi(SESSION_ID, cmsSyncManager);
        verify(cmsJejuWelfareApiService).syncWelfareServicesFromExternalApi(SESSION_ID, cmsSyncManager);
        verify(cmsJejuGasPriceApiService).syncGasPricesFromExternalApi(SESSION_ID, cmsSyncManager);
        verify(cmsJejuRealEstateApiService).syncRecentMonths(SESSION_ID, cmsSyncManager);
        verify(cmsSyncManager, times(4)).checkCancellation(SESSION_ID);
        verify(cmsSyncManager).completeSync(SESSION_ID);
    }

    @Test
    void unsupportedJobKeyFailsFast() {
        assertThatThrownBy(() -> adminContentPortalService.runSyncJob("legacy", session))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
