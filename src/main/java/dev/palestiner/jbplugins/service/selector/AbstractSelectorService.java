package dev.palestiner.jbplugins.service.selector;

import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.support.SelectorItem;

import static org.springframework.shell.component.SingleItemSelector.SingleItemSelectorContext;

public abstract class AbstractSelectorService<T> implements SelectorService<T> {

    @Override
    public final <I extends SelectorItem<T>> SingleItemSelectorContext<T, I> runWithTry(
            SingleItemSelector<T, I> selector
    ) {
        return SelectorService.super.runWithTry(selector);
    }

}
