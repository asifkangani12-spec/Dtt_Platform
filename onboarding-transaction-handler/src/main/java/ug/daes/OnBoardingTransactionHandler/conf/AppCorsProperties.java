package ug.daes.OnBoardingTransactionHandler.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.cors")
public class AppCorsProperties {

    private String allowedOrigins;
    private String allowedHeaders;
    private String allowedMethods;

    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public String getAllowedHeaders() {
        return allowedHeaders;
    }

    public void setAllowedHeaders(String allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    public String getAllowedMethods() {
        return allowedMethods;
    }

    public void setAllowedMethods(String allowedMethods) {
        this.allowedMethods = allowedMethods;
    }
}
