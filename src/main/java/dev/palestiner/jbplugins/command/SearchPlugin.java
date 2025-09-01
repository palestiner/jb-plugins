package dev.palestiner.jbplugins.command;

import dev.palestiner.jbplugins.model.Plugin;
import dev.palestiner.jbplugins.model.PluginVersion;
import dev.palestiner.jbplugins.service.DownloadProgressBar;
import dev.palestiner.jbplugins.service.PluginService;
import dev.palestiner.jbplugins.service.selector.SelectorService;
import lombok.experimental.Delegate;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.component.support.Itemable;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.commands.Quit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

@Component
public class SearchPlugin extends AbstractShellComponent implements CommandRegistration {

    @Delegate
    private final CommandRegistration origin;

    public SearchPlugin(
            PluginService pluginService,
            Quit quit,
            SelectorService<Plugin> pluginSelectorService,
            SelectorService<PluginVersion> versionSelectorService,
            SelectorService<String> ideFamilySelectorService
    ) {
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
                    var pluginContext = pluginSelectorService.context(getResourceLoader(), items);
                    if (pluginContext == null) return "No plugin selected";
                    List<PluginVersion> pluginVersions = pluginContext.getResultItem()
                            .map(Itemable::getItem)
                            .map(pluginService::pluginVersions)
                            .orElse(List.of());
                    if (pluginVersions.isEmpty()) return "No plugins available";
                    var versionContext = versionSelectorService.context(getResourceLoader(), pluginVersions);
                    if (versionContext == null) return "No version selected";
                    PluginVersion pluginVersion = versionContext.getResultItem()
                            .map(Itemable::getItem)
                            .orElseThrow(() -> new IllegalStateException("No plugin version found"));
                    var familyContext = ideFamilySelectorService.context(
                            getResourceLoader(),
                            extractFamily(pluginVersion)
                    );
                    if (familyContext == null) return "No IDE selected";
                    String family = familyContext.getResultItem()
                            .map(Itemable::getItem)
                            .orElseThrow(() -> new IllegalStateException("No IDE found"));
                    pluginService.downloadPlugin(pluginVersion, family);
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

    private List<String> extractFamily(PluginVersion pluginVersion) {
        Iterable<String> names = () -> pluginVersion.compatibleVersions().fieldNames();
        return StreamSupport.stream(names.spliterator(), false)
                .toList();
    }

}
