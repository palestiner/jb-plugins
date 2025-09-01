package dev.palestiner.jbplugins.service.selector;

import org.jline.terminal.Terminal;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.support.Itemable;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.style.TemplateExecutor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class IdeFamilySelectorService extends AbstractSelectorService<String> {

    private final Terminal terminal;
    private final TemplateExecutor templateExecutor;

    public IdeFamilySelectorService(Terminal terminal, TemplateExecutor templateExecutor) {
        this.terminal = terminal;
        this.templateExecutor = templateExecutor;
    }


    @Override
    public SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context(
            ResourceLoader resourceLoader,
            List<String> items
    ) {
        var selector = new SingleItemSelector<>(
                terminal,
                items.stream()
                        .map(fieldName -> SelectorItem.of(fieldName, fieldName))
                        .toList(),
                "Select IDE",
                Comparator.comparing(Itemable::getItem)
        );
        selector.setResourceLoader(resourceLoader);
        selector.setTemplateExecutor(templateExecutor);
        return runWithTry(selector);
    }

}
