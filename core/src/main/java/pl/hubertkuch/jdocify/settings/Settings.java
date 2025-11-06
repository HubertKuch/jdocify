package pl.hubertkuch.jdocify.settings;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.hubertkuch.jdocify.filter.DefaultMemberFilter;
import pl.hubertkuch.jdocify.filter.MemberFilter;
import pl.hubertkuch.jdocify.integrations.Integration;
import pl.hubertkuch.jdocify.integrations.VitePressIntegration;
import pl.hubertkuch.jdocify.naming.DefaultFileNamer;
import pl.hubertkuch.jdocify.naming.FileNamer;
import pl.hubertkuch.jdocify.renderer.DefaultMarkdownRenderer;
import pl.hubertkuch.jdocify.renderer.MarkdownRenderer;
import pl.hubertkuch.jdocify.template.DefaultTemplateEngine;
import pl.hubertkuch.jdocify.template.TemplateEngine;
import pl.hubertkuch.jdocify.writer.DefaultDocumentationWriter;
import pl.hubertkuch.jdocify.writer.DocumentationWriter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Settings {
    private static final Logger log = LoggerFactory.getLogger(Settings.class);
    private static DocifySettings instance;
    private static TemplateEngine templateEngine;
    private static DocumentationWriter documentationWriter;
    private static Integration integration;
    private static MarkdownRenderer markdownRenderer;
    private static FileNamer fileNamer;
    private static MemberFilter memberFilter;

    public static synchronized void initializeDefaults() {
        setTemplateEngine(new DefaultTemplateEngine());
        setDocumentationWriter(new DefaultDocumentationWriter());
        setMarkdownRenderer(new DefaultMarkdownRenderer(templateEngine()));
        var integrationOutput = get().getIntegrationOutput();

        log.info("Integration output " + integrationOutput);

        setIntegration(new VitePressIntegration(Path.of(integrationOutput)));
        setFileNamer(new DefaultFileNamer());
        setMemberFilter(new DefaultMemberFilter());
    }

    public static synchronized DocifySettings get() {
        if (instance == null) {
            var configFilePath = System.getProperty("jdocify.configFile");

            if (configFilePath != null && !configFilePath.isEmpty()) {
                try {
                    log.info("Config file path: {}", configFilePath);

                    var props = new Properties();

                    props.load(Files.newInputStream(Path.of(configFilePath)));

                    instance = ConfigFactory.create(DocifySettings.class, props);
                } catch (Exception e) {
                    log.error("Failed to load properties", e);
                    instance = ConfigFactory.create(DocifySettings.class, System.getProperties());
                }
            } else {
                instance = ConfigFactory.create(DocifySettings.class, System.getProperties());
            }
        }

        log.info("Getting configuration properties instance: {}", instance);

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

    public static void setFileNamer(FileNamer fileNamer) {
        Settings.fileNamer = fileNamer;
    }

    public static FileNamer fileNamer() {
        return fileNamer;
    }

    public static MemberFilter memberFilter() {
        return memberFilter;
    }

    public static void setMemberFilter(MemberFilter memberFilter) {
        Settings.memberFilter = memberFilter;
    }
}
