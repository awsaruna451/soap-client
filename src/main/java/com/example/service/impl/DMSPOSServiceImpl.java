package com.example.service.impl;

import com.example.exception.SoapServiceException;
import com.example.service.DMSPOSService;
import com.example.service.SoapErrorHandler;
import com.example.config.WebServiceTemplateFactory;

import com.example.soap.ObjectFactory;
import dmsposintegration.dialog.lk.*;
import dmsposintegration.dialog.lk.GetMaterialAvailableResponse;
import dmsposintegration.dialog.lk.PendingPosItemRequest;
import dmsposintegration.dialog.lk.PendingPosItemResponse;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
//import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import javax.xml.namespace.QName;

@Service
public class DMSPOSServiceImpl implements DMSPOSService {

    private static final Logger log = LoggerFactory.getLogger(DMSPOSServiceImpl.class);
    
    // SOAP Action constants
    private static final String ACCEPT_GOODS_TRANSFER_ACTION = "http://sap.com/xi/WebService/soap1.1";
    private static final String ACCEPT_DIR_GOODS_TRANSFER_ACTION = "http://sap.com/xi/WebService/soap1.1";
    private static final String BRANCH_TO_BRANCH_TRANSFER_ACTION = "http://sap.com/xi/WebService/soap1.1";
    private static final String APPROVAL_STO_REQUEST_ACTION = "http://sap.com/xi/WebService/soap1.1";
    private static final String CANCEL_STO_ACTION = "http://sap.com/xi/WebService/soap1.1";
    private static final String STOCK_MOVEMENT_ACTION = "http://sap.com/xi/WebService/soap1.1";
    private static final String WH_RETURN_ACTION = "http://sap.com/xi/WebService/soap1.1";
    private static final String CREATE_POS_STO_ACTION = "http://sap.com/xi/WebService/soap1.1";

    private final WebServiceTemplate webServiceTemplate;
    private final SoapErrorHandler errorHandler;
    private final String serviceEndpoint;

    public DMSPOSServiceImpl(
            WebServiceTemplateFactory webServiceTemplateFactory,
            @Value("${soap.dmspos.endpoint}") String endpoint) {
        String contextPath = "";
        this.webServiceTemplate = webServiceTemplateFactory
            .createWebServiceTemplate(endpoint, contextPath);
        this.errorHandler = null; // Assuming SoapErrorHandler is injected via constructor
        this.serviceEndpoint = endpoint;
    }

    @Override
    public PendingPosItemResponse getPendingItemRequest(PendingPosItemRequest request) throws SoapServiceException {
        try {
            log.debug("Sending getPendingItemRequest: {}", request);
            PendingPosItemResponse response = (PendingPosItemResponse) webServiceTemplate
                    .marshalSendAndReceive(serviceEndpoint, request);
            log.debug("Received getPendingItemRequest response: {}", response);
            return response;
        } catch (Exception e) {
            errorHandler.handleSoapError(e, "getPendingItemRequest");
            throw new SoapServiceException("Failed to get pending item request", e);
        }
    }

    @Override
    public GetMaterialAvailableResponse checkMaterialAvailability(GetMaterialAvailableRequest request) throws SoapServiceException {
        try {
            log.debug("Sending checkMaterialAvailability request: {}", request);
            GetMaterialAvailableResponse response = (GetMaterialAvailableResponse) webServiceTemplate
                    .marshalSendAndReceive(serviceEndpoint, request);
            log.debug("Received checkMaterialAvailability response: {}", response);
            return response;
        } catch (Exception e) {
            errorHandler.handleSoapError(e, "checkMaterialAvailability");
            throw new SoapServiceException("Failed to check material availability", e);
        }
    }

    @Override
    public MaterialCheckReserveResponse checkMaterialReservation(MaterialCheckReserveRequest request) throws SoapServiceException {
        try {
            log.debug("Sending checkMaterialReservation request: {}", request);
            MaterialCheckReserveResponse response = (MaterialCheckReserveResponse) webServiceTemplate
                    .marshalSendAndReceive(serviceEndpoint, request);
            log.debug("Received checkMaterialReservation response: {}", response);
            return response;
        } catch (Exception e) {
            errorHandler.handleSoapError(e, "checkMaterialReservation");
            throw new SoapServiceException("Failed to check material reservation", e);
        }
    }

    @Override
    //@Retryable(value = {SoapFaultClientException.class}, maxAttempts = 3)
    public ReservationConfirmResponse confirmReservation(ReservationConfirmRequest request) throws SoapServiceException {
        try {
            log.debug("Sending confirmReservation request: {}", request);
            ReservationConfirmResponse response = (ReservationConfirmResponse) webServiceTemplate
                    .marshalSendAndReceive(serviceEndpoint, request);
            log.debug("Received confirmReservation response: {}", response);
            return response;
        } catch (Exception e) {
            errorHandler.handleSoapError(e, "confirmReservation");
            throw new SoapServiceException("Failed to confirm reservation", e);
        }
    }

