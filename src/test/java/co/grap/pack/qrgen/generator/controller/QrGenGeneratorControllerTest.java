package co.grap.pack.qrgen.generator.controller;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class QrGenGeneratorControllerTest {

    @Test
    void downloadPostMappingIsRemoved() {
        assertThat(hasPostMapping("/qrgen/download")).isFalse();
        assertThat(hasPostMapping("/qrgen/generate")).isTrue();
    }

    private boolean hasPostMapping(String fullPath) {
        return Arrays.stream(QrGenGeneratorController.class.getDeclaredMethods())
                .anyMatch(method -> hasPostMapping(method, fullPath));
    }

    private boolean hasPostMapping(Method method, String fullPath) {
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping == null) {
            return false;
        }

        return containsPath(postMapping.value(), fullPath)
                || containsPath(postMapping.path(), fullPath);
    }

    private boolean containsPath(String[] paths, String fullPath) {
        return Arrays.stream(paths)
                .map(path -> "/qrgen" + path)
                .anyMatch(fullPath::equals);
    }
}
