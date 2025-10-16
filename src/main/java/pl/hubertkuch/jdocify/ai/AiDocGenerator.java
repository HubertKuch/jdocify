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
     * Generates a single, concise Javadoc sentence by analyzing a method's signature and body.
     *
     * @param signature The full method signature to document.
     * @param methodBody The body of the method.
     * @return A clean, single-sentence Javadoc description.
     * @throws DocGenerationException if the model fails to generate a response.
     */
    public String generateDoc(String signature, String methodBody) throws DocGenerationException {
        String prompt = buildPrompt(signature, methodBody);

        log.debug("Generating documentation for method: {}", signature);
        log.trace("Full prompt for model: {}", prompt);

        var inferenceParameters = new InferenceParameters(prompt)
                .setNPredict(128)      // Max tokens to generate
                .setTemperature(0.4f); // Controls creativity vs. predictability

        var responseBuilder = new StringBuilder();

        log.debug("Starting AI model generation for signature: {}", signature);
        try {
            for (var output : model.generate(inferenceParameters)) {
                responseBuilder.append(output);
            }
            log.debug("AI model generation completed for signature: {}", signature);
        } catch (Exception e) {
            // Wrap the original exception in our custom, more specific exception.
            throw new DocGenerationException("Error during AI model generation for signature: " + signature, e);
        }

        String processedText = processGeneratedText(responseBuilder.toString());
        log.debug("Processed generated text: {}", processedText);

        return processedText;
    }

    /**
     * Constructs a more detailed "few-shot" prompt with method bodies for better model performance.
     * Providing examples that include the body teaches the model how to infer the method's true purpose.
     */
    private String buildPrompt(String signature, String methodBody) {
        return """
               Generate a short, one-sentence Javadoc description for the following Java method by analyzing its signature and body.
               The description should summarize the method's primary purpose. Do not include the signature in the response. End the sentence with a period.

               Example 1:
               Method Signature: `public User findUser(long id)`
               Method Body: `{
                   return database.query("SELECT * FROM users WHERE id = ?", id).stream().findFirst().orElse(null);
               }`
               Response: Finds a single user by their unique ID from the database.

               Example 2:
               Method Signature: `public void deactivateUser(User user)`
               Method Body: `{
                   user.setActive(false);
                   database.save(user);
                   eventBus.post(new UserDeactivatedEvent(user.getId()));
               }`
               Response: Deactivates a user, saves the change, and posts a deactivation event.

               Now, generate the response for this method:
               Method Signature: `%s`
               Method Body: `%s`
               Response: \
               """.formatted(signature, methodBody).stripIndent();
    }

    /**
     * Cleans and truncates the raw model output to a safe, single sentence.
     */
    private String processGeneratedText(String rawText) {
        // Remove any special tokens and trim whitespace.
        String cleanedText = rawText.replace("<|file_separator|>", "").trim();

        // Find the first sentence and return it to avoid overly long or incomplete output.
        int firstPeriod = cleanedText.indexOf('.');
        if (firstPeriod != -1) {
            return cleanedText.substring(0, firstPeriod + 1);
        }

        // Fallback: If no period, just return the cleaned text (it's likely short).
        return cleanedText;
    }

    /**
     * Closes the underlying LlamaModel, releasing its resources.
     * This is called automatically when using a try-with-resources block.
     */
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

