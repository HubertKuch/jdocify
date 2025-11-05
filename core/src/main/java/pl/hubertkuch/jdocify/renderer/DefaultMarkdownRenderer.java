package pl.hubertkuch.jdocify.renderer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import pl.hubertkuch.jdocify.template.TemplateEngine;
import pl.hubertkuch.jdocify.vo.ClassData;
import pl.hubertkuch.jdocify.vo.ConstructorData;
import pl.hubertkuch.jdocify.vo.FieldData;
import pl.hubertkuch.jdocify.vo.MethodData;
import pl.hubertkuch.jdocify.vo.StoryData;
import pl.hubertkuch.jdocify.vo.StoryStepData;

public class DefaultMarkdownRenderer implements MarkdownRenderer {

    private final TemplateEngine templateEngine;

    public DefaultMarkdownRenderer(TemplateEngine templateEngine) {
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

    public String render(StoryData storyData) throws IOException {
        String storyTemplate = templateEngine.getTemplate("story.md.template");

        // Render only the name part of the story template
        String renderedStory = storyTemplate.replace("{{name}}", storyData.name());

        StringBuilder stepsContent = new StringBuilder();
        for (StoryStepData step : storyData.steps()) {
            if (step.narrative().isPresent()) {
                stepsContent.append(step.narrative().get()).append("\n\n");
            } else if (step.classData().isPresent()) {
                ClassData classData = step.classData().get();
                // Render class data
                stepsContent.append("### Class: ").append(classData.name()).append("\n\n");
                stepsContent.append(classData.description()).append("\n\n");

                // Render fields if present
                if (classData.fields() != null && !classData.fields().isEmpty()) {
                    stepsContent.append("#### Fields\n\n");
                    for (FieldData field : classData.fields()) {
                        stepsContent.append(renderField(field)).append("\n");
                    }
                    stepsContent.append("\n");
                }

                // Render constructors if present
                if (classData.constructors() != null && !classData.constructors().isEmpty()) {
                    stepsContent.append("#### Constructors\n\n");
                    for (ConstructorData constructor : classData.constructors()) {
                        stepsContent.append(renderConstructor(constructor)).append("\n");
                    }
                    stepsContent.append("\n");
                }

                // Render methods (either filtered or all)
                List<MethodData> methodsToRender = step.methodDataList();
                if (methodsToRender != null && !methodsToRender.isEmpty()) {
                    stepsContent.append("#### Methods\n\n");
                    for (MethodData method : methodsToRender) {
                        stepsContent.append(renderMethod(method)).append("\n");
                    }
                    stepsContent.append("\n");
                }
            }
        }

        renderedStory = renderedStory.replace("{{steps}}", stepsContent.toString());

        return renderedStory;
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
