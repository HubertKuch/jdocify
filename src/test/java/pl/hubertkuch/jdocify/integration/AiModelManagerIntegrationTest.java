package pl.hubertkuch.jdocify.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.hubertkuch.jdocify.ai.ModelManager;
import pl.hubertkuch.jdocify.settings.Settings;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiModelManagerIntegrationTest {

    @TempDir
    Path tempDir;

    private ModelManager modelManager;

    @BeforeEach
    void setUp() {
        Settings.reset();
        modelManager = new ModelManager();
    }

    @AfterEach
    void tearDown() {
        Settings.reset();
    }

    @Test
    void initAiDocGenerator_shouldNotDownloadModel_whenAiIsDisabled() {
        // Given
        System.setProperty("jdocify.ai.enabled", "false");
        Path modelPath = tempDir.resolve("model.gguf");
        System.setProperty("jdocify.modelPath", modelPath.toString());

        // When
        modelManager.initAiDocGenerator();

        // Then
        assertFalse(Files.exists(modelPath));
    }

    @Test
    void initAiDocGenerator_shouldDownloadModel_whenAiIsEnabledAndModelNotFound() throws InterruptedException {
        // Given
        System.setProperty("jdocify.ai.enabled", "true");
        Path modelPath = tempDir.resolve("model.gguf");
        System.setProperty("jdocify.ai.path", modelPath.toString());

        Thread.sleep(1000);

        URL dummyModelUrl = getClass().getClassLoader().getResource("dummy-model.gguf");
        System.setProperty("jdocify.ai.downloadUrl", dummyModelUrl.toString());

        // When
        modelManager.initAiDocGenerator();

        // Then
        assertTrue(Files.exists(modelPath));
    }
}
