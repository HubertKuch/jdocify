package pl.hubertkuch.jdocify.integrations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.hubertkuch.jdocify.renderer.DefaultMarkdownRenderer;
import pl.hubertkuch.jdocify.vo.ClassData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class VitePressIntegrationTest {

    private DefaultMarkdownRenderer defaultMarkdownRenderer;

    @TempDir
    private Path tempDir;

    private VitePressIntegration vitePressIntegration;

    @BeforeEach
    void setUp() {
        // Use a real DefaultMarkdownRenderer mock to control its output
        defaultMarkdownRenderer = mock(DefaultMarkdownRenderer.class);
        vitePressIntegration = new VitePressIntegration(tempDir);
    }

    @Test
    void run_shouldHandleEmptyClassListGracefully() throws IOException {
        // Arrange
        List<ClassData> emptyList = Collections.emptyList();

        // Act
        vitePressIntegration.run(emptyList);

        // Assert
        Path docsDir = tempDir.resolve("docs");
        assertTrue(Files.isDirectory(docsDir), "docs directory should be created");

        // Verify index.md is created but has no classes
        Path indexPath = docsDir.resolve("index.md");
        assertTrue(Files.exists(indexPath));
        String indexContent = Files.readString(indexPath);
        assertTrue(indexContent.contains("## Classes\n\n"));

        // Verify sidebar.js is created but has no items
        Path sidebarPath = docsDir.resolve(".vitepress/sidebar.js");
        assertTrue(Files.exists(sidebarPath));
        String sidebarContent = Files.readString(sidebarPath);
        Pattern emptyItemsPattern = Pattern.compile("items:\\s*\\[\\s*]");
        boolean matchFound = emptyItemsPattern.matcher(sidebarContent).find();

        assertTrue(matchFound, "Sidebar items should be empty");

        verify(defaultMarkdownRenderer, never()).render(any(ClassData.class));
    }
}