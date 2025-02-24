package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

public class WebServiceTemplateFactory {
    private static final Logger log = LoggerFactory.getLogger(WebServiceTemplateFactory.class);
    
    private final SaajSoapMessageFactory messageFactory;
    private final HttpComponentsMessageSender messageSender;

    public WebServiceTemplateFactory(SaajSoapMessageFactory messageFactory,
                                   HttpComponentsMessageSender messageSender) {
        this.messageFactory = messageFactory;
        this.messageSender = messageSender;
    }

    public WebServiceTemplate createWebServiceTemplate(String endpoint, String contextPath) {
        Jaxb2Marshaller marshaller = createMarshaller(contextPath);
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(marshaller);
        webServiceTemplate.setUnmarshaller(marshaller);
        webServiceTemplate.setMessageFactory(messageFactory);
        webServiceTemplate.setMessageSender(messageSender);
        webServiceTemplate.setDefaultUri(endpoint);
        return webServiceTemplate;
    }

    private Jaxb2Marshaller createMarshaller(String contextPath) {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(contextPath);
        marshaller.setValidationEventHandler(event -> {
            log.warn("JAXB validation event: {}", event.getMessage());
            return true;
        });
        return marshaller;
    }
} 