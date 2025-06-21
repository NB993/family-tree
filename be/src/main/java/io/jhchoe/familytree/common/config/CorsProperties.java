package io.jhchoe.familytree.common.config;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("cors")
public class CorsProperties {

    private final List<String> allowedOrigins;
}
