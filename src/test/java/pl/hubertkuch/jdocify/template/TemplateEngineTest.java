package pl.hubertkuch.jdocify.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class TemplateEngineTest {

    private TemplateEngine templateEngine;

    @BeforeEach
    void setUp() {
        templateEngine = new TemplateEngine();
    }

    @Test
    void getTemplate_shouldLoadTemplate() throws IOException {
        String templateContent = templateEngine.getTemplate("class.md.template");
        assertNotNull(templateContent);
        assertTrue(templateContent.contains("{{name}}"));
    }

    @Test
    void getTemplate_shouldThrowExceptionForNonExistentTemplate() {
        assertThrows(IOException.class, () -> {
            templateEngine.getTemplate("nonexistent.template");
        });
    }

    @Test
    void render_shouldReplacePlaceholders() {
        String template = "Hello, {{name}}!";
        Map<String, String> data = Map.of("name", "World");
        String rendered = templateEngine.render(template, data);
        assertEquals("Hello, World!", rendered);
    }

    @Test
    void render_shouldNotReplaceMissingPlaceholders() {
        String template = "Hello, {{name}}!";
        Map<String, String> data = Map.of("other", "World");
        String rendered = templateEngine.render(template, data);
        assertEquals("Hello, {{name}}!", rendered);
    }
}
