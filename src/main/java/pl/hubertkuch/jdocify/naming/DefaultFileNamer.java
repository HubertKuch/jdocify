package pl.hubertkuch.jdocify.naming;

import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import pl.hubertkuch.jdocify.annotations.DocumentedStory;

public class DefaultFileNamer implements FileNamer {
    @Override
    public String name(Class<?> clazz) {
        return StringUtils.capitalize(clazz.getName().toLowerCase(Locale.ROOT));
    }

    @Override
    public String name(DocumentedStory story) {
        return story.name();
    }
}
