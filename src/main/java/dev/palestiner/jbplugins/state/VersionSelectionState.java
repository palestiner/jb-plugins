package dev.palestiner.jbplugins.state;

import dev.palestiner.jbplugins.model.PluginVersion;
import dev.palestiner.jbplugins.service.selector.SelectorService;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.component.support.Itemable;

public class VersionSelectionState implements SearchPluginState {

    private final SelectorService<PluginVersion> versionSelectorService;
    private final ResourceLoader resourceLoader;

    public VersionSelectionState(SelectorService<PluginVersion> versionSelectorService, ResourceLoader resourceLoader) {
        this.versionSelectorService = versionSelectorService;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public String process(PluginDownloadContext context) {
        var versionContext = versionSelectorService.context(resourceLoader, context.getPluginVersions());
        if (versionContext == null) return "No version selected";
        context.setVersionContext(versionContext);
        PluginVersion pluginVersion = versionContext.getResultItem()
                .map(Itemable::getItem)
                .orElseThrow(() -> new IllegalStateException("No plugin version found"));
        context.setPluginVersion(pluginVersion);
        return null;
    }

    @Override
    public int order() {
        return 4;
    }

}
