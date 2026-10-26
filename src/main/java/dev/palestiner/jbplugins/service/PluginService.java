package dev.palestiner.jbplugins.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.palestiner.jbplugins.exception.MarketplaceException;
import dev.palestiner.jbplugins.model.Plugin;
import dev.palestiner.jbplugins.model.PluginVersion;
import dev.palestiner.jbplugins.properties.PluginProperties;
import lombok.extern.slf4j.Slf4j;
import org.jline.terminal.Terminal;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public class PluginService {

    private static final TypeReference<List<PluginVersion>> LIST_PLUGIN_VERSIONS_TYPE = new TypeReference<>() {};
    private static final TypeReference<List<Plugin>> LIST_PLUGINS_TYPE = new TypeReference<>() {};
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);

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
        HttpRequest searchReq = get(url("search").formatted(encode(pattern)));
        try {
            HttpResponse<String> send = httpClient.send(
                    searchReq,
                    HttpResponse.BodyHandlers.ofString()
            );
            requireOk(send, "plugin search");
            JsonNode plugins = objectMapper.readTree(send.body()).get("plugins");
            if (plugins == null || plugins.isNull() || plugins.isMissingNode()) {
                throw new MarketplaceException("Unexpected plugin search response from JetBrains Marketplace");
            }
            return objectMapper.readValue(plugins.toString(), LIST_PLUGINS_TYPE);
        } catch (IOException e) {
            log.warn("Plugin search failed: {}", e.getMessage());
            throw new MarketplaceException("Plugin search failed: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MarketplaceException("Plugin search interrupted", e);
        }
    }

    public List<PluginVersion> pluginVersions(Plugin plugin) {
        HttpRequest versionsReq = get(url("version").formatted(plugin.id()));
        try {
            HttpResponse<String> send = httpClient.send(
                    versionsReq,
                    HttpResponse.BodyHandlers.ofString()
            );
            requireOk(send, "plugin versions");
            return objectMapper.readValue(send.body(), LIST_PLUGIN_VERSIONS_TYPE);
        } catch (IOException e) {
            log.warn("Loading plugin versions failed: {}", e.getMessage());
            throw new MarketplaceException("Failed to load plugin versions: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MarketplaceException("Loading plugin versions interrupted", e);
        }
    }

    public void downloadPlugin(
            PluginVersion pluginVersion,
            String family
    ) {
        String fileName = fileName(pluginVersion);
        Path target = Path.of(fileName);
        Path tempFile = Path.of(fileName + ".part");
        try (InputStream is = download(pluginVersion, family);
             OutputStream os = Files.newOutputStream(
                     tempFile,
                     StandardOpenOption.CREATE,
                     StandardOpenOption.TRUNCATE_EXISTING,
                     StandardOpenOption.WRITE
             )
        ) {
            long totalBytes = pluginVersion.size();
            long downloadedBytes = 0;
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
                downloadedBytes += bytesRead;
                progressBar.display(downloadedBytes, totalBytes);
            }
            move(tempFile, target);
        } catch (IOException e) {
            log.warn("Download of {} failed: {}", fileName, e.getMessage());
            deleteQuietly(tempFile);
            throw new MarketplaceException("Download failed: " + e.getMessage(), e);
        }
        terminal.writer().println("\nPlugin downloaded to " + fileName);
    }

    private InputStream download(PluginVersion pluginVersion, String family) {
        HttpRequest downloadReq = get(url("download").formatted(pluginVersion.file(), family));
        try {
            HttpResponse<InputStream> send = httpClient.send(
                    downloadReq,
                    HttpResponse.BodyHandlers.ofInputStream()
            );
            if (isNotOk(send)) {
                try {
                    send.body().close();
                } catch (IOException ignored) {
                }
                throw new MarketplaceException(
                        "Download failed: JetBrains Marketplace returned HTTP " + send.statusCode()
                );
            }
            return send.body();
        } catch (IOException e) {
            log.warn("Download failed: {}", e.getMessage());
            throw new MarketplaceException("Download failed: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MarketplaceException("Download interrupted", e);
        }
    }

    private void move(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("Failed to delete temporary file {}: {}", path, e.getMessage());
        }
    }

    private String fileName(PluginVersion pluginVersion) {
        String file = pluginVersion.file();
        if (file == null || file.isBlank()) {
            throw new MarketplaceException("Plugin version has no downloadable file");
        }
        int lastSlash = file.lastIndexOf('/');
        return lastSlash >= 0 ? file.substring(lastSlash + 1) : file;
    }

    private HttpRequest get(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(REQUEST_TIMEOUT)
                .header("User-Agent", "jb-plugins")
                .GET()
                .build();
    }

    private String url(String key) {
        String url = pluginProperties.url(key);
        if (url == null) {
            throw new MarketplaceException("No URL configured for '" + key + "'");
        }
        return url;
    }

    private boolean isNotOk(HttpResponse<?> response) {
        int status = response.statusCode();
        return status < 200 || status >= 300;
    }

    private void requireOk(HttpResponse<?> response, String operation) {
        if (isNotOk(response)) {
            throw new MarketplaceException(
                    "JetBrains Marketplace returned HTTP " + response.statusCode() + " for " + operation
            );
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

}
