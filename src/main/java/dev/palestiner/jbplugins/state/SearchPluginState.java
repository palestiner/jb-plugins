package dev.palestiner.jbplugins.state;

public interface SearchPluginState {

    String process(PluginDownloadContext context);

    int order();

}
