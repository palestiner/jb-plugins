package dev.palestiner.jbplugins.state;

import dev.palestiner.jbplugins.model.PluginVersion;
import dev.palestiner.jbplugins.service.selector.SelectorService;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.component.support.Itemable;

public record VersionSelectionState(
        SelectorService selectorService,
        ResourceLoader resourceLoader
) implements SearchPluginState {

    @Override
    public String process(PluginDownloadContext context) {
        var versionContext = selectorService.select(
                resourceLoader,
                context.getPluginVersions(),
                PluginVersion::version,
                "Select version"
        );
        PluginVersion pluginVersion = versionContext == null ? null
                : versionContext.getResultItem()
                        .map(Itemable::getItem)
                        .orElse(null);
        if (pluginVersion == null) return "No version selected";
        context.setSelectedPluginVersion(pluginVersion);
        return null;
    }

    @Override
    public StateOrder order() {
        return StateOrder.VERSION_SELECTION_STATE;
    }

}
