package pl.hubertkuch.jdocify.naming;

import org.apache.commons.lang3.StringUtils;
import pl.hubertkuch.jdocify.annotations.DocumentedStory;

public class DefaultFileNamer implements FileNamer {
    @Override
    public String name(Class<?> clazz) {
        var simpleName = clazz.getSimpleName();

        if (StringUtils.isBlank(simpleName)) {
            return simpleName;
        }

        String[] words = StringUtils.splitByCharacterTypeCamelCase(simpleName);

        return StringUtils.join(words, '-').toLowerCase();
    }

    @Override
    public String name(DocumentedStory story) {
        return story.name();
    }
}
