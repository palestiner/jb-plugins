package dev.palestiner.jbplugins.state;

import dev.palestiner.jbplugins.model.PluginVersion;
import dev.palestiner.jbplugins.service.selector.SelectorService;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.component.support.Itemable;

import java.util.List;
import java.util.stream.StreamSupport;

public record FamilySelectionState(
        SelectorService<String> ideFamilySelectorService,
        ResourceLoader resourceLoader
) implements SearchPluginState {

    @Override
    public String process(PluginDownloadContext context) {
        var familyContext = ideFamilySelectorService.context(
                resourceLoader,
                extractFamily(context.getSelectedPluginVersion())
        );
        if (familyContext == null) return "No IDE selected";
        context.setFamilyContext(familyContext);
        String family = familyContext.getResultItem()
                .map(Itemable::getItem)
                .orElseThrow(() -> new IllegalStateException("No IDE found"));
        context.setFamily(family);
        return null;
    }

    @Override
    public StateOrder order() {
        return StateOrder.FAMILY_SELECTION_STATE;
    }

    private List<String> extractFamily(PluginVersion pluginVersion) {
        Iterable<String> names = () -> pluginVersion.compatibleVersions().fieldNames();
        return StreamSupport.stream(names.spliterator(), false)
                .sorted(String::compareTo)
                .toList();
    }

}
