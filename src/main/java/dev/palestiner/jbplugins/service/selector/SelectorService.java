package dev.palestiner.jbplugins.service.selector;

import lombok.extern.slf4j.Slf4j;
import org.jline.terminal.Terminal;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.style.TemplateExecutor;
import org.springframework.stereotype.Component;

import java.io.IOError;
import java.util.List;
import java.util.function.Function;

import static org.springframework.shell.component.SingleItemSelector.SingleItemSelectorContext;

@Slf4j
@Component
public class SelectorService {

    private final Terminal terminal;
    private final TemplateExecutor templateExecutor;

    public SelectorService(Terminal terminal, TemplateExecutor templateExecutor) {
        this.terminal = terminal;
        this.templateExecutor = templateExecutor;
    }

    public <T> SingleItemSelectorContext<T, SelectorItem<T>> select(
            ResourceLoader resourceLoader,
            List<T> items,
            Function<T, String> label,
            String title
    ) {
        var selector = new EmacsSingleItemSelector<>(
                terminal,
                items.stream()
                        .map(item -> SelectorItem.of(label.apply(item), item))
                        .toList(),
                title,
                null
        );
        selector.setResourceLoader(resourceLoader);
        selector.setTemplateExecutor(templateExecutor);
        try {
            return selector.run(SingleItemSelectorContext.empty());
        } catch (IOError e) {
            // TODO Right Ctrl-C handling
            return null;
        } catch (Exception e) {
            log.warn("Selection '{}' failed: {}", title, e.getMessage());
            return null;
        }
    }

}
