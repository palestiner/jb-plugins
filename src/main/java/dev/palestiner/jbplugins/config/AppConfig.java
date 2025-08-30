package dev.palestiner.jbplugins.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import dev.palestiner.jbplugins.properties.PluginProperties;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.jline.PromptProvider;

@Configuration
public class AppConfig {

    @Bean
    @ConfigurationProperties(prefix = "plugin")
    public PluginProperties pluginProperties() {
        return new PluginProperties();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new ParameterNamesModule());
    }

    @Bean
    public PromptProvider myPromptProvider() {
        return () -> new AttributedString("jb-plugins:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }


}
