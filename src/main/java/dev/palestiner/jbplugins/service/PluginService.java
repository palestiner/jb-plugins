package dev.palestiner.jbplugins.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.palestiner.jbplugins.model.Plugin;
import dev.palestiner.jbplugins.model.PluginVersion;
import dev.palestiner.jbplugins.properties.PluginProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class PluginService {

    private final static HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private final PluginProperties pluginProperties;
    private final ObjectMapper objectMapper;

    public PluginService(PluginProperties pluginProperties, ObjectMapper objectMapper) {
        this.pluginProperties = pluginProperties;
        this.objectMapper = objectMapper;
    }

    public List<Plugin> search(String pattern) {
        HttpRequest searchReq = HttpRequest.newBuilder()
                .uri(URI.create(pluginProperties.url("search").formatted(pattern)))
                .GET()
                .build();
        try {
            HttpResponse<String> send = HTTP_CLIENT.send(
                    searchReq,
                    HttpResponse.BodyHandlers.ofString()
            );
            JsonNode jsonNode = objectMapper.readTree(send.body());
            JsonNode plugins = jsonNode.get("plugins");
            return objectMapper.readValue(plugins.toString(), new TypeReference<>() {});
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<PluginVersion> pluginVersions(Plugin plugin) {
        HttpRequest versionsReq = HttpRequest.newBuilder()
                .uri(URI.create(pluginProperties.url("version").formatted(plugin.id())))
                .GET()
                .build();
        try {
            HttpResponse<String> send = HTTP_CLIENT.send(
                    versionsReq,
                    HttpResponse.BodyHandlers.ofString()
            );
            return objectMapper.readValue(send.body(), new TypeReference<>() {});
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream download(PluginVersion pluginVersion, String family) {
        HttpRequest downloadReq = HttpRequest.newBuilder()
                .uri(URI.create(pluginProperties.url("download").formatted(pluginVersion.file(), family)))
                .GET()
                .build();
        try {
            return HTTP_CLIENT.send(
                    downloadReq,
                    HttpResponse.BodyHandlers.ofInputStream()
            ).body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}

