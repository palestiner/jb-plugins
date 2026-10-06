package dev.palestiner.jbplugins.state;

import com.fasterxml.jackson.databind.JsonNode;
import dev.palestiner.jbplugins.model.PluginVersion;
import dev.palestiner.jbplugins.service.selector.SelectorService;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.component.support.Itemable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

public record FamilySelectionState(
        SelectorService selectorService,
        ResourceLoader resourceLoader
) implements SearchPluginState {

    @Override
    public String process(PluginDownloadContext context) {
        List<String> families = extractFamilies(context.getSelectedPluginVersion());
        if (families.isEmpty()) return "No compatible IDEs found";
        var familyContext = selectorService.select(
                resourceLoader,
                families,
                family -> family,
                "Select IDE"
        );
        String family = familyContext == null ? null
                : familyContext.getResultItem()
                        .map(Itemable::getItem)
                        .orElse(null);
        if (family == null) return "No IDE selected";
        context.setFamily(family);
        return null;
    }

    @Override
    public StateOrder order() {
        return StateOrder.FAMILY_SELECTION_STATE;
    }

    private List<String> extractFamilies(PluginVersion pluginVersion) {
        JsonNode compatibleVersions = pluginVersion.compatibleVersions();
        if (compatibleVersions == null || compatibleVersions.isNull() || compatibleVersions.isMissingNode()) {
            return List.of();
        }
        Iterable<String> names = () -> compatibleVersions.fieldNames();
        return StreamSupport.stream(names.spliterator(), false)
                .sorted(Comparator.naturalOrder())
                .toList();
    }

}
