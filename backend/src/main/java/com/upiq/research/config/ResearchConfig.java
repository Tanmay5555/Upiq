package com.upiq.research.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * EXPERIMENTAL: research module configuration. Does not alter production beans.
 */
@Configuration
@EnableConfigurationProperties(ResearchSeedingProperties.class)
public class ResearchConfig {
}
