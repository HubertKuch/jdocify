package pl.hubertkuch.jdocify.settings;

import org.aeonbits.owner.Config;

@Config.Sources({"file:~/.jdocify/config.properties", "classpath:config.properties", "classpath:jdocify.properties"})
public interface DocifySettings extends Config {
    @Config.Key("jdocify.scanPackage")
    String getScanPackage();

    @DefaultValue("./docs")
    @Config.Key("jdocify.integration.output")
    String getIntegrationOutput();

    @DefaultValue("./model.gguf")
    @Config.Key("jdocify.ai.path")
    String getModelPath();

    @DefaultValue("true")
    @Config.Key("jdocify.ai.enabled")
    boolean isAiEnabled();

    @DefaultValue("")
    @Config.Key("jdocify.ai.downloadUrl")
    String getModelDownloadUrl();

    @Config.Key("jdocify.source.paths")
    @Config.DefaultValue("src/main/java")
    String[] getSourcePaths();
}
