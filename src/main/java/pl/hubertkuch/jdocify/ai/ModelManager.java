package pl.hubertkuch.jdocify.ai;

import de.kherud.llama.ModelParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.hubertkuch.jdocify.settings.DocifySettings;
import pl.hubertkuch.jdocify.settings.Settings;
import pl.hubertkuch.jdocify.utils.FancyFileDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class ModelManager {
    private static final Logger log = LoggerFactory.getLogger(ModelManager.class);

    public Optional<AiDocGenerator> initAiDocGenerator() {
        if (! Settings.get().isAiEnabled()) {
            log.info("AI documentation generation is disabled. To enable it, set the 'jdocify.ai.enabled' system property to 'true'.");
            return Optional.empty();
        }

        return prepareModelFile().flatMap(this::createGenerator);
    }

    private Optional<Path> prepareModelFile() {
        final DocifySettings settings = Settings.get();
        final Path modelPath = Paths.get(settings.getModelPath());
        final String downloadUrl = settings.getModelDownloadUrl();

        if (Files.exists(modelPath)) {
            log.info("Model file found at path: {}. Validating...", modelPath);
            if (validateModel(downloadUrl, modelPath)) {
                return Optional.of(modelPath);
            }

            log.warn("Existing model file is corrupt or invalid. Deleting and preparing for re-download.");
            if (! deleteFile(modelPath)) {
                return Optional.empty();
            }
        }

        return downloadAndValidate(downloadUrl, modelPath);
    }

    private Optional<Path> downloadAndValidate(final String url, final Path path) {
        if (url == null || url.isBlank()) {
            log.error("Model download URL is not specified. Please set the 'jdocify.model.downloadUrl' system property.");
            return Optional.empty();
        }

        try {
            downloadModel(url, path);
            if (validateModel(url, path)) {
                return Optional.of(path);
            }
            log.error("Model validation failed AFTER download. The downloaded file is corrupt. Please try again.");
            return Optional.empty();
        } catch (IOException e) {
            log.error("Failed to download model from {}: {}", url, e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<AiDocGenerator> createGenerator(final Path modelPath) {
        log.info("Initializing AiDocGenerator with model path: {}", modelPath);
        try {
            final var modelParameters = new ModelParameters().setModel(modelPath.toString());
            final var aiDocGenerator = new AiDocGenerator(modelParameters);
            log.debug("LlamaModel instantiated successfully.");
            return Optional.of(aiDocGenerator);
        } catch (Exception e) {
            log.error("Failed to load model from path: {}. Error: {}", modelPath, e.getMessage(), e);
            return Optional.empty();
        }
    }

    private void downloadModel(final String url, final Path path) throws IOException {
        log.info("Preparing to download model from {} to {}", url, path);
        Files.createDirectories(path.getParent());
        FancyFileDownloader.downloadFile(url, path);
    }

    private boolean deleteFile(final Path path) {
        try {
            Files.delete(path);
            return true;
        } catch (IOException e) {
            log.error("Failed to delete corrupt model file at {}: {}", path, e.getMessage());
            return false;
        }
    }

    /**
     * Validates a model file by comparing its local size against the expected
     * size from the download server's Content-Length header.
     *
     * @param url       The original URL of the model to get the expected size.
     * @param localPath The path to the local model file.
     * @return true if the local file size matches the expected size, false otherwise.
     */
    private boolean validateModel(final String url, final Path localPath) {
        log.info("Validating model file: {}", localPath);
        try {
            if (! Files.exists(localPath) || ! Files.isRegularFile(localPath)) {
                log.warn("Validation failed: Model file does not exist or is not a regular file.");
                return false;
            }

            final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            final int responseCode = connection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                log.warn("Validation failed: Could not get model metadata. Server responded with code {}.", responseCode);
                return false;
            }

            final long expectedSize = connection.getContentLengthLong();
            final long actualSize = Files.size(localPath);

            if (expectedSize == - 1) {
                log.warn("Validation skipped: Server did not provide a Content-Length header.");
                return true;
            }

            if (expectedSize == actualSize) {
                log.info("Model validation successful. File size matches expected size ({} bytes).", actualSize);
                return true;
            } else {
                log.error("Validation failed: Model file is incomplete or corrupted. Expected size: {}, Actual size: {}", expectedSize, actualSize);
                return false;
            }
        } catch (IOException e) {
            log.error("Validation failed due to an IO error: {}", e.getMessage());
            return false;
        }
    }
}

