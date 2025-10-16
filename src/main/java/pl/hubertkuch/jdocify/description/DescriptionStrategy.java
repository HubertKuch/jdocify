package pl.hubertkuch.jdocify.description;

import java.lang.reflect.Method;
import java.util.Optional;

public interface DescriptionStrategy {
    Optional<String> getDescription(Method method);
}
