package pl.hubertkuch.jdocify.renderer;

import pl.hubertkuch.jdocify.vo.ClassData;
import pl.hubertkuch.jdocify.vo.ConstructorData;
import pl.hubertkuch.jdocify.vo.FieldData;
import pl.hubertkuch.jdocify.vo.MethodData;
import pl.hubertkuch.jdocify.template.TemplateEngine;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class MarkdownRenderer {

    private final TemplateEngine templateEngine;

    public MarkdownRenderer(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String render(ClassData classData) throws IOException {
        String fields = classData.fields().stream().map(this::renderField).collect(Collectors.joining("\n"));
        String constructors = classData.constructors().stream().map(this::renderConstructor).collect(Collectors.joining("\n"));
        String methods = classData.methods().stream().map(this::renderMethod).collect(Collectors.joining("\n"));

        // This is not ideal, as we are back to using a map.
        // I will improve this when I refactor the TemplateEngine.
        var data = Map.of(
                "name", classData.name(),
                "description", classData.description(),
                "fields", fields,
                "constructors", constructors,
                "methods", methods
        );

        return templateEngine.render(templateEngine.getTemplate("class.md.template"), data);
    }

    private String renderField(FieldData fieldData) {
        try {
            return templateEngine.render(templateEngine.getTemplate("field.md.template"), Map.of(
                    "field.name", fieldData.name(),
                    "field.type", fieldData.type()
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String renderConstructor(ConstructorData constructorData) {
        try {
            return templateEngine.render(templateEngine.getTemplate("constructor.md.template"), Map.of(
                    "constructor.signature", constructorData.signature()
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String renderMethod(MethodData methodData) {
        try {
            return templateEngine.render(templateEngine.getTemplate("method.md.template"), Map.of(
                    "method.name", methodData.name(),
                    "method.signature", methodData.signature(),
                    "method.description", methodData.description()
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
