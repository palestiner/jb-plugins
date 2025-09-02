package dev.palestiner.jbplugins.state;

import dev.palestiner.jbplugins.service.PluginService;
import org.springframework.shell.context.InteractionMode;

public class DownloadAndFinishState implements SearchPluginState {

    private final PluginService pluginService;

    public DownloadAndFinishState(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Override
    public String process(PluginDownloadContext context) {
        pluginService.downloadPlugin(context.getPluginVersion(), context.getFamily());
        if (context.getInteractionMode().equals(InteractionMode.INTERACTIVE)) {
            context.getQuitHandler().quit();
        }
        return "";
    }

    @Override
    public int order() {
        return 6;
    }

}
