package pl.hubertkuch.jdocify.description;

import pl.hubertkuch.jdocify.annotations.DocumentedMethod;

import java.lang.reflect.Method;
import java.util.Optional;

public class AnnotationDescriptionStrategy implements DescriptionStrategy {
    @Override
    public Optional<String> getDescription(Method method) {
        if (method.isAnnotationPresent(DocumentedMethod.class)) {
            String description = method.getAnnotation(DocumentedMethod.class).description();
            if (!description.isEmpty()) {
                return Optional.of(description);
            }
        }
        return Optional.empty();
    }
}
