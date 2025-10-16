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
                "Generate a short, one-sentence Javadoc description for the following Java method:"
                        + " `"
                        + signature
                        + "`. Do not include the signature in the response.";

        log.info("Generating documentation for method: {}", signature);
        log.debug("Prompt: {}", prompt);

        var inferenceParameters = new InferenceParameters(prompt);
        var sb = new StringBuilder();

        for (var output : model.generate(inferenceParameters)) {
            sb.append(output.toString());
        }

        String generatedText = sb.toString();

        log.debug("Generated text: {}", generatedText);

        return generatedText;
    }
}
