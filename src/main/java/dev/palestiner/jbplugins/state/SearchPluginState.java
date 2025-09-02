package dev.palestiner.jbplugins.state;

public interface SearchPluginState extends OrderedState {

    String process(PluginDownloadContext context);

}
