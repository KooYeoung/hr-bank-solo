package com.kooyeoung.hrbank.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "hrbank.employee-number")
public record EmployeeNumberProperties(
        @DefaultValue("4")
        int sequenceWidth
) { }
