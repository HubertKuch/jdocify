package pl.hubertkuch.jdocify.naming;

import pl.hubertkuch.jdocify.annotations.DocumentedStory;

public interface FileNamer {
    String name(Class<?> clazz);

    String name(DocumentedStory story);
}
