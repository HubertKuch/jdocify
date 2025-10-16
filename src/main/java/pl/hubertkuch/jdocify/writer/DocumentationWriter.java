package pl.hubertkuch.jdocify.writer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentationWriter {

    private static final Logger log = LoggerFactory.getLogger(DocumentationWriter.class);

    private final Path outputDir;

    public DocumentationWriter() {
        String outputDirPath = System.getProperty("jdocify.outputDir", "docs");
        this.outputDir = Paths.get(outputDirPath);
        try {
            Files.createDirectories(this.outputDir);
        } catch (IOException e) {
            log.error("Failed to create output directory: {}", this.outputDir, e);
        }
    }

    public void write(String fileName, String content) {
        Path outputFile = outputDir.resolve(fileName + ".md");
        try {
            Files.writeString(outputFile, content);
            log.info("Successfully wrote documentation to: {}", outputFile.toAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to write documentation to file: {}", outputFile.toAbsolutePath(), e);
        }
    }
}
