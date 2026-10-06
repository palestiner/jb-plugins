package dev.palestiner.jbplugins.state;

import dev.palestiner.jbplugins.model.Plugin;
import dev.palestiner.jbplugins.model.PluginVersion;
import lombok.Data;
import org.springframework.shell.command.CommandContext;

import java.util.List;

@Data
public class PluginDownloadContext {

    private final CommandContext commandContext;

    private List<Plugin> plugins;

    private List<PluginVersion> pluginVersions;

    private Plugin selectedPlugin;

    private PluginVersion selectedPluginVersion;

    private String family;

    public PluginDownloadContext(CommandContext commandContext) {
        this.commandContext = commandContext;
    }

}
