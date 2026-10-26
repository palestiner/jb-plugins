package dev.palestiner.jbplugins.service.selector;

import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.support.SelectorItem;

import java.util.Comparator;
import java.util.List;

import static org.jline.keymap.KeyMap.ctrl;

public class EmacsSingleItemSelector<T> extends SingleItemSelector<T, SelectorItem<T>> {

    public EmacsSingleItemSelector(
        Terminal terminal,
        List<SelectorItem<T>> items,
        String name,
        Comparator<SelectorItem<T>> comparator
    ) {
        super(terminal, items, name, comparator);
    }

    @Override
    protected void bindKeyMap(KeyMap<String> keyMap) {
        super.bindKeyMap(keyMap);
        keyMap.bind(OPERATION_DOWN, ctrl('N'));
        keyMap.bind(OPERATION_UP, ctrl('P'));
    }

}