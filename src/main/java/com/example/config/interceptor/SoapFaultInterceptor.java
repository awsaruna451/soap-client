package com.example.config.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapMessage;

public class SoapFaultInterceptor implements ClientInterceptor {
    
    private static final Logger log = LoggerFactory.getLogger(SoapFaultInterceptor.class);

    @Override
    public boolean handleRequest(MessageContext messageContext) {
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) {
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext) {
        SoapMessage soapMessage = (SoapMessage) messageContext.getResponse();
        SoapBody soapBody = soapMessage.getSoapBody();
        SoapFault soapFault = soapBody.getFault();
        
        if (soapFault != null) {
            log.error("SOAP Fault: {}", soapFault.getFaultStringOrReason());
        }
        
        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException {

    }
} 