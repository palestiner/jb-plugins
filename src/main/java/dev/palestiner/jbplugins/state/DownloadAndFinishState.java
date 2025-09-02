package dev.palestiner.jbplugins.state;

import dev.palestiner.jbplugins.service.PluginService;
import org.springframework.shell.context.InteractionMode;

public record DownloadAndFinishState(PluginService pluginService) implements SearchPluginState {

    @Override
    public String process(PluginDownloadContext context) {
        pluginService.downloadPlugin(context.getSelectedPluginVersion(), context.getFamily());
        if (context.getCommandContext().getShellContext().getInteractionMode().equals(InteractionMode.INTERACTIVE)) {
            context.getQuit().quit();
        }
        return "";
    }

    @Override
    public StateOrder order() {
        return StateOrder.DOWNLOAD_AND_FINISH_STATE;
    }

}
