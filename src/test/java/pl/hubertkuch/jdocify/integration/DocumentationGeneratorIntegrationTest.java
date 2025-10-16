package pl.hubertkuch.jdocify.integration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.hubertkuch.jdocify.generator.DocumentationGenerator;
import pl.hubertkuch.jdocify.settings.Settings;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentationGeneratorIntegrationTest {

    private final String outputDir = "docs";
    private final String outputFileName = "UserService.md";
    private final Path outputFilePath = Path.of(outputDir, outputFileName);

    @BeforeEach
    void setUp() throws IOException {
        Settings.reset();
        // Ensure the output directory exists and is empty
        Files.createDirectories(Path.of(outputDir));
        Files.deleteIfExists(outputFilePath);
    }

    @AfterEach
    void tearDown() throws IOException {
        Settings.reset();
        // Clean up the generated file
        Files.deleteIfExists(outputFilePath);
    }

    @Test
    void generate_shouldCreateDocumentationForSampleClass() throws IOException {
        // Set the scanPackage to the sample package
        System.setProperty("jdocify.scanPackage", "pl.hubertkuch.jdocify.sample");

        // Run the documentation generator
        DocumentationGenerator.main(new String[]{});

        // Verify that the output file was created
        assertTrue(Files.exists(outputFilePath));

        // Verify the content of the output file
        String content = Files.readString(outputFilePath);
        try {
            assertTrue(content.contains("# User Service"));
            assertTrue(content.contains("## Fields"));
            assertTrue(content.contains("databaseConnection"));
            assertTrue(content.contains("## Constructors"));
            assertTrue(content.contains("## Methods"));
            assertTrue(content.contains("### findUserById"));
        } catch (AssertionError e) {
            System.err.println("Generated content:\n" + content);
            throw e;
        }
    }
}