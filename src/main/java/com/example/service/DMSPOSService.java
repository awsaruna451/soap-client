package com.example.service;

import com.example.exception.SoapServiceException;

import dmsposintegration.dialog.lk.*;
import dmsposintegration.dialog.lk.GetMaterialAvailableResponse;
import dmsposintegration.dialog.lk.PendingPosItemRequest;
import dmsposintegration.dialog.lk.PendingPosItemResponse;

public interface DMSPOSService {
    
    /**
     * Get pending item request
     */
    PendingPosItemResponse getPendingItemRequest(PendingPosItemRequest request) throws SoapServiceException;
    
    /**
     * Check material availability
     */
    GetMaterialAvailableResponse checkMaterialAvailability(GetMaterialAvailableRequest request) throws SoapServiceException;
    
    /**
     * Check material reservation
     */
    MaterialCheckReserveResponse checkMaterialReservation(MaterialCheckReserveRequest request) throws SoapServiceException;
    
    /**
     * Confirm reservation
     */
    ReservationConfirmResponse confirmReservation(ReservationConfirmRequest request) throws SoapServiceException;
    
    /**
     * Cancel reservation
     */
    ReservationCancelResponse cancelReservation(ReservationCancelRequest request) throws SoapServiceException;
    
    /**
     * Get STO request info
     */
    GetSTODetailResponse getSTORequestInfo(GetStoByIDRequest request) throws SoapServiceException;
    
    /**
     * Get available item quantity from outlet
     */
    CheckOutletItemBalanceResponse getAvailableItemQtyFromOutlet(CheckOutletItemBalanceRequest request) throws SoapServiceException;
    
    /**
     * Create POS STO
     */
    CreatePOSSTOItemResponse createPOSSto(CreatePOSSTOItemRequest request) throws SoapServiceException;
    
    /**
     * Update goods issue
     */
    UpdateGoodIssueResponse updateGoodsIssue(UpdateGoodIssueRequest request) throws SoapServiceException;
    
    /**
     * Validate serial numbers
     */
    GetSerialValidationResponse validateSerialNumbers(GetSerialValidationRequest request) throws SoapServiceException;
    
    /**
     * Load GTN details
     */
    LoadGTNDetailsResponse loadGTNDetails(LoadGTNDetailsRequest request) throws SoapServiceException;
    
    /**
     * Get inventory bin balance
     */
    BinCardInvBalanceResponse getInventoryBinBalance(BinCardInvBalanaceRequest request) throws SoapServiceException;
    
    /**
     * Get inventory bin item flow
     */
    BinLocationItemFlowResponse getInventoryBinItemFlow(BinCardInvBalanaceRequest request) throws SoapServiceException;
    
    /**
     * Accept goods transfer note
     */
    AcceptGoodsResponse acceptGoodsTranferNote(AcceptGoodsRequest request) throws SoapServiceException;
    
    /**
     * Accept direct goods transfer note
     */
    AcceptGoodsResponse acceptDirectGoodsTranferNote(AcceptDirectGoodsRequest request) throws SoapServiceException;
    
    /**
     * Process branch to branch transfer
     */
    BranchToBranchTransferResponse branchToBranchTransfer(BranchToBranchTransferRequest request) throws SoapServiceException;
    
    /**
     * Process stock movement
     */
    LazyPayMessageResponse stockMovement(LazyPayMessageRequest request) throws SoapServiceException;
}
    
