package pl.hubertkuch.jdocify.settings;

import org.aeonbits.owner.Config;

@Config.Sources({"file:~/.jdocify/config.properties", "classpath:default.properties"})
public interface DocifySettings extends Config {
    @Config.Key("jdocify.scanPackage")
    String getScanPackage();

    @Config.Key("jdocify.integration.output")
    String getIntegrationOutput();

    @Config.Key("jdocify.ai.path")
    String getModelPath();

    @Config.Key("jdocify.ai.enabled")
    boolean isAiEnabled();

    @Config.Key("jdocify.ai.downloadUrl")
    String getModelDownloadUrl();

    @Config.Key("jdocify.source.paths")
    @Config.DefaultValue("src/main/java")
    String[] getSourcePaths();
}
