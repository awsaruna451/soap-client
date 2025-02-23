package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.example.exception.BadRequestException;
import com.example.exception.InternalServerException;
import com.example.exception.ServiceUnavailableException;

@Component
public class SoapErrorHandler implements ClientInterceptor {
    
    private static final Logger log = LoggerFactory.getLogger(SoapErrorHandler.class);

    @Override
    public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
        log.debug("Received SOAP Response");
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
        log.error("SOAP Fault occurred");
        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException {
        if (ex != null) {
            log.error("Error occurred during SOAP call", ex);
            handleSoapError(ex, "");
        }
    }

    public void handleSoapError(Exception ex, String operation) {
        log.error("Error during SOAP operation {}: {}", operation, ex.getMessage());
        
        if (ex instanceof SoapFaultClientException) {
            throw new BadRequestException("Invalid request for operation: " + operation, ex);
        } else if (ex instanceof WebServiceIOException) {
            throw new ServiceUnavailableException("Service unavailable for operation: " + operation, ex);
        } else {
            throw new InternalServerException("Internal error during operation: " + operation, ex);
        }
    }
} 