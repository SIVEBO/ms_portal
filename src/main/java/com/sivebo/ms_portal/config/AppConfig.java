package com.sivebo.ms_portal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
        
        @Value("${ms.tracking.url}")
        private String trackingBaseUrl;

        @Value("${ms.clientes.url}")
        private String clientesBaseUrl;

        @Bean
        public WebClient.Builder webClientBuilder() {
                return WebClient.builder();
        }

        @Bean
        public WebClient trackingWebClient(WebClient.Builder webClientBuilder) {
                return webClientBuilder
                        .baseUrl(trackingBaseUrl)
                        .build();
        }

        @Bean
        public WebClient clientesWebClient(WebClient.Builder webClientBuilder) {
                return webClientBuilder
                        .baseUrl(clientesBaseUrl)
                        .build();
        }

}
