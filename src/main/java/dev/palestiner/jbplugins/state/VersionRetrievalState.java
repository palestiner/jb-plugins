package dev.palestiner.jbplugins.state;

import dev.palestiner.jbplugins.model.PluginVersion;
import dev.palestiner.jbplugins.service.PluginService;
import org.springframework.shell.component.support.Itemable;

import java.util.List;

public class VersionRetrievalState implements SearchPluginState {

    private final PluginService pluginService;

    public VersionRetrievalState(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Override
    public String process(PluginDownloadContext context) {
        List<PluginVersion> pluginVersions = context.getPluginContext().getResultItem()
                .map(Itemable::getItem)
                .map(pluginService::pluginVersions)
                .orElse(List.of());
        if (pluginVersions.isEmpty()) return "No plugins available";
        context.setPluginVersions(pluginVersions);
        return null;
    }

    @Override
    public int order() {
        return 3;
    }

}