    @Override
   // @Retryable(value = {SoapFaultClientException.class}, maxAttempts = 3)
    public ReservationCancelResponse cancelReservation(ReservationCancelRequest request) throws SoapServiceException {
        try {
            log.debug("Sending cancelReservation request: {}", request);
            ReservationCancelResponse response = (ReservationCancelResponse) webServiceTemplate
                    .marshalSendAndReceive(serviceEndpoint, request);
            log.debug("Received cancelReservation response: {}", response);
            return response;
        } catch (Exception e) {
            errorHandler.handleSoapError(e, "cancelReservation");
            throw new SoapServiceException("Failed to cancel reservation", e);
        }
    }

    @Override
    @Cacheable(value = "stoDetails", key = "#request.stoId")
    public GetSTODetailResponse getSTORequestInfo(GetStoByIDRequest request) throws SoapServiceException {
        try {
            log.debug("Sending getSTORequestInfo request: {}", request);
            GetSTODetailResponse response = (GetSTODetailResponse) webServiceTemplate
                    .marshalSendAndReceive(serviceEndpoint, request);
            log.debug("Received getSTORequestInfo response: {}", response);
            return response;
        } catch (Exception e) {
            errorHandler.handleSoapError(e, "getSTORequestInfo");
            throw new SoapServiceException("Failed to get STO request info", e);
        }
    }

    @Override
    @Cacheable(value = "outletItemBalance", key = "#request.outletId + '-' + #request.itemId")
    public CheckOutletItemBalanceResponse getAvailableItemQtyFromOutlet(CheckOutletItemBalanceRequest request) throws SoapServiceException {
        try {
            log.debug("Sending getAvailableItemQtyFromOutlet request: {}", request);
            CheckOutletItemBalanceResponse response = (CheckOutletItemBalanceResponse) webServiceTemplate
                    .marshalSendAndReceive(serviceEndpoint, request);
            log.debug("Received getAvailableItemQtyFromOutlet response: {}", response);
            return response;
        } catch (Exception e) {
            errorHandler.handleSoapError(e, "getAvailableItemQtyFromOutlet");
            throw new SoapServiceException("Failed to get available item quantity", e);
        }
    }

    @Override
    //@Retryable(value = {SoapFaultClientException.class}, maxAttempts = 2)
    public CreatePOSSTOItemResponse createPOSSto(CreatePOSSTOItemRequest request) throws SoapServiceException {
        try {
            log.debug("Sending createPOSSto request: {}", request);
            CreatePOSSTOItemResponse response = (CreatePOSSTOItemResponse) webServiceTemplate
                    .marshalSendAndReceive(serviceEndpoint, request);
            log.debug("Received createPOSSto response: {}", response);
            return response;
        } catch (Exception e) {
            errorHandler.handleSoapError(e, "createPOSSto");
            throw new SoapServiceException("Failed to create POS STO", e);
        }
    }

    @Override
    public UpdateGoodIssueResponse updateGoodsIssue(UpdateGoodIssueRequest request) throws SoapServiceException {
        try {
            log.debug("Sending updateGoodsIssue request: {}", request);
            UpdateGoodIssueResponse response = (UpdateGoodIssueResponse) webServiceTemplate
                    .marshalSendAndReceive(serviceEndpoint, request);
            log.debug("Received updateGoodsIssue response: {}", response);
            return response;
        } catch (Exception e) {
            errorHandler.handleSoapError(e, "updateGoodsIssue");
            throw new SoapServiceException("Failed to update goods issue", e);
        }
    }

    @Override
    @Cacheable(value = "serialValidation", key = "#request.serialNumber")
    public GetSerialValidationResponse validateSerialNumbers(GetSerialValidationRequest request) throws SoapServiceException {
        try {
            log.debug("Sending validateSerialNumbers request: {}", request);
            GetSerialValidationResponse response = (GetSerialValidationResponse) webServiceTemplate
                    .marshalSendAndReceive(serviceEndpoint, request);
            log.debug("Received validateSerialNumbers response: {}", response);
            return response;
        } catch (Exception e) {
            errorHandler.handleSoapError(e, "validateSerialNumbers");
            throw new SoapServiceException("Failed to validate serial numbers", e);
        }
    }

    @Override
    public LoadGTNDetailsResponse loadGTNDetails(LoadGTNDetailsRequest request) throws SoapServiceException {
        try {
            log.debug("Sending loadGTNDetails request: {}", request);
            LoadGTNDetailsResponse response = (LoadGTNDetailsResponse) webServiceTemplate
                    .marshalSendAndReceive(serviceEndpoint, request);
            log.debug("Received loadGTNDetails response: {}", response);
            return response;
        } catch (Exception e) {
            errorHandler.handleSoapError(e, "loadGTNDetails");
            throw new SoapServiceException("Failed to load GTN details", e);
        }
    }

