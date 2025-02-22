package com.example.service;

import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
//import org.springframework.retry.annotation.Retry;
import com.example.soap.GetOrderStatusRequest;  // Generated from WSDL
import com.example.soap.GetOrderStatusResponse; // Generated from WSDL

@Service
public class SoapClientService {
    private static final Logger log = LoggerFactory.getLogger(SoapClientService.class);

    private final WebServiceTemplate webServiceTemplate;
    private final SoapErrorHandler errorHandler;
    
    public SoapClientService(WebServiceTemplate webServiceTemplate, SoapErrorHandler errorHandler) {
        this.webServiceTemplate = webServiceTemplate;
        this.errorHandler = errorHandler;
    }
    
  //  @Retry(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public GetOrderStatusResponse getOrderStatus(GetOrderStatusRequest request) {
        try {
            log.debug("Calling SOAP service with request: {}", request);
            GetOrderStatusResponse response = (GetOrderStatusResponse) webServiceTemplate.marshalSendAndReceive(request);
            log.debug("Received SOAP response: {}", response);
            return response;
        } catch (Exception ex) {
            errorHandler.handleSoapError(ex, "getOrderStatus");
            return null; // Will never reach here due to exception handling
        }
    }
} 