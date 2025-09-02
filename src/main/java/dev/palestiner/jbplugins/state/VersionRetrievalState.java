package dev.palestiner.jbplugins.state;

import dev.palestiner.jbplugins.model.PluginVersion;
import dev.palestiner.jbplugins.service.PluginService;
import org.springframework.shell.component.support.Itemable;

import java.util.List;

public record VersionRetrievalState(PluginService pluginService) implements SearchPluginState {

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
    public StateOrder order() {
        return StateOrder.VERSION_RETRIEVAL_STATE;
    }

}