    @Override
    public BinCardInvBalanceResponse getInventoryBinBalance(BinCardInvBalanaceRequest request) throws SoapServiceException {
        try {
            log.debug("Sending getInventoryBinBalance request: {}", request);
            BinCardInvBalanceResponse response = (BinCardInvBalanceResponse) webServiceTemplate
                    .marshalSendAndReceive(serviceEndpoint, request);
            log.debug("Received getInventoryBinBalance response: {}", response);
            return response;
        } catch (Exception e) {
            errorHandler.handleSoapError(e, "getInventoryBinBalance");
            throw new SoapServiceException("Failed to get inventory bin balance", e);
        }
    }

    @Override
    public BinLocationItemFlowResponse getInventoryBinItemFlow(BinCardInvBalanaceRequest request) throws SoapServiceException {
        try {
            log.debug("Sending getInventoryBinItemFlow request: {}", request);
            BinLocationItemFlowResponse response = (BinLocationItemFlowResponse) webServiceTemplate
                    .marshalSendAndReceive(serviceEndpoint, request);
            log.debug("Received getInventoryBinItemFlow response: {}", response);
            return response;
        } catch (Exception e) {
            errorHandler.handleSoapError(e, "getInventoryBinItemFlow");
            throw new SoapServiceException("Failed to get inventory bin item flow", e);
        }
    }

    private void addSoapHeaders(SoapMessage soapMessage, String action) {
        try {
            soapMessage.setSoapAction(action);
            
            // Cast to SaajSoapMessage to access the SOAP header
            SaajSoapMessage saajSoapMessage = (SaajSoapMessage) soapMessage;
            SOAPHeader soapHeader = saajSoapMessage.getSaajMessage().getSOAPHeader();
            
            // Create the header element
            QName qName = new QName("http://sap.com/xi/WebService", "MessageSender");
            SOAPElement headerElement = soapHeader.addChildElement(qName);
            headerElement.addTextNode("DMS");
            
        } catch (SOAPException e) {
            log.error("Error adding SOAP headers", e);
        }
    }

    @Override
    //@Retryable(value = {SoapFaultClientException.class}, maxAttempts = 2)
    public AcceptGoodsResponse acceptGoodsTranferNote(AcceptGoodsRequest request) throws SoapServiceException {
        try {
            log.info("Starting accept goods transfer note request");
            AcceptGoodsResponse response = (AcceptGoodsResponse) webServiceTemplate
                    .marshalSendAndReceive(serviceEndpoint, request, message -> 
                        addSoapHeaders((SoapMessage) message, ACCEPT_GOODS_TRANSFER_ACTION));
            log.info("Completed accept goods transfer note request");
            return response;
        } catch (Exception e) {
            log.error("Exception caught in accept-goods-transfer-route", e);
            errorHandler.handleSoapError(e, "acceptGoodsTranferNote");
            throw new SoapServiceException("Failed to accept goods transfer note", e);
        }
    }

    @Override
    public AcceptGoodsResponse acceptDirectGoodsTranferNote(AcceptDirectGoodsRequest request) throws SoapServiceException {
        try {
            log.info("Starting accept direct goods transfer note request");
            AcceptGoodsResponse response = (AcceptGoodsResponse) webServiceTemplate
                    .marshalSendAndReceive(serviceEndpoint, request, message -> 
                        addSoapHeaders((SoapMessage) message, ACCEPT_DIR_GOODS_TRANSFER_ACTION));
            log.info("Completed accept direct goods transfer note request");
            return response;
        } catch (Exception e) {
            log.error("Exception caught in accept-direct-goods-transfer-route", e);
            errorHandler.handleSoapError(e, "acceptDirectGoodsTranferNote");
            throw new SoapServiceException("Failed to accept direct goods transfer note", e);
        }
    }

    @Override
    public BranchToBranchTransferResponse branchToBranchTransfer(BranchToBranchTransferRequest request) throws SoapServiceException {
        try {
            log.info("Starting branch to branch transfer request");
            BranchToBranchTransferResponse response = (BranchToBranchTransferResponse) webServiceTemplate
                    .marshalSendAndReceive(serviceEndpoint, request, message -> 
                        addSoapHeaders((SoapMessage) message, BRANCH_TO_BRANCH_TRANSFER_ACTION));
            log.info("Completed branch to branch transfer request");
            return response;
        } catch (Exception e) {
            log.error("Exception caught in branch-to-branch-transfer-route", e);
            errorHandler.handleSoapError(e, "branchToBranchTransfer");
            throw new SoapServiceException("Failed to process branch to branch transfer", e);
        }
    }

    @Override
    public LazyPayMessageResponse stockMovement(LazyPayMessageRequest request) throws SoapServiceException {
        try {
            log.debug("Sending stockMovement request: {}", request);
            LazyPayMessageResponse response = (LazyPayMessageResponse) webServiceTemplate
                    .marshalSendAndReceive(serviceEndpoint, request, message -> {
                        ((SoapMessage) message).setSoapAction("http://sap.com/xi/WebService/soap1.1");
                    });
            log.debug("Received stockMovement response: {}", response);
            return response;
        } catch (Exception e) {
            errorHandler.handleSoapError(e, "stockMovement");
            throw new SoapServiceException("Failed to process stock movement", e);
        }
    }
}
