package com.example.controller;

import com.example.service.SoapClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SoapController {
    private final SoapClient soapClient;

    public SoapController(SoapClient soapClient) {
        this.soapClient = soapClient;
    }

    @PostMapping("/soap-operation")
    public Object callSoapOperation(@RequestBody Object request) {
        return soapClient.callSoapMethod(request);
    }
} 