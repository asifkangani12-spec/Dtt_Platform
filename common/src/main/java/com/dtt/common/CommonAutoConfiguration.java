package com.dtt.common;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration entry point for the common module.
 * Ensures all beans in com.dtt.common.* are discovered when
 * this module is on the classpath.
 */
@Configuration
@ComponentScan(basePackages = "com.dtt.common")
public class CommonAutoConfiguration {
}
