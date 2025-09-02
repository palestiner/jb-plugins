package dev.palestiner.jbplugins.state;

import dev.palestiner.jbplugins.model.Plugin;
import dev.palestiner.jbplugins.model.PluginVersion;
import lombok.Data;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.commands.Quit;

import java.util.List;

import static org.springframework.shell.component.SingleItemSelector.SingleItemSelectorContext;

@Data
public class PluginDownloadContext {

    private String[] rawArgs;
    private String pattern;
    private List<Plugin> items;
    private SingleItemSelectorContext<Plugin, SelectorItem<Plugin>> pluginContext;
    private List<PluginVersion> pluginVersions;
    private SingleItemSelectorContext<PluginVersion, SelectorItem<PluginVersion>> versionContext;
    private PluginVersion pluginVersion;
    private SingleItemSelectorContext<String, SelectorItem<String>> familyContext;
    private String family;
    private final InteractionMode interactionMode;
    private final Quit quitHandler;

    public PluginDownloadContext(String[] rawArgs, InteractionMode mode, Quit quitHandler) {
        this.rawArgs = rawArgs;
        this.interactionMode = mode;
        this.quitHandler = quitHandler;
    }


}
