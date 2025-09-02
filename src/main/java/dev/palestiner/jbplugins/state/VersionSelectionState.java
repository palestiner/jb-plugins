package dev.palestiner.jbplugins.state;

import dev.palestiner.jbplugins.model.PluginVersion;
import dev.palestiner.jbplugins.service.selector.SelectorService;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.component.support.Itemable;

public record VersionSelectionState(
        SelectorService<PluginVersion> versionSelectorService,
        ResourceLoader resourceLoader
) implements SearchPluginState {

    @Override
    public String process(PluginDownloadContext context) {
        var versionContext = versionSelectorService.context(resourceLoader, context.getPluginVersions());
        if (versionContext == null) return "No version selected";
        context.setVersionContext(versionContext);
        PluginVersion pluginVersion = versionContext.getResultItem()
                .map(Itemable::getItem)
                .orElseThrow(() -> new IllegalStateException("No plugin version found"));
        context.setSelectedPluginVersion(pluginVersion);
        return null;
    }

    @Override
    public StateOrder order() {
        return StateOrder.VERSION_SELECTION_STATE;
    }

}
