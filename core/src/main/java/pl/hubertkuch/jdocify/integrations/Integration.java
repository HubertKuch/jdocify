package pl.hubertkuch.jdocify.integrations;

import pl.hubertkuch.jdocify.vo.ClassData;

import java.io.IOException;
import java.util.List;

/**
 * Represents an integration that processes a collection of class data,
 * typically to export it to a specific documentation format or platform.
 */
public interface Integration {

    /**
     * Runs the integration process.
     *
     * @param classes The list of class data objects to process.
     * @throws IOException if an error occurs during the export process.
     */
    void run(List<ClassData> classes) throws IOException;
}
