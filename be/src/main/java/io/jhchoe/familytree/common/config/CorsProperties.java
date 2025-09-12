package io.jhchoe.familytree.common.config;

import java.util.List;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties("cors")
public class CorsProperties {

    private final List<String> allowedOrigins;
    private final String frontendUrl;

    public CorsProperties(List<String> allowedOrigins, String frontendUrl) {
        this.allowedOrigins = allowedOrigins;
        this.frontendUrl = frontendUrl;
    }
}
