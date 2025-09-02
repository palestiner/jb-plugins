package dev.palestiner.jbplugins.state;

import dev.palestiner.jbplugins.model.Plugin;
import dev.palestiner.jbplugins.service.selector.SelectorService;
import org.springframework.core.io.ResourceLoader;

public record PluginSelectionState(
        SelectorService<Plugin> pluginSelectorService,
        ResourceLoader resourceLoader
) implements SearchPluginState {

    @Override
    public String process(PluginDownloadContext context) {
        var pluginContext = pluginSelectorService.context(resourceLoader, context.getPlugins());
        if (pluginContext == null) return "No plugin selected";
        context.setPluginContext(pluginContext);
        return null;
    }

    @Override
    public StateOrder order() {
        return StateOrder.PLUGIN_SELECTION_STATE;
    }

}
