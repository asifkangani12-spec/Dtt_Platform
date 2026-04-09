package com.dtt.common.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

/**
 * Single authoritative source for MessageSource and LocaleResolver beans.
 * All module-level AppConfiguration classes have had these @Bean methods removed
 * to eliminate ConflictingBeanDefinitionException on startup.
 */
@Configuration("platformAppConfiguration")
public class PlatformAppConfiguration {

    @Primary
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasenames(
                "classpath:messages/api_error_messages",
                "classpath:messages/api_response_messages"
        );
        ms.setDefaultEncoding("UTF-8");
        ms.setFallbackToSystemLocale(false);
        return ms;
    }

    @Primary
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }
}
