package ug.daes.OnBoardingTransactionHandler.conf;

import org.springframework.context.annotation.Configuration;

/**
 * Transaction handler module configuration placeholder.
 * messageSource() and localeResolver() beans moved to PlatformAppConfiguration (common module).
 * restTemplate() bean moved to PlatformHttpConfig (common module).
 * jasyptStringEncryptor() bean moved to PlatformJasyptConfig (common module).
 */
@Configuration("txHandlerAppConfiguration")
public class AppConfiguration {
    // Intentionally empty — shared beans live in com.dtt.common.config.*
}
