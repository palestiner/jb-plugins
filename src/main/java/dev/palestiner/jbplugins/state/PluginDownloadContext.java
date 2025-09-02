package dev.palestiner.jbplugins.state;

import dev.palestiner.jbplugins.model.Plugin;
import dev.palestiner.jbplugins.model.PluginVersion;
import lombok.Data;
import org.springframework.shell.command.CommandContext;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.commands.Quit;

import java.util.List;

import static org.springframework.shell.component.SingleItemSelector.SingleItemSelectorContext;

@Data
public class PluginDownloadContext {

    private final Quit quit;
    private final CommandContext commandContext;

    private String family;
    private String searchPattern;
    private List<Plugin> plugins;
    private List<PluginVersion> pluginVersions;
    private PluginVersion selectedPluginVersion;
    private SingleItemSelectorContext<Plugin, SelectorItem<Plugin>> pluginContext;
    private SingleItemSelectorContext<PluginVersion, SelectorItem<PluginVersion>> versionContext;
    private SingleItemSelectorContext<String, SelectorItem<String>> familyContext;

    public PluginDownloadContext(CommandContext commandContext, Quit quit) {
        this.commandContext = commandContext;
        this.quit = quit;
    }


}
