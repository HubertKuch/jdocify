package pl.hubertkuch.jdocify.settings;

import org.aeonbits.owner.ConfigFactory;

public class Settings {
    private static DocifySettings instance;

    public static synchronized DocifySettings get() {
        if (instance == null) {
            instance = ConfigFactory.create(DocifySettings.class, System.getProperties());
        }

        return instance;
    }
}
