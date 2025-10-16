package pl.hubertkuch.jdocify.settings;

import org.aeonbits.owner.ConfigFactory;
import pl.hubertkuch.jdocify.integrations.Integration;
import pl.hubertkuch.jdocify.integrations.VitePressIntegration;
import pl.hubertkuch.jdocify.renderer.DefaultMarkdownRenderer;
import pl.hubertkuch.jdocify.renderer.MarkdownRenderer;
import pl.hubertkuch.jdocify.template.DefaultTemplateEngine;
import pl.hubertkuch.jdocify.template.TemplateEngine;
import pl.hubertkuch.jdocify.writer.DefaultDocumentationWriter;
import pl.hubertkuch.jdocify.writer.DocumentationWriter;

import java.nio.file.Path;

public class Settings {
    private static DocifySettings instance;
    private static TemplateEngine templateEngine;
    private static DocumentationWriter documentationWriter;
    private static Integration integration;
    private static MarkdownRenderer markdownRenderer;

    public static synchronized void initialize() {
        setTemplateEngine(new DefaultTemplateEngine());
        setDocumentationWriter(new DefaultDocumentationWriter());
        setMarkdownRenderer(new DefaultMarkdownRenderer(templateEngine()));
        setIntegration(new VitePressIntegration(Path.of(get().getIntegrationOutput())));
    }

    public static synchronized DocifySettings get() {
        if (instance == null) {
            instance = ConfigFactory.create(DocifySettings.class, System.getProperties());
        }

        return instance;
    }

    public static synchronized void reset() {
        instance = null;
    }

    public static TemplateEngine templateEngine() {
        return templateEngine;
    }

    public static void setTemplateEngine(TemplateEngine templateEngine) {
        Settings.templateEngine = templateEngine;
    }

    public static DocifySettings instance() {
        return instance;
    }

    public static void setInstance(DocifySettings instance) {
        Settings.instance = instance;
    }

    public static DocumentationWriter documentationWriter() {
        return documentationWriter;
    }

    public static void setDocumentationWriter(DocumentationWriter documentationWriter) {
        Settings.documentationWriter = documentationWriter;
    }

    public static Integration integration() {
        return integration;
    }

    public static void setIntegration(Integration integration) {
        Settings.integration = integration;
    }

    public static MarkdownRenderer markdownRenderer() {
        return markdownRenderer;
    }

    public static void setMarkdownRenderer(MarkdownRenderer markdownRenderer) {
        Settings.markdownRenderer = markdownRenderer;
    }
}
