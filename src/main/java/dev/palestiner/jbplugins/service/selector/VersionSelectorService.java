package dev.palestiner.jbplugins.service.selector;

import dev.palestiner.jbplugins.model.PluginVersion;
import org.jline.terminal.Terminal;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.style.TemplateExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.shell.component.SingleItemSelector.SingleItemSelectorContext;

@Component
public class VersionSelectorService extends AbstractSelectorService<PluginVersion> {

    private final Terminal terminal;
    private final TemplateExecutor templateExecutor;

    public VersionSelectorService(Terminal terminal, TemplateExecutor templateExecutor) {
        this.terminal = terminal;
        this.templateExecutor = templateExecutor;
    }

    @Override
    public SingleItemSelectorContext<PluginVersion, SelectorItem<PluginVersion>> context(
            ResourceLoader resourceLoader,
            List<PluginVersion> items
    ) {
        var selector = new SingleItemSelector<>(
                terminal,
                items.stream()
                        .map(pv -> SelectorItem.of(pv.version(), pv))
                        .toList(),
                "Select version",
                null
        );
        selector.setResourceLoader(resourceLoader);
        selector.setTemplateExecutor(templateExecutor);
        return runWithTry(selector);
    }

}
