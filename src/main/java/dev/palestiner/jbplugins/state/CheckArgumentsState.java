package dev.palestiner.jbplugins.state;

import dev.palestiner.jbplugins.model.Plugin;
import dev.palestiner.jbplugins.service.PluginService;

import java.util.Arrays;
import java.util.List;

public record CheckArgumentsState(PluginService pluginService) implements SearchPluginState {

    @Override
    public String process(PluginDownloadContext context) {
        String pattern = searchPattern(context);
        if (pattern == null || pattern.isBlank()) return "Enter search pattern";
        pattern = pattern.trim();
        context.setSearchPattern(pattern);
        List<Plugin> items = pluginService.search(pattern);
        if (items.isEmpty()) return "No items found";
        context.setPlugins(items);
        return null;
    }

    private String searchPattern(PluginDownloadContext context) {
        String[] rawArgs = context.getCommandContext().getRawArgs();
        return rawArgs.length > 1
                ? String.join(" ", Arrays.copyOfRange(rawArgs, 1, rawArgs.length))
                : null;
    }

    @Override
    public StateOrder order() {
        return StateOrder.CHECK_ARGUMENTS_STATE;
    }

}
