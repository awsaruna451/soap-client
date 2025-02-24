package com.example.config;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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

@Configuration
public class SoapClientConfiguration {
    private static final Logger log = LoggerFactory.getLogger(SoapClientConfiguration.class);

    @Value("${soap.client.connection.timeout:30000}")
    private int connectionTimeout;

    @Value("${soap.client.read.timeout:30000}")
    private int readTimeout;

    @Value("${soap.dmspos.endpoint}")
    private String defaultUri;

    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(50);
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
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    @Bean
    public HttpComponentsMessageSender httpComponentsMessageSender(CloseableHttpClient httpClient) {
        HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender(httpClient);
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
        webServiceTemplate.setDefaultUri(defaultUri);
        
        return webServiceTemplate;
    }
} 