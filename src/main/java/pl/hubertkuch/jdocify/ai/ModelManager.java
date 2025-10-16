package pl.hubertkuch.jdocify.ai;

import de.kherud.llama.ModelParameters;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelManager {
    private static final Logger log = LoggerFactory.getLogger(ModelManager.class);

    private void downloadModel(String url, String path) throws IOException {
        log.info("Downloading model from {} to {}", url, path);
        var modelFile = new File(path);
        modelFile.getParentFile().mkdirs();

        try (var in = new URL(url).openStream()) {
            Files.copy(in, modelFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        log.info("Model downloaded successfully.");
    }

    public Optional<AiDocGenerator> initAiDocGenerator() {
        var aiEnabled = Boolean.parseBoolean(System.getProperty("jdocify.ai.enabled", "false"));
        if (! aiEnabled) {
            log.info("AI documentation generation is disabled. To enable it, set the 'jdocify.ai.enabled' system property to 'true'.");

            return Optional.empty();
        }

        var modelPath = System.getProperty("jdocify.modelPath");
        if (modelPath == null || modelPath.isEmpty()) {
            modelPath = System.getProperty("user.home") + File.separator + ".jdocify" + File.separator + "models" + File.separator + "model.gguf";
            log.warn("'jdocify.modelPath' system property is not set. Using default model path: {}", modelPath);
        }

        var modelFile = new File(modelPath);
        if (! modelFile.exists()) {
            log.warn("Model file not found at path: {}", modelPath);
            var downloadUrl = System.getProperty("jdocify.model.downloadUrl");
            if (downloadUrl == null || downloadUrl.isEmpty()) {
                log.error("Model download URL is not specified. Please set the" + " 'jdocify.model.downloadUrl' system property.");

                return Optional.empty();
            }
            try {
                downloadModel(downloadUrl, modelPath);
            } catch (IOException e) {
                log.error("Failed to download model from {}: {}", downloadUrl, e.getMessage());

                return Optional.empty();
            }
        }

        log.info("Initializing AiDocGenerator with model path: {}", modelPath);
        var modelParameters = new ModelParameters().setModel(modelPath);

        return Optional.of(new AiDocGenerator(modelParameters));
    }
}
