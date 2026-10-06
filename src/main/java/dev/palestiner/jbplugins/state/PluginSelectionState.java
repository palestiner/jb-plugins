package dev.palestiner.jbplugins.state;

import dev.palestiner.jbplugins.model.Plugin;
import dev.palestiner.jbplugins.service.selector.SelectorService;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.component.support.Itemable;

public record PluginSelectionState(
        SelectorService selectorService,
        ResourceLoader resourceLoader
) implements SearchPluginState {

    @Override
    public String process(PluginDownloadContext context) {
        var pluginContext = selectorService.select(
                resourceLoader,
                context.getPlugins(),
                Plugin::name,
                "Select plugin"
        );
        Plugin plugin = pluginContext == null ? null
                : pluginContext.getResultItem()
                        .map(Itemable::getItem)
                        .orElse(null);
        if (plugin == null) return "No plugin selected";
        context.setSelectedPlugin(plugin);
        return null;
    }

    @Override
    public StateOrder order() {
        return StateOrder.PLUGIN_SELECTION_STATE;
    }

}
