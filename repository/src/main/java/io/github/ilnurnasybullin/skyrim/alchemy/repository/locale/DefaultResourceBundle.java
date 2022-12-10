package io.github.ilnurnasybullin.skyrim.alchemy.repository.locale;

import java.util.*;

public class DefaultResourceBundle extends ResourceBundle {

    @Override
    protected Object handleGetObject(String key) {
        return key;
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.emptyEnumeration();
    }
}
