package com.dtt.organization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import ug.daes.DAESService;
import ug.daes.PKICoreServiceException;
import ug.daes.Result;


/**
 * Organization module bootstrap configuration.
 *
 * REMOVED: restTemplate() @Bean → consolidated in PlatformHttpConfig (common module)
 * REMOVED: signatueServiceInitilize() @Bean → moved to SignatureServiceInitializer @Component below
 */
@Configuration("orgModuleConfig")
public class OrganizationApplication {

    private OrganizationApplication(){
        throw  new UnsupportedOperationException("Utility Class");
    }

    private static final Logger logger = LoggerFactory.getLogger(OrganizationApplication.class);

    @Component("orgSignatureServiceInitializer")
    public static class OrgSignatureServiceInitializer {

        private static final Logger log = LoggerFactory.getLogger(OrgSignatureServiceInitializer.class);

        @Bean
        public void init() {
            try {
                Result result = DAESService.initPKINativeUtils();
                if (result.getStatus() == 0) {
                    log.info("Organization: PKI native utils initialized successfully");
                } else {
                    String error = result.getResponse() != null
                            ? new String(result.getResponse()) : "unknown";
                    log.error("Organization: PKI init failed: {}", error);
                    throw new IllegalStateException("Organization PKI initialization failed");
                }
            } catch (PKICoreServiceException e) {
                throw new IllegalStateException("Organization PKI init error", e);
            }
        }
    }
}
