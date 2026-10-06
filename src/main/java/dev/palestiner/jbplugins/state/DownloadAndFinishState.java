package dev.palestiner.jbplugins.state;

import dev.palestiner.jbplugins.service.PluginService;

public record DownloadAndFinishState(PluginService pluginService) implements SearchPluginState {

    @Override
    public String process(PluginDownloadContext context) {
        pluginService.downloadPlugin(context.getSelectedPluginVersion(), context.getFamily());
        return "";
    }

    @Override
    public StateOrder order() {
        return StateOrder.DOWNLOAD_AND_FINISH_STATE;
    }

}
