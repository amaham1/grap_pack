package co.grap.pack.common.visitor.service;

import co.grap.pack.common.visitor.model.PackVisitorMenuCode;
import co.grap.pack.common.visitor.model.PackVisitorServiceCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PackVisitorRouteClassifierTest {

    private final PackVisitorRouteClassifier classifier = new PackVisitorRouteClassifier();

    @Test
    void landingPathIsClassified() {
        var result = classifier.classify("/");

        assertThat(result).isPresent();
        assertThat(result.get().getServiceCode()).isEqualTo(PackVisitorServiceCode.LANDING);
        assertThat(result.get().getMenuCode()).isEqualTo(PackVisitorMenuCode.LANDING_HOME);
        assertThat(result.get().getRouteKey()).isEqualTo("/");
    }

    @Test
    void exhibitionListKeepsTabInRouteKey() {
        var result = classifier.classify("/grap/user/content/exhibitions?tab=ongoing");

        assertThat(result).isPresent();
        assertThat(result.get().getServiceCode()).isEqualTo(PackVisitorServiceCode.GRAP);
        assertThat(result.get().getMenuCode()).isEqualTo(PackVisitorMenuCode.GRAP_EXHIBITION_LIST);
        assertThat(result.get().getRouteKey()).isEqualTo("/grap/user/content/exhibitions?tab=ongoing");
    }

    @Test
    void qrManageMenuDetailIsClassified() {
        var result = classifier.classify("/qr-manage/view/menu/store-qr/12");

        assertThat(result).isPresent();
        assertThat(result.get().getServiceCode()).isEqualTo(PackVisitorServiceCode.QRMANAGE);
        assertThat(result.get().getMenuCode()).isEqualTo(PackVisitorMenuCode.QRMANAGE_MENU_DETAIL);
        assertThat(result.get().getRouteKey()).isEqualTo("/qr-manage/view/menu/{qrCode}/{menuId}");
    }

    @Test
    void unknownPathReturnsEmpty() {
        var result = classifier.classify("/unknown/path");

        assertThat(result).isEmpty();
    }
}
