package com.example.config;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import java.util.concurrent.TimeUnit;

@Configuration
public class SoapClientConfiguration {
    private static final Logger log = LoggerFactory.getLogger(SoapClientConfiguration.class);

    @Value("${soap.client.connection.timeout:30000}")
    private int connectionTimeout;

    @Value("${soap.client.read.timeout:30000}")
    private int readTimeout;

    @Value("${soap.client.max.connections.per.route:20}")
    private int maxConnectionsPerRoute;

    @Value("${soap.client.max.connections.total:100}")
    private int maxConnectionsTotal;

    @Value("${soap.client.connection.ttl:60000}")
    private long connectionTTL;

    @Bean
    public PoolingHttpClientConnectionManager connectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxConnectionsTotal);
        connectionManager.setDefaultMaxPerRoute(maxConnectionsPerRoute);
        connectionManager.setValidateAfterInactivity(1000); // Validate connections after 1 second of inactivity
        return connectionManager;
    }

    @Bean
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager connectionManager) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setSocketTimeout(readTimeout)
                .setConnectionRequestTimeout(connectionTimeout)
                .build();

        return HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .evictExpiredConnections()
                .evictIdleConnections(connectionTTL, TimeUnit.MILLISECONDS)
                .setKeepAliveStrategy((response, context) -> connectionTTL)
                .disableCookieManagement() // Disable cookie management for stateless calls
                .useSystemProperties() // Use system properties for proxy settings
                .build();
    }

    @Bean
    public HttpComponentsMessageSender messageSender(CloseableHttpClient httpClient) {
        HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender(httpClient);
        messageSender.setConnectionTimeout(connectionTimeout);
        messageSender.setReadTimeout(readTimeout);
        return messageSender;
    }

    @Bean
    public SaajSoapMessageFactory messageFactory() throws SOAPException {
        SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory(
                MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL));
        messageFactory.afterPropertiesSet();
        return messageFactory;
    }

    @Bean
    public Jaxb2Marshaller marshaller(@Value("${soap.dmspos.context-path}") String contextPath) {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(contextPath);
        
        // Add validation and processing options
        marshaller.setValidationEventHandler(event -> {
            log.warn("JAXB validation event: {}", event.getMessage());
            return true; // Continue processing despite validation errors
        });
        
        marshaller.setMtomEnabled(true); // Enable MTOM for efficient binary data handling
        return marshaller;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate(
            Jaxb2Marshaller marshaller,
            SaajSoapMessageFactory messageFactory,
            HttpComponentsMessageSender messageSender) {
        
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(marshaller);
        webServiceTemplate.setUnmarshaller(marshaller);
        webServiceTemplate.setMessageFactory(messageFactory);
        webServiceTemplate.setMessageSender(messageSender);
        
        // Enable fault message resolution
        webServiceTemplate.setFaultMessageResolver(message -> {
            log.error("SOAP Fault received: {}", message);
        });
        
        // Set default URI for all requests
        webServiceTemplate.setDefaultUri("${soap.dmspos.endpoint}");
        
        return webServiceTemplate;
    }
} 