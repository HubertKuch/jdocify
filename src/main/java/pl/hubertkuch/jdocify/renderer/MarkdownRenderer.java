package pl.hubertkuch.jdocify.renderer;

import java.io.IOException;
import java.util.stream.Collectors;
import pl.hubertkuch.jdocify.template.TemplateEngine;
import pl.hubertkuch.jdocify.vo.ClassData;
import pl.hubertkuch.jdocify.vo.ConstructorData;
import pl.hubertkuch.jdocify.vo.FieldData;
import pl.hubertkuch.jdocify.vo.MethodData;

public class MarkdownRenderer {

    private final TemplateEngine templateEngine;

    public MarkdownRenderer(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String render(ClassData classData) throws IOException {
        String fieldsContent =
                classData.fields().stream()
                        .map(this::renderField)
                        .collect(Collectors.joining("\n"));
        String constructorsContent =
                classData.constructors().stream()
                        .map(this::renderConstructor)
                        .collect(Collectors.joining("\n"));
        String methodsContent =
                classData.methods().stream()
                        .map(this::renderMethod)
                        .collect(Collectors.joining("\n"));

        var renderedClassData =
                new ClassData(classData.name(), classData.description(), null, null, null);

        String classTemplate = templateEngine.getTemplate("class.md.template");
        String renderedTemplate = templateEngine.render(classTemplate, renderedClassData);

        renderedTemplate = renderedTemplate.replace("{{fields}}", fieldsContent);
        renderedTemplate = renderedTemplate.replace("{{constructors}}", constructorsContent);
        renderedTemplate = renderedTemplate.replace("{{methods}}", methodsContent);

        return renderedTemplate;
    }

    private String renderField(FieldData fieldData) {
        try {
            return templateEngine.render(
                    templateEngine.getTemplate("field.md.template"), fieldData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String renderConstructor(ConstructorData constructorData) {
        try {
            return templateEngine.render(
                    templateEngine.getTemplate("constructor.md.template"), constructorData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String renderMethod(MethodData methodData) {
        try {
            return templateEngine.render(
                    templateEngine.getTemplate("method.md.template"), methodData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
