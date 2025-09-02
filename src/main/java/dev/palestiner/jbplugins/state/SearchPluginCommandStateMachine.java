package dev.palestiner.jbplugins.state;

import dev.palestiner.jbplugins.model.Plugin;
import dev.palestiner.jbplugins.model.PluginVersion;
import dev.palestiner.jbplugins.service.PluginService;
import dev.palestiner.jbplugins.service.selector.SelectorService;
import org.springframework.core.io.ResourceLoader;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public record SearchPluginCommandStateMachine(List<SearchPluginState> states) {

    public static SearchPluginCommandStateMachine create(
            PluginService pluginService,
            SelectorService<Plugin> pluginSelectorService,
            SelectorService<PluginVersion> versionSelectorService,
            SelectorService<String> ideFamilySelectorService,
            ResourceLoader resourceLoader
    ) {
        List<SearchPluginState> searchPluginStates = Arrays.asList(
                new CheckArgumentsState(pluginService),
                new PluginSelectionState(pluginSelectorService, resourceLoader),
                new VersionRetrievalState(pluginService),
                new VersionSelectionState(versionSelectorService, resourceLoader),
                new FamilySelectionState(ideFamilySelectorService, resourceLoader),
                new DownloadAndFinishState(pluginService)
        );
        searchPluginStates.sort(Comparator.comparing(sps -> sps.order().value()));
        return new SearchPluginCommandStateMachine(searchPluginStates);
    }

    public String execute(PluginDownloadContext context) {
        for (SearchPluginState state : states) {
            String result = state.process(context);
            if (result != null) return result;
        }
        throw new IllegalStateException("Illegal search command state");
    }

}
