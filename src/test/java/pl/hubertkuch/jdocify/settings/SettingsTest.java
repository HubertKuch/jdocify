package pl.hubertkuch.jdocify.settings;

import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.Test;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SettingsTest {

    @Test
    void get_shouldLoadSettingsFromPropertiesFile() {
        // The config.properties file in src/main/resources has jdocify.ai.enabled=false
        DocifySettings settings = ConfigFactory.create(DocifySettings.class);
        assertFalse(settings.isAiEnabled());
    }

    @Test
    void get_shouldOverrideSettingsWithSystemProperties() {
        // Create a properties object with the overriding property
        Properties props = new Properties();
        props.setProperty("jdocify.ai.enabled", "true");

        // Create a new settings instance with the overriding properties
        DocifySettings settings = ConfigFactory.create(DocifySettings.class, props);
        assertTrue(settings.isAiEnabled());
    }
}
