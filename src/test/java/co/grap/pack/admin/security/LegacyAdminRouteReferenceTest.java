package co.grap.pack.admin.security;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LegacyAdminRouteReferenceTest {

    private static final List<String> TEXT_EXTENSIONS = List.of(
            ".java", ".html", ".xml", ".yml", ".yaml", ".js", ".css", ".txt"
    );

    @Test
    void mainSourcesDoNotReferenceRemovedGrapAdminRoutes() throws IOException {
        String legacyAdminRoute = "/grap/" + "admin";
        String legacyAuthRoute = "/grap/" + "auth";

        try (var paths = Files.walk(Path.of("src/main"))) {
            List<String> offenders = paths
                    .filter(Files::isRegularFile)
                    .filter(this::isTextFile)
                    .filter(path -> containsLegacyRoute(path, legacyAdminRoute, legacyAuthRoute))
                    .map(Path::toString)
                    .toList();

            assertThat(offenders).isEmpty();
        }
    }

    private boolean isTextFile(Path path) {
        String fileName = path.getFileName().toString();
        return TEXT_EXTENSIONS.stream().anyMatch(fileName::endsWith);
    }

    private boolean containsLegacyRoute(Path path, String legacyAdminRoute, String legacyAuthRoute) {
        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            return content.contains(legacyAdminRoute) || content.contains(legacyAuthRoute);
        } catch (IOException exception) {
            throw new IllegalStateException("파일을 읽을 수 없습니다: " + path, exception);
        }
    }
}
