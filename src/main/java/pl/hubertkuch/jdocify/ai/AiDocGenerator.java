package pl.hubertkuch.jdocify.ai;

import de.kherud.llama.InferenceParameters;
import de.kherud.llama.LlamaModel;
import de.kherud.llama.ModelParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AiDocGenerator {

    private static final Logger log = LoggerFactory.getLogger(AiDocGenerator.class);

    private final LlamaModel model;

    public AiDocGenerator(ModelParameters modelParameters) {
        this.model = new LlamaModel(modelParameters);
    }

    public String generateDoc(String signature) {
        String prompt =
                "Generate a short, one-sentence description for the following method:"
                        + " `"
                        + signature
                        + "`. Do not include the signature in the response. For example: getUsers() into Find all users";

        log.info("Generating documentation for method: {}", signature);
        log.debug("Prompt: {}", prompt);

        var inferenceParameters = new InferenceParameters(prompt).setNPredict(128)
                                                                 .setTemperature(0.4f);

        var sb = new StringBuilder();

        log.debug("Starting AI model generation for signature: {}", signature);
        try {
            for (var output : model.generate(inferenceParameters)) {
                sb.append(output.toString());
                log.trace("Generated chunk: {}", output);
            }
            log.debug("AI model generation completed for signature: {}", signature);
        } catch (Exception e) {
            log.error("Error during AI model generation for signature {}: {}", signature, e.getMessage(), e);
            return "";
        }

        String generatedText = sb.toString();

        generatedText = generatedText.replace("<|file_separator|>", "").trim();
        if (generatedText.length() > 200) { // Truncate to a reasonable length
            generatedText = generatedText.substring(0, 200) + "...";
        }

        log.debug("Processed generated text: {}", generatedText);

        return generatedText;
    }

    public void close() {
        log.debug("Attempting to close LlamaModel.");
        model.close();
        log.debug("LlamaModel closed successfully.");
    }
}
