package pl.hubertkuch.jdocify.settings;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:config.properties"})
public interface DocifySettings extends Config {
    @Config.Key("jdocify.scanPackage")
    String getScanPackage();

    @Config.Key("jdocify.ai.path")
    String getModelPath();

    @Config.Key("jdocify.ai.enabled")
    boolean isAiEnabled();

    @Config.Key("jdocify.ai.downloadUrl")
    String getModelDownloadUrl();
}
