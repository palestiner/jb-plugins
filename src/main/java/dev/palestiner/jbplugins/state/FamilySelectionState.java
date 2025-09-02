package dev.palestiner.jbplugins.state;

import dev.palestiner.jbplugins.model.PluginVersion;
import dev.palestiner.jbplugins.service.selector.IdeFamilySelectorService;
import dev.palestiner.jbplugins.service.selector.SelectorService;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.component.support.Itemable;

import java.util.List;
import java.util.stream.StreamSupport;

public class FamilySelectionState implements SearchPluginState {

    private final SelectorService<String> ideFamilySelectorService;
    private final ResourceLoader resourceLoader;

    public FamilySelectionState(SelectorService<String> ideFamilySelectorService, ResourceLoader resourceLoader) {
        this.ideFamilySelectorService = ideFamilySelectorService;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public String process(PluginDownloadContext context) {
        var familyContext = ideFamilySelectorService.context(
                resourceLoader,
                extractFamily(context.getPluginVersion())
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
    public int order() {
        return 5;
    }

    private List<String> extractFamily(PluginVersion pluginVersion) {
        Iterable<String> names = () -> pluginVersion.compatibleVersions().fieldNames();
        return StreamSupport.stream(names.spliterator(), false)
                .sorted(String::compareTo)
                .toList();
    }

}
