package ug.daes.OnBoardingTransactionHandler;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

/**
 * OnBoarding Transaction Handler module bootstrap configuration.
 *
 * The static restTemplate field that previously existed here has been replaced
 * with a Spring-managed instance field. All services that previously used
 *   OnBoardingTransactionHandlerApplication.restTemplate   (static access)
 *   application.restTemplate                               (instance access)
 * should now inject RestTemplate directly via @Autowired / constructor injection.
 *
 * The field is retained here ONLY as a transitional bridge for services that are
 * still being migrated. New code must use constructor injection.
 *
 * REMOVED: restTemplate() @Bean → PlatformHttpConfig (common module)
 * REMOVED: jasyptStringEncryptor() @Bean → PlatformJasyptConfig (common module)
 */

@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@SpringBootApplication(scanBasePackages = {
        "ug.daes.OnBoardingTransactionHandler"
})
@EnableAsync
//@SpringBootApplication
public class OnBoardingTransactionHandlerApplication {

    /**
     * Spring-managed RestTemplate injected from PlatformHttpConfig (common module).
     * Public so that legacy service code using
     *   OnBoardingTransactionHandlerApplication.restTemplate  can still compile
     * until each service is migrated to constructor injection.
     *
     * The @Autowired instance is also assigned to the static reference so that
     * both access patterns resolve to the same bean.
     */

    private final RestTemplate restTemplateInstance;

    /**
     * Static reference — kept for backward compatibility with service code that
     * accesses it as OnBoardingTransactionHandlerApplication.restTemplate.
     * Set during bean post-construction via the @Autowired instance above.
     */
    private static RestTemplate restTemplate;

    public OnBoardingTransactionHandlerApplication(RestTemplate restTemplate) {
        this.restTemplateInstance = restTemplate;
    }

    private static synchronized void setStaticRestTemplate(RestTemplate restTemplate) {
        OnBoardingTransactionHandlerApplication.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        setStaticRestTemplate(this.restTemplateInstance);
    }


    public static RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(OnBoardingTransactionHandlerApplication.class, args);
    }
}
