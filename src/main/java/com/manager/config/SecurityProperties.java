package com.manager.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties("security")
@Component
@Data
@Validated
public class SecurityProperties {
    @NotBlank
    @Size(min = 32)
    private String secret;
    @DurationUnit(ChronoUnit.DAYS)
    Duration tokenTtl;
}