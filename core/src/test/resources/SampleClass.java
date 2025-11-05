package pl.hubertkuch.jdocify.sample;

/**
 * This is a sample class for testing Javadoc parsing.
 */
public class SampleClass {

    /**
     * This is a sample field.
     */
    public String sampleField;

    /**
     * This is a sample method.
     * @param param1 This is a parameter.
     * @return This is a return value.
     */
    public String sampleMethod(String param1) {
        return "hello";
    }

    public void methodWithoutJavadoc() {
    }
}
