package pl.hubertkuch.jdocify.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DefaultTemplateEngineTest {

    private DefaultTemplateEngine defaultTemplateEngine;

    @BeforeEach
    void setUp() {
        defaultTemplateEngine = DefaultTemplateEngine.createTemplateEngine();
    }

    @Test
    void getTemplate_shouldLoadTemplate() throws IOException {
        String templateContent = defaultTemplateEngine.getTemplate("class.md.template");
        assertNotNull(templateContent);
        assertTrue(templateContent.contains("{{name}}"));
    }

    @Test
    void getTemplate_shouldThrowExceptionForNonExistentTemplate() {
        assertThrows(IOException.class, () -> defaultTemplateEngine.getTemplate("nonexistent.template"));
    }

    record TestData(String name) {}
    record TestDataWithOther(String other) {}

    @Test
    void render_shouldReplacePlaceholders() {
        String template = "Hello, {{name}}!";
        TestData data = new TestData("World");
        String rendered = defaultTemplateEngine.render(template, data);
        assertEquals("Hello, World!", rendered);
    }

    @Test
    void render_shouldNotReplaceMissingPlaceholders() {
        String template = "Hello, {{name}}!";
        TestDataWithOther data = new TestDataWithOther("World");
        String rendered = defaultTemplateEngine.render(template, data);
        assertEquals("Hello, {{name}}!", rendered);
    }
}
