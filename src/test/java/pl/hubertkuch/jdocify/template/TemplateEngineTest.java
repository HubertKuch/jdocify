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
        assertThrows(IOException.class, () -> templateEngine.getTemplate("nonexistent.template"));
    }

    record TestData(String name) {}
    record TestDataWithOther(String other) {}

    @Test
    void render_shouldReplacePlaceholders() {
        String template = "Hello, {{name}}!";
        TestData data = new TestData("World");
        String rendered = templateEngine.render(template, data);
        assertEquals("Hello, World!", rendered);
    }

    @Test
    void render_shouldNotReplaceMissingPlaceholders() {
        String template = "Hello, {{name}}!";
        TestDataWithOther data = new TestDataWithOther("World");
        String rendered = templateEngine.render(template, data);
        assertEquals("Hello, {{name}}!", rendered);
    }
}
