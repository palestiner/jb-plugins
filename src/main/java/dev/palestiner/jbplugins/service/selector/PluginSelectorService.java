package dev.palestiner.jbplugins.service.selector;

import dev.palestiner.jbplugins.model.Plugin;
import org.jline.terminal.Terminal;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.style.TemplateExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.shell.component.SingleItemSelector.SingleItemSelectorContext;

@Component
public class PluginSelectorService extends AbstractSelectorService<Plugin> {

    private final Terminal terminal;
    private final TemplateExecutor templateExecutor;

    public PluginSelectorService(Terminal terminal, TemplateExecutor templateExecutor) {
        this.terminal = terminal;
        this.templateExecutor = templateExecutor;
    }


    @Override
    public SingleItemSelectorContext<Plugin, SelectorItem<Plugin>> context(
            ResourceLoader resourceLoader,
            List<Plugin> items
    ) {
        var selector = new SingleItemSelector<>(
                terminal,
                items.stream()
                        .map(p -> SelectorItem.of(p.name(), p))
                        .toList(),
                "Select plugin",
                null
        );
        selector.setResourceLoader(resourceLoader);
        selector.setTemplateExecutor(templateExecutor);
        return runWithTry(selector);
    }

}
