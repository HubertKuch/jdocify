package pl.hubertkuch.jdocify;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.hubertkuch.jdocify.annotations.Documented;
import pl.hubertkuch.jdocify.annotations.DocumentedStory;
import pl.hubertkuch.jdocify.generator.DocumentationGenerator;
import pl.hubertkuch.jdocify.settings.Settings;

import java.io.IOException;

public class JDocify {

    private static final Logger log = LoggerFactory.getLogger(JDocify.class);

    public static void main(String[] args) throws IOException {
        new JDocify().run();
        System.exit(0);
    }

    public void run() throws IOException {
        var packageToScan = Settings.get().getScanPackage();
        if (packageToScan == null || packageToScan.isEmpty()) {
            log.error("Error: The package to scan was not specified.");
            log.error("Please configure the 'jdocify.scanPackage' in your config.properties file or" + " as a system property.");
            return;
        }

        log.info("Scanning for @Documented and @DocumentedStory classes in package: {}", packageToScan);
        var reflections = new Reflections(packageToScan, Scanners.TypesAnnotated);
        var documentationGenerator = new DocumentationGenerator();

        var documentedClasses = reflections.getTypesAnnotatedWith(Documented.class);
        if (documentedClasses.isEmpty()) {
            log.info("No classes found with the @Documented annotation.");
        } else {
            log.info("Found {} documented class(es):", documentedClasses.size());
            documentationGenerator.generate(documentedClasses);
        }

        var documentedStoryClasses = reflections.getTypesAnnotatedWith(DocumentedStory.class);
        if (documentedStoryClasses.isEmpty()) {
            log.info("No classes found with the @DocumentedStory annotation.");
        } else {
            log.info("Found {} documented story class(es):", documentedStoryClasses.size());
            documentationGenerator.generateStories(documentedStoryClasses);
        }
    }
}
