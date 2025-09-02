package dev.palestiner.jbplugins.state;

import dev.palestiner.jbplugins.service.PluginService;

public record CheckArgumentsState(PluginService pluginService) implements SearchPluginState {

    @Override
    public String process(PluginDownloadContext context) {
        String[] rawArgs = context.getCommandContext().getRawArgs();
        if (rawArgs.length < 2) return "Enter search pattern";
        String pattern = rawArgs[1];
        context.setSearchPattern(pattern);
        var items = pluginService.search(pattern);
        if (items.isEmpty()) return "No items found";
        context.setPlugins(items);
        return null;
    }

    @Override
    public StateOrder order() {
        return StateOrder.CHECK_ARGUMENTS_STATE;
    }

}
