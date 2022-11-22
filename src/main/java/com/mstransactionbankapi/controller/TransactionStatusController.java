package com.mstransactionbankapi.controller;

import com.mstransactionbankapi.dto.RequestTransactionStatusDto;
import com.mstransactionbankapi.dto.ResponseTransactionStatusDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/status")
public class TransactionStatusController {

  private TransactionStatusService service;

  public TransactionStatusController(TransactionStatusService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<ResponseTransactionStatusDto> verifyTransactionStored(
      @RequestBody @Valid final RequestTransactionStatusDto dto) {
    final ResponseTransactionStatusDto response = service.verifyTransactionStored(
        dto);
    return ResponseEntity.ok(response);
  }

}
