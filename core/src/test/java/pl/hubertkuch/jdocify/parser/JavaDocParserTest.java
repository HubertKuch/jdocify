package pl.hubertkuch.jdocify.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class JavaDocParserTest {

    private JavaDocParser javaDocParser;

    @BeforeEach
    void setUp() throws IOException, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("SampleClass.java");
        assertNotNull(resource);
        File file = Paths.get(resource.toURI()).toFile();
        javaDocParser = new JavaDocParser(file.getAbsolutePath());
    }

    @Test
    void getClassJavaDoc_shouldParseClassJavadoc() {
        Optional<String> classJavaDoc = javaDocParser.getClassJavaDoc("SampleClass");
        assertTrue(classJavaDoc.isPresent());
        assertTrue(classJavaDoc.get().contains("This is a sample class for testing Javadoc parsing."));
    }

    @Test
    void getMethodJavaDoc_shouldParseMethodJavadoc() {
        Optional<String> methodJavaDoc = javaDocParser.getMethodJavaDoc("sampleMethod");
        assertTrue(methodJavaDoc.isPresent());
        assertTrue(methodJavaDoc.get().contains("This is a sample method."));
    }

    @Test
    void getMethodJavaDoc_shouldReturnEmptyForMethodWithoutJavadoc() {
        Optional<String> methodJavaDoc = javaDocParser.getMethodJavaDoc("methodWithoutJavadoc");
        assertFalse(methodJavaDoc.isPresent());
    }
}
