package pl.hubertkuch.jdocify.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateEngine {

    private static final Logger log = LoggerFactory.getLogger(TemplateEngine.class);

    public String getTemplate(String templateName) throws IOException {
        try (var is =
                getClass().getClassLoader().getResourceAsStream("templates/" + templateName)) {
            if (is == null) {
                throw new IOException("Template not found: " + templateName);
            }
            try (var isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                    var reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }

    public String render(String template, Object data) {
        log.info("Rendering template for data: {}", data);
        for (java.lang.reflect.Method method : data.getClass().getDeclaredMethods()) {
            log.info("Processing method: {}", method.getName());
            if (method.getParameterCount() == 0
                    && !method.getName().equals("hashCode")
                    && !method.getName().equals("equals")
                    && !method.getName().equals("toString")
                    && !method.getName().equals("getClass")) {
                try {
                    String propertyName = method.getName();
                    if (propertyName.startsWith("get") && propertyName.length() > 3) {
                        propertyName =
                                Character.toLowerCase(propertyName.charAt(3))
                                        + propertyName.substring(4);
                    } else if (propertyName.startsWith("is") && propertyName.length() > 2) {
                        propertyName =
                                Character.toLowerCase(propertyName.charAt(2))
                                        + propertyName.substring(3);
                    }

                    String placeholder = "{{" + propertyName + "}}";
                    Object value = method.invoke(data);
                    log.info("Placeholder: {}, Value: {}", placeholder, value);
                    if (value != null) {
                        template = template.replace(placeholder, value.toString());
                    }
                } catch (Exception e) {
                    log.error(
                            "Error rendering template for method {}: {}",
                            method.getName(),
                            e.getMessage());
                }
            }
        }

        return template;
    }
}
