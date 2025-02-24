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

    @Value("${soap.client.max.total.connections:200}")
    private int maxTotalConnections;

    @Value("${soap.client.max.per.route:50}")
    private int maxPerRoute;

    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxTotalConnections);
        connectionManager.setDefaultMaxPerRoute(maxPerRoute);
        connectionManager.setValidateAfterInactivity(1000);
        return connectionManager;
    }

    @Bean
    public RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setSocketTimeout(readTimeout)
                .setConnectionRequestTimeout(connectionTimeout)
                .build();
    }

    @Bean
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager connectionManager,
                                        RequestConfig requestConfig) {
        return HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .evictExpiredConnections()
                .evictIdleConnections(30, TimeUnit.SECONDS)
                .disableCookieManagement()
                .disableAuthCaching()
                .build();
    }

    @Bean
    public HttpComponentsMessageSender messageSender(CloseableHttpClient httpClient) {
        return new HttpComponentsMessageSender(httpClient);
    }

    @Bean
    public SaajSoapMessageFactory messageFactory() throws SOAPException {
        SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory(
                MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL));
        messageFactory.afterPropertiesSet();
        return messageFactory;
    }

    @Bean
    public WebServiceTemplateFactory webServiceTemplateFactory(
            SaajSoapMessageFactory messageFactory,
            HttpComponentsMessageSender messageSender) {
        return new WebServiceTemplateFactory(messageFactory, messageSender);
    }
} 