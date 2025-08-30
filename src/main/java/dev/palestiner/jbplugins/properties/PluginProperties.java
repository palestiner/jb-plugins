package dev.palestiner.jbplugins.properties;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class PluginProperties {

    private final Map<String, String> urls = new HashMap<>();

    public String url(String value) {
        return urls.get(value);
    }

}
