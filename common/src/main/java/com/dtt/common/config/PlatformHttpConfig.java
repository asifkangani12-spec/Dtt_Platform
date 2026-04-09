package com.dtt.common.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Single shared RestTemplate bean for the entire platform.
 * All module-level restTemplate() @Bean declarations have been removed
 * to eliminate ConflictingBeanDefinitionException.
 */
@Configuration("platformHttpConfig")
public class PlatformHttpConfig {

    @Bean
    public RestTemplate restTemplate() {

        CloseableHttpClient httpClient = HttpClients.custom().build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        requestFactory.setConnectionRequestTimeout(300000);
        requestFactory.setConnectTimeout(300000);

        return new RestTemplate(requestFactory);
    }
}
