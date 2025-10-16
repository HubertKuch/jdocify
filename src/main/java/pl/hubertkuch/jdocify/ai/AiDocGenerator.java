package pl.hubertkuch.jdocify.ai;

import de.kherud.llama.InferenceParameters;
import de.kherud.llama.LlamaModel;
import de.kherud.llama.ModelParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.hubertkuch.jdocify.exceptions.DocGenerationException;

/**
 * Generates Java documentation for method signatures using a Llama model.
 * This class implements AutoCloseable to ensure the underlying model resources are
 * released properly.
 */
public class AiDocGenerator implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(AiDocGenerator.class);

    private final LlamaModel model;

    public AiDocGenerator(ModelParameters modelParameters) {
        this.model = new LlamaModel(modelParameters);
    }

    /**
     * Generates a single, concise Javadoc sentence for a given method signature.
     *
     * @param signature The full method signature to document.
     * @return A clean, single-sentence Javadoc description.
     * @throws DocGenerationException if the model fails to generate a response.
     */
    public String generateDoc(String signature) throws DocGenerationException {
        String prompt = buildPrompt(signature);

        log.debug("Generating documentation for method: {}", signature);
        log.trace("Full prompt for model: {}", prompt);

        var inferenceParameters = new InferenceParameters(prompt).setNPredict(128)
                                                                 .setTemperature(0.4f);

        var responseBuilder = new StringBuilder();

        log.debug("Starting AI model generation for signature: {}", signature);
        try {
            for (var output : model.generate(inferenceParameters)) {
                responseBuilder.append(output);
            }
            log.debug("AI model generation completed for signature: {}", signature);
        } catch (Exception e) {
            throw new DocGenerationException("Error during AI model generation for signature: " + signature, e);
        }

        String processedText = processGeneratedText(responseBuilder.toString());
        log.debug("Processed generated text: {}", processedText);

        return processedText;
    }

    private String buildPrompt(String signature) {
        return """
                Generate a short, one-sentence Javadoc description for the following Java method.
                Do not include the method signature in the response. End the sentence with a period.
                
                Example 1:
                Method: `public List<User> getUsers()`
                Response: Finds all users.
                
                Example 2:
                Method: `public void saveUser(User user)`
                Response: Saves a user object.
                
                Now, generate the response for this method:
                Method: `%s`
                Response: \
                """.formatted(signature).stripIndent();
    }

    /**
     * Cleans and truncates the raw model output to a safe, single sentence.
     */
    private String processGeneratedText(String rawText) {
        String cleanedText = rawText.replace("<|file_separator|>", "").trim();

        int firstPeriod = cleanedText.indexOf('.');
        if (firstPeriod != - 1) {
            return cleanedText.substring(0, firstPeriod + 1);
        }

        return cleanedText;
    }

    @Override
    public void close() {
        log.debug("Attempting to close LlamaModel.");
        try {
            model.close();
            log.debug("LlamaModel closed successfully.");
        } catch (Exception e) {
            log.error("Failed to close LlamaModel cleanly.", e);
        }
    }
}
