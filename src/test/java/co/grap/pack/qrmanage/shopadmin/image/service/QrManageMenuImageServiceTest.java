package co.grap.pack.qrmanage.shopadmin.image.service;

import co.grap.pack.qrmanage.shopadmin.image.mapper.QrManageMenuImageMapper;
import co.grap.pack.qrmanage.shopadmin.image.model.QrManageMenuImage;
import co.grap.pack.qrmanage.shopadmin.menu.mapper.QrManageMenuMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QrManageMenuImageServiceTest {

    @Mock
    private QrManageMenuImageMapper imageMapper;

    @Mock
    private QrManageMenuMapper menuMapper;

    private QrManageMenuImageService imageService;

    @BeforeEach
    void setUp() {
        imageService = new QrManageMenuImageService(imageMapper, menuMapper);
    }

    @Test
    void deleteImageRejectsImageOwnedByAnotherMenu() {
        when(menuMapper.existsByIdAndShopId(10L, 1L)).thenReturn(true);
        when(imageMapper.findByIdAndMenuId(100L, 10L)).thenReturn(null);

        assertThatThrownBy(() -> imageService.deleteImage(1L, 100L, 10L))
                .isInstanceOf(SecurityException.class);

        verify(imageMapper, never()).deleteByIdAndMenuId(any(), any());
    }

    @Test
    void setPrimaryImageRejectsImageOwnedByAnotherMenu() {
        when(menuMapper.existsByIdAndShopId(10L, 1L)).thenReturn(true);
        when(imageMapper.existsByIdAndMenuId(100L, 10L)).thenReturn(false);

        assertThatThrownBy(() -> imageService.setPrimaryImage(1L, 10L, 100L))
                .isInstanceOf(SecurityException.class);

        verify(menuMapper, never()).updatePrimaryImage(any(), any(), any());
    }

    @Test
    void updateSortOrdersRejectsIdsNotAllOwnedByMenu() {
        List<Long> imageIds = List.of(100L, 101L);
        when(menuMapper.existsByIdAndShopId(10L, 1L)).thenReturn(true);
        when(imageMapper.countByIdsAndMenuId(imageIds, 10L)).thenReturn(1);

        assertThatThrownBy(() -> imageService.updateSortOrders(1L, 10L, imageIds))
                .isInstanceOf(SecurityException.class);

        verify(imageMapper, never()).updateSortOrderForMenu(any(), any(), any());
    }

    @Test
    void deleteImageRemovesOwnedImageAndClearsPrimaryImage(@TempDir Path tempDir) throws IOException {
        Path imageFile = Files.createFile(tempDir.resolve("menu.jpg"));
        QrManageMenuImage image = QrManageMenuImage.builder()
                .id(100L)
                .menuId(10L)
                .filePath("/" + imageFile)
                .build();

        when(menuMapper.existsByIdAndShopId(10L, 1L)).thenReturn(true);
        when(imageMapper.findByIdAndMenuId(100L, 10L)).thenReturn(image);
        when(imageMapper.findByMenuId(10L)).thenReturn(List.of());

        imageService.deleteImage(1L, 100L, 10L);

        assertThat(Files.exists(imageFile)).isFalse();
        verify(imageMapper).deleteByIdAndMenuId(100L, 10L);
        verify(menuMapper).updatePrimaryImage(10L, null, 1L);
    }

    @Test
    void setPrimaryImageUpdatesWhenImageBelongsToMenu() {
        when(menuMapper.existsByIdAndShopId(10L, 1L)).thenReturn(true);
        when(imageMapper.existsByIdAndMenuId(100L, 10L)).thenReturn(true);

        imageService.setPrimaryImage(1L, 10L, 100L);

        verify(menuMapper).updatePrimaryImage(10L, 100L, 1L);
    }
}
