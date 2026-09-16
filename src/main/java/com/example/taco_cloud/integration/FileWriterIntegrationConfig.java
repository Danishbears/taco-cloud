package com.example.taco_cloud.integration;

import org.springframework.boot.autoconfigure.ssl.SslProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.support.FileExistsMode;

import java.io.File;

@Configuration
public class FileWriterIntegrationConfig {

    @Bean
    public IntegrationFlow fileWriterFlow() {
        return IntegrationFlow.from("textInChannel")
                .<String, String>transform(String::toUpperCase)
                .handle(Files.outboundAdapter(new File("/tmp/taco_orders"))
                        .fileExistsMode(FileExistsMode.APPEND)
                        .appendNewLine(true))
                .get();
    }
}