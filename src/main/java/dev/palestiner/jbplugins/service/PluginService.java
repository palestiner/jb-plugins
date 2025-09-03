package dev.palestiner.jbplugins.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.palestiner.jbplugins.model.Plugin;
import dev.palestiner.jbplugins.model.PluginVersion;
import dev.palestiner.jbplugins.properties.PluginProperties;
import org.jline.terminal.Terminal;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

@Service
public class PluginService {

    private static final TypeReference<List<PluginVersion>> LIST_PLUGIN_VERSIONS_TYPE = new TypeReference<>() {};
    private static final TypeReference<List<Plugin>> LIST_PLUGINS_TYPE = new TypeReference<>() {};
    private final PluginProperties pluginProperties;
    private final ObjectMapper objectMapper;
    private final DownloadProgressBar progressBar;
    private final Terminal terminal;
    private final HttpClient httpClient;

    public PluginService(
            PluginProperties pluginProperties,
            ObjectMapper objectMapper,
            DownloadProgressBar progressBar,
            Terminal terminal,
            HttpClient httpClient
    ) {
        this.pluginProperties = pluginProperties;
        this.objectMapper = objectMapper;
        this.progressBar = progressBar;
        this.terminal = terminal;
        this.httpClient = httpClient;
    }

    public List<Plugin> search(String pattern) {
        HttpRequest searchReq = HttpRequest.newBuilder()
                .uri(URI.create(pluginProperties.url("search").formatted(pattern)))
                .GET()
                .build();
        try {
            HttpResponse<String> send = httpClient.send(
                    searchReq,
                    HttpResponse.BodyHandlers.ofString()
            );
            JsonNode jsonNode = objectMapper.readTree(send.body());
            JsonNode plugins = jsonNode.get("plugins");
            return objectMapper.readValue(plugins.toString(), LIST_PLUGINS_TYPE);
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
            HttpResponse<String> send = httpClient.send(
                    versionsReq,
                    HttpResponse.BodyHandlers.ofString()
            );
            return objectMapper.readValue(send.body(), LIST_PLUGIN_VERSIONS_TYPE);
        } catch (IOException | InterruptedException e) { // TODO handle InterruptedException (CTRL-C)
            throw new RuntimeException(e);
        }
    }

    public void downloadPlugin(
            PluginVersion pluginVersion,
            String family
    ) {
        String fileName = fileName(pluginVersion);
        try (InputStream is = download(pluginVersion, family);
             FileOutputStream os = new FileOutputStream(fileName)
        ) {
            long totalBytes = pluginVersion.size();
            long downloadedBytes = 0;
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
                downloadedBytes += bytesRead;
                progressBar.display(downloadedBytes, totalBytes);
            }
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO handle IOException (CTRL-C -> InterruptedException)
        }
        terminal.writer().println("\nPlugin downloaded to " + fileName);
    }

    private String fileName(PluginVersion pluginVersion) {
        return "./" + Arrays.asList(pluginVersion.file().split("/"))
                .getLast();
    }

    private InputStream download(PluginVersion pluginVersion, String family) {
        HttpRequest downloadReq = HttpRequest.newBuilder()
                .uri(URI.create(pluginProperties.url("download").formatted(pluginVersion.file(), family)))
                .GET()
                .build();
        try {
            return httpClient.send(
                    downloadReq,
                    HttpResponse.BodyHandlers.ofInputStream()
            ).body();
        } catch (IOException | InterruptedException e) { // TODO handle InterruptedException (CTRL-C)
            throw new RuntimeException(e);
        }

    }

}

