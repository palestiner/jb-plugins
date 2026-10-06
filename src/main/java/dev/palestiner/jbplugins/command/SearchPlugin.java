package dev.palestiner.jbplugins.command;

import dev.palestiner.jbplugins.exception.MarketplaceException;
import dev.palestiner.jbplugins.service.PluginService;
import dev.palestiner.jbplugins.service.selector.SelectorService;
import dev.palestiner.jbplugins.state.PluginDownloadContext;
import dev.palestiner.jbplugins.state.SearchPluginCommandStateMachine;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.command.CommandContext;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.commands.Quit;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SearchPlugin extends AbstractShellComponent implements CommandRegistration {

    @Delegate
    private final CommandRegistration origin;
    private final Quit quit;
    private final PluginService pluginService;
    private final SelectorService selectorService;

    public SearchPlugin(
            Quit quit,
            PluginService pluginService,
            SelectorService selectorService
    ) {
        this.quit = quit;
        this.pluginService = pluginService;
        this.selectorService = selectorService;
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
        var context = new PluginDownloadContext(ctx, quit);
        try {
            var stateMachine = SearchPluginCommandStateMachine.create(
                    pluginService,
                    selectorService,
                    getResourceLoader()
            );
            return stateMachine.execute(context);
        } catch (MarketplaceException e) {
            log.warn("Marketplace error: {}", e.getMessage());
            return e.getMessage();
        } catch (Exception e) {
            log.error("Unexpected error", e);
            return "Unexpected error: " + e.getMessage();
        }
    }

}
