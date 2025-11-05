package pl.hubertkuch.jdocify.plugin

public class JDocifyExtension {
    private String scanPackage = "default_not_set";
    private String modelPath = null;
    private String propertiesFileName = "jdocify.properties";

    public String getScanPackage() { return scanPackage; }
    public void setScanPackage(String scanPackage) { this.scanPackage = scanPackage; }

    public String getModelPath() { return modelPath; }
    public void setModelPath(String modelPath) { this.modelPath = modelPath; }

    public String getPropertiesFileName() { return propertiesFileName; }
    public void setPropertiesFileName(String propertiesFileName) { this.propertiesFileName = propertiesFileName; }
}
