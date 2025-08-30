package dev.palestiner.jbplugins.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nonnull;

import java.util.StringJoiner;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PluginVersion(int id, String version, long size, String file, JsonNode compatibleVersions) {

    @Override
    @Nonnull
    public String toString() {
        return new StringJoiner(", ", "[", "]")
                .add("version='" + version + "'")
                .add("size=" + size)
                .toString();
    }

}
