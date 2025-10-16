package pl.hubertkuch.jdocify.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DocumentationWriter {

    private static final Logger log = LoggerFactory.getLogger(DocumentationWriter.class);

    private final File outputDir;

    public DocumentationWriter() {
        String outputDirPath = System.getProperty("jdocify.outputDir", "docs");
        this.outputDir = new File(outputDirPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
    }

    public void write(String fileName, String content) {
        File outputFile = new File(outputDir, fileName + ".md");
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(content);
            log.info("Successfully wrote documentation to: {}", outputFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to write documentation to file: {}", outputFile.getAbsolutePath(), e);
        }
    }
}
