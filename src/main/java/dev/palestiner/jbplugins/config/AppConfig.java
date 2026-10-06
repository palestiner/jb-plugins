package dev.palestiner.jbplugins.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import dev.palestiner.jbplugins.properties.PluginProperties;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.jline.PromptProvider;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;

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
    public PromptProvider promptProvider() {
        return () -> new AttributedString("jb-plugins:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }

    @Bean
    public HttpClient httpClient(@Value("${plugin.http.insecure:false}") boolean insecure) {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10));
        if (insecure) {
            builder.sslContext(trustAllSslContext());
        }
        return builder.build();
    }

    private SSLContext trustAllSslContext() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            return sc;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalStateException("Failed to create insecure SSL context", e);
        }
    }

}
