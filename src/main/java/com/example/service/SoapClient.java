package com.example.service;

import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.stereotype.Service;

@Service
public class SoapClient {

    private final WebServiceTemplate webServiceTemplate;
    private final String serviceUrl = "YOUR_SOAP_SERVICE_URL"; // Replace with actual WSDL URL

    public SoapClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    public Object callSoapMethod(Object request) {
        return webServiceTemplate.marshalSendAndReceive(serviceUrl, request);
    }
} 