package io.github.ilnurnasybullin.skyrim.alchemy.locale;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.spi.ResourceBundleProvider;

public class AlchemyResourceBundleProvider implements ResourceBundleProvider {

    @Override
    public ResourceBundle getBundle(String baseName, Locale locale) {
        return ResourceBundle.getBundle(baseName, locale);
    }
}
