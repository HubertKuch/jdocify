package pl.hubertkuch.jdocify.description;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.hubertkuch.jdocify.ai.AiDocGenerator;
import pl.hubertkuch.jdocify.exceptions.DocGenerationException;
import pl.hubertkuch.jdocify.utils.MethodBodyExtractor;

import java.lang.reflect.Method;
import java.util.Optional;

public class AiDescriptionStrategy implements DescriptionStrategy<Method> {

    private static final Logger log = LoggerFactory.getLogger(AiDescriptionStrategy.class);

    private final AiDocGenerator aiDocGenerator;

    public AiDescriptionStrategy(AiDocGenerator aiDocGenerator) {
        this.aiDocGenerator = aiDocGenerator;
    }

    @Override
    public Optional<String> getDescription(Method method) {
        if (aiDocGenerator != null) {
            try {
                return Optional.of(aiDocGenerator.generateDoc(method.getName(), MethodBodyExtractor.getBody(method)));
            } catch (Exception | DocGenerationException e) {
                log.error("Error generating AI documentation for method {}: {}", method.getName(), e.getMessage(), e);
            }
        }

        return Optional.empty();
    }
}
