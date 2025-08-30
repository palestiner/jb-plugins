package dev.palestiner.jbplugins.command;

import dev.palestiner.jbplugins.model.Plugin;
import dev.palestiner.jbplugins.model.PluginVersion;
import dev.palestiner.jbplugins.service.DownloadProgressBar;
import dev.palestiner.jbplugins.service.PluginService;
import lombok.experimental.Delegate;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.support.Itemable;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.commands.Quit;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.springframework.shell.component.SingleItemSelector.SingleItemSelectorContext;

@Component
public class SearchPlugin extends AbstractShellComponent implements CommandRegistration {

    @Delegate
    private final CommandRegistration origin;
    private final PluginService pluginService;
    private final DownloadProgressBar progressBar;

    public SearchPlugin(PluginService pluginService, Quit quit, DownloadProgressBar progressBar) {
        this.progressBar = progressBar;
        this.pluginService = pluginService;
        origin = CommandRegistration.builder()
                .command("search")
                .description("Search plugin")
                .withTarget()
                .function(ctx -> {
                    String[] rawArgs = ctx.getRawArgs();
                    if (rawArgs.length < 2) return "Enter search pattern";
                    String pattern = rawArgs[1];
                    var items = pluginService.search(pattern);
                    if (items.isEmpty()) return "No items found";
                    var pluginSelector = getPluginSelector(items);
                    var pluginContext = runWithTry(pluginSelector);
                    if (pluginContext == null) return "No plugin selected";
                    List<PluginVersion> pluginVersions = pluginContext.getResultItem()
                            .map(Itemable::getItem)
                            .map(pluginService::pluginVersions)
                            .orElse(List.of());
                    if (pluginVersions.isEmpty()) return "No plugins available";
                    var versionSelector = getVersionSelector(pluginVersions);
                    var versionContext = runWithTry(versionSelector);
                    if (versionContext == null) return "No version selected";
                    PluginVersion pluginVersion = versionContext.getResultItem()
                            .map(Itemable::getItem)
                            .orElseThrow(() -> new IllegalStateException("No plugin version found"));
                    var familySelector = getFamilySelector(pluginVersion);
                    var familyContext = runWithTry(familySelector);
                    if (familyContext == null) return "No IDE selected";
                    String family = familyContext.getResultItem()
                            .map(Itemable::getItem)
                            .orElseThrow(() -> new IllegalStateException("No IDE found"));
                    downloadPlugin(pluginVersion, family);
                    InteractionMode mode = ctx.getShellContext().getInteractionMode();
                    if (mode.equals(InteractionMode.INTERACTIVE)) {
                        quit.quit();
                    }
                    return "";
                })
                .and()
                .withOption()
                .description("Search pattern")
                .required()
                .and()
                .build();
    }

    private SingleItemSelector<Plugin, SelectorItem<Plugin>> getPluginSelector(List<Plugin> items) {
        var component = new SingleItemSelector<>(
                getTerminal(),
                items.stream()
                        .map(p -> SelectorItem.of(p.name(), p))
                        .toList(),
                "Select plugin",
                null
        );
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        return component;
    }

    private SingleItemSelector<PluginVersion, SelectorItem<PluginVersion>> getVersionSelector(List<PluginVersion> pluginVersions) {
        var component = new SingleItemSelector<>(
                getTerminal(),
                pluginVersions.stream()
                        .map(pv -> SelectorItem.of(pv.version(), pv))
                        .toList(),
                "Select version",
                null
        );
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        return component;
    }

    private SingleItemSelector<String, SelectorItem<String>> getFamilySelector(PluginVersion pluginVersion) {
        Iterable<String> names = () -> pluginVersion.compatibleVersions().fieldNames();
        List<SelectorItem<String>> families = StreamSupport
                .stream(names.spliterator(), false)
                .map(fieldName -> SelectorItem.of(fieldName, fieldName))
                .toList();
        var component = new SingleItemSelector<>(
                getTerminal(),
                families,
                "Select IDE",
                null
        );
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        return component;
    }

    private String fileName(PluginVersion pluginVersion) {
        return "./" + Arrays.asList(pluginVersion.file().split("/"))
                .getLast();
    }

    private <T, I extends SelectorItem<T>> SingleItemSelectorContext<T, I> runWithTry(SingleItemSelector<T, I> selector) {
        try {
            return selector.run(SingleItemSelector.SingleItemSelectorContext.empty());
        } catch (Throwable ignored) {
        }
        return null;
    }

    private void downloadPlugin(
            PluginVersion pluginVersion,
            String family
    ) {
        String fileName = fileName(pluginVersion);
        try (InputStream is = pluginService.download(pluginVersion, family);
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
            throw new RuntimeException(e);
        }
        System.out.println("\nPlugin downloaded to " + fileName);
    }

}
