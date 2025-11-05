package pl.hubertkuch.jdocify.description;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.hubertkuch.jdocify.parser.JavaDocParser;
import java.lang.reflect.Method;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JavaDocDescriptionStrategyTest {

    @Mock
    private JavaDocParser javaDocParser;

    private JavaDocDescriptionStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new JavaDocDescriptionStrategy(javaDocParser);
    }

    static class SampleClass {
        public void documentedMethod() {
        }

        public void undocumentedMethod() {
        }
    }

    @Test
    void getDescription_shouldReturnDescriptionFromJavadoc() throws NoSuchMethodException {
        Method method = SampleClass.class.getMethod("documentedMethod");
        when(javaDocParser.getMethodJavaDoc("documentedMethod")).thenReturn(Optional.of("This is a Javadoc description."));

        Optional<String> description = strategy.getDescription(method);

        assertTrue(description.isPresent());
        assertEquals("This is a Javadoc description.", description.get());
    }

    @Test
    void getDescription_shouldReturnEmptyForMethodWithoutJavadoc() throws NoSuchMethodException {
        Method method = SampleClass.class.getMethod("undocumentedMethod");
        when(javaDocParser.getMethodJavaDoc("undocumentedMethod")).thenReturn(Optional.empty());

        Optional<String> description = strategy.getDescription(method);

        assertFalse(description.isPresent());
    }
}
