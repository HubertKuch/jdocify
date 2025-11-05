package pl.hubertkuch.jdocify.template;

import java.io.IOException;

public interface TemplateEngine {
    String getTemplate(String templateName) throws IOException;

    String render(String template, Object data);
}
