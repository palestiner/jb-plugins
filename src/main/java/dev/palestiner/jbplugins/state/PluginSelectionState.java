package dev.palestiner.jbplugins.state;

import dev.palestiner.jbplugins.model.Plugin;
import dev.palestiner.jbplugins.service.selector.PluginSelectorService;
import dev.palestiner.jbplugins.service.selector.SelectorService;
import org.springframework.core.io.ResourceLoader;

public class PluginSelectionState implements SearchPluginState {

    private final SelectorService<Plugin> pluginSelectorService;
    private final ResourceLoader resourceLoader;

    public PluginSelectionState(SelectorService<Plugin> pluginSelectorService, ResourceLoader resourceLoader) {
        this.pluginSelectorService = pluginSelectorService;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public String process(PluginDownloadContext context) {
        var pluginContext = pluginSelectorService.context(resourceLoader, context.getItems());
        if (pluginContext == null) return "No plugin selected";
        context.setPluginContext(pluginContext);
        return null;
    }

    @Override
    public int order() {
        return 2;
    }

}
