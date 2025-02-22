package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.soap.client.SoapFaultClientException;
import com.example.exception.BadRequestException;
import com.example.exception.InternalServerException;
import com.example.exception.ServiceUnavailableException;

@Component
public class SoapErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(SoapErrorHandler.class);

    public void handleSoapError(Exception ex, String operation) {
        if (ex instanceof WebServiceIOException) {
            log.error("SOAP Connection Error during {}: {}", operation, ex.getMessage());
            throw new ServiceUnavailableException("SOAP service unavailable", ex);
        } else if (ex instanceof SoapFaultClientException) {
            log.error("SOAP Fault Error during {}: {}", operation, ex.getMessage());
            throw new BadRequestException("Invalid SOAP request", ex);
        } else {
            log.error("Unexpected error during {} operation: {}", operation, ex.getMessage());
            throw new InternalServerException("Internal server error", ex);
        }
    }
} 