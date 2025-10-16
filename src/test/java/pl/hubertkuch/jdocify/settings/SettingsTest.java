package pl.hubertkuch.jdocify.settings;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SettingsTest {

    @BeforeEach
    void setUp() {
        Settings.reset();
    }

    @AfterEach
    void tearDown() {
        Settings.reset();
    }

    @Test
    void get_shouldLoadSettingsFromPropertiesFile() {
        // The config.properties file in src/main/resources has jdocify.ai.enabled=false
        DocifySettings settings = Settings.get();
        assertFalse(settings.isAiEnabled());
    }

    @Test
    void get_shouldOverrideSettingsWithSystemProperties() {
        // Set a system property to override the value from the config file
        System.setProperty("jdocify.ai.enabled", "true");

        DocifySettings settings = Settings.get();
        assertTrue(settings.isAiEnabled());

        // Clean up the system property
        System.clearProperty("jdocify.ai.enabled");
    }
}
