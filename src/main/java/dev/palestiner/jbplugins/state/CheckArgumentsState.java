package dev.palestiner.jbplugins.state;

import dev.palestiner.jbplugins.service.PluginService;

public class CheckArgumentsState implements SearchPluginState {

    private final PluginService pluginService;

    public CheckArgumentsState(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Override
    public String process(PluginDownloadContext context) {
        String[] rawArgs = context.getRawArgs();
        if (rawArgs.length < 2) return "Enter search pattern";
        String pattern = rawArgs[1];
        context.setPattern(pattern);
        var items = pluginService.search(pattern);
        if (items.isEmpty()) return "No items found";
        context.setItems(items);
        return null;
    }

    @Override
    public int order() {
        return 1;
    }

}
