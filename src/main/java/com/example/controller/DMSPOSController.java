/*
package com.example.controller;

import com.example.service.DMSPOSService;
import dmsposintegration.dialog.lk.GetSTODetailResponse;
import dmsposintegration.dialog.lk.GetStoByIDRequest;
import io.micrometer.core.annotation.Timed;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.example.exception.InternalServerException;

@RestController
@RequestMapping("/api/dms")
public class DMSPOSController {

    private final DMSPOSService dmsposService;

    public DMSPOSController(DMSPOSService dmsposService) {
        this.dmsposService = dmsposService;
    }

    @PostMapping("/sto/details")
    @Timed("dms.sto.details")
    public ResponseEntity<GetSTODetailResponse> getSTODetails(@RequestBody GetStoByIDRequest request) {
        try {
            GetSTODetailResponse response = dmsposService.getSTORequestInfo(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new InternalServerException("Error getting STO details", e);
        }
    }

    // Add other endpoints as needed
}*/
