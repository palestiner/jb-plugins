package dev.palestiner.jbplugins.command;

import dev.palestiner.jbplugins.model.Plugin;
import dev.palestiner.jbplugins.model.PluginVersion;
import dev.palestiner.jbplugins.service.PluginService;
import dev.palestiner.jbplugins.service.selector.SelectorService;
import dev.palestiner.jbplugins.state.PluginDownloadContext;
import dev.palestiner.jbplugins.state.SearchPluginCommandStateMachine;
import lombok.experimental.Delegate;
import org.springframework.shell.command.CommandContext;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.commands.Quit;
import org.springframework.stereotype.Component;

@Component
public class SearchPlugin extends AbstractShellComponent implements CommandRegistration {

    @Delegate
    private final CommandRegistration origin;
    private final Quit quit;
    private final PluginService pluginService;
    private final SelectorService<Plugin> pluginSelectorService;
    private final SelectorService<PluginVersion> versionSelectorService;
    private final SelectorService<String> ideFamilySelectorService;

    public SearchPlugin(
            Quit quit,
            PluginService pluginService,
            SelectorService<Plugin> pluginSelectorService,
            SelectorService<PluginVersion> versionSelectorService,
            SelectorService<String> ideFamilySelectorService
    ) {
        this.quit = quit;
        this.pluginService = pluginService;
        this.pluginSelectorService = pluginSelectorService;
        this.versionSelectorService = versionSelectorService;
        this.ideFamilySelectorService = ideFamilySelectorService;
        origin = CommandRegistration.builder()
                .command("search")
                .description("Search plugin")
                .withTarget()
                .function(this::processCommand)
                .and()
                .withOption()
                .description("Search pattern")
                .required()
                .and()
                .build();
    }

    private String processCommand(CommandContext ctx) {
        PluginDownloadContext context = new PluginDownloadContext(
                ctx.getRawArgs(),
                ctx.getShellContext().getInteractionMode(),
                quit
        );
        SearchPluginCommandStateMachine stateMachine = SearchPluginCommandStateMachine.create(
                pluginService,
                pluginSelectorService,
                versionSelectorService,
                ideFamilySelectorService,
                getResourceLoader()
        );
        return stateMachine.execute(context);
    }

}
