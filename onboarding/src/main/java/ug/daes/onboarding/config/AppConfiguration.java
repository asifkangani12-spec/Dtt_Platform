package ug.daes.onboarding.config;

import org.springframework.context.annotation.Configuration;

/**
 * Onboarding module configuration placeholder.
 * messageSource() and localeResolver() moved to PlatformAppConfiguration (common module).
 */
@Configuration("onboardingAppConfiguration")
public class AppConfiguration {
    // Intentionally empty — shared beans live in com.dtt.common.config.PlatformAppConfiguration
}
