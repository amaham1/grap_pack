package co.grap.pack.common.seo.model;

/**
 * 구조화 데이터용 Breadcrumb 항목이다.
 */
public record PublicSeoBreadcrumbItem(
        String name,
        String path
) {
}
