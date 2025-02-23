package com.example.config.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapMessage;

public class LoggingInterceptor implements ClientInterceptor {
    
    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean handleRequest(MessageContext messageContext) {
        logMessage("Request", (SoapMessage) messageContext.getRequest());
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) {
        logMessage("Response", (SoapMessage) messageContext.getResponse());
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext) {
        logMessage("Fault", (SoapMessage) messageContext.getResponse());
        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException {
        if (ex != null) {
            log.error("Error in SOAP communication", ex);
        }
    }

    private void logMessage(String type, SoapMessage message) {
        try {
            String messageContent = message.getPayloadSource().toString();
            log.debug("SOAP {} Message: {}", type, messageContent);
        } catch (Exception e) {
            log.warn("Could not log {} message", type, e);
        }
    }
} 