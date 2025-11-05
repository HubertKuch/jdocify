package pl.hubertkuch.jdocify.description;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.hubertkuch.jdocify.ai.AiDocGenerator;
import pl.hubertkuch.jdocify.exceptions.DocGenerationException;

import java.lang.reflect.Method;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiDescriptionStrategyTest {

    @Mock
    private AiDocGenerator aiDocGenerator;

    private AiDescriptionStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new AiDescriptionStrategy(aiDocGenerator);
    }

    static class SampleClass {
        public void sampleMethod() {
        }
    }

    @Test
    void getDescription_shouldReturnDescriptionFromAiGenerator() throws NoSuchMethodException, DocGenerationException {
        Method method = SampleClass.class.getMethod("sampleMethod");
        when(aiDocGenerator.generateDoc(anyString(), anyString())).thenReturn("This is an AI-generated description.");

        Optional<String> description = strategy.getDescription(method);

        assertTrue(description.isPresent());
        assertEquals("This is an AI-generated description.", description.get());
    }

    @Test
    void getDescription_shouldReturnEmptyWhenAiGeneratorIsNull() {
        strategy = new AiDescriptionStrategy(null);
        Method method = SampleClass.class.getMethods()[0]; // any method
        Optional<String> description = strategy.getDescription(method);
        assertFalse(description.isPresent());
    }
}
