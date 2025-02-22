package com.example.controller;

import com.example.service.SoapClientService;
import com.example.soap.GetOrderStatusRequest;
import com.example.soap.GetOrderStatusResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SoapController {
    private final SoapClientService soapClient;

    public SoapController(SoapClientService soapClient) {
        this.soapClient = soapClient;
    }

    @PostMapping("/order-status")
    public GetOrderStatusResponse getOrderStatus(@RequestBody GetOrderStatusRequest request) {
        return soapClient.getOrderStatus(request);
    }
} 