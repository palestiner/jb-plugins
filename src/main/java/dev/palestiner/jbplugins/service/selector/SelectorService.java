package dev.palestiner.jbplugins.service.selector;

import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.support.SelectorItem;

import java.util.List;

import static org.springframework.shell.component.SingleItemSelector.SingleItemSelectorContext;

public interface SelectorService<T> {

    SingleItemSelectorContext<T, SelectorItem<T>> context(ResourceLoader resourceLoader, List<T> items);

    default <I extends SelectorItem<T>> SingleItemSelectorContext<T, I> runWithTry(SingleItemSelector<T, I> selector) {
        try {
            return selector.run(SingleItemSelector.SingleItemSelectorContext.empty());
        } catch (Throwable ignored) {
        }
        return null;
    }

}
