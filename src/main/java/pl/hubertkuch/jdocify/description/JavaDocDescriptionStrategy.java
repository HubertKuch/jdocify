package pl.hubertkuch.jdocify.description;

import pl.hubertkuch.jdocify.parser.JavaDocParser;

import java.lang.reflect.Method;
import java.util.Optional;

public class JavaDocDescriptionStrategy implements DescriptionStrategy<Method> {

    private final JavaDocParser javaDocParser;

    public JavaDocDescriptionStrategy(JavaDocParser javaDocParser) {
        this.javaDocParser = javaDocParser;
    }

    @Override
    public Optional<String> getDescription(Method method) {
        return javaDocParser.getMethodJavaDoc(method.getName());
    }
}
