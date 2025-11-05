package pl.hubertkuch.jdocify.description;

import java.util.Optional;

public interface DescriptionStrategy<T> {
    Optional<String> getDescription(T element);
}
