package pl.hubertkuch.jdocify.settings;

import org.aeonbits.owner.Config;
import pl.hubertkuch.jdocify.integrations.Integration;

@Config.Sources({"file:~/.jdocify/config.properties", "classpath:default.properties"})
public interface DocifySettings extends Config {
    @Config.Key("jdocify.scanPackage")
    String getScanPackage();

    @Config.Key("jdocify.ai.path")
    String getModelPath();

    @Config.Key("jdocify.ai.enabled")
    boolean isAiEnabled();

    @Config.Key("jdocify.ai.downloadUrl")
    String getModelDownloadUrl();

    @Key("jdocify.integration.class")
    @DefaultValue("pl.hubertkuch.jdocify.integrations.VitePressIntegration")
    @ConverterClass(IntegrationConverter.class)
    Class<? extends Integration> integrationClass();
}
