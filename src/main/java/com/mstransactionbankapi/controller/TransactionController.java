package com.mstransactionbankapi.controller;

import com.mstransactionbankapi.dto.PayLoadDto;
import com.mstransactionbankapi.dto.StatusDto;
import com.mstransactionbankapi.service.TransactionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
@Slf4j
public class TransactionController {

  private final TransactionService service;
  public TransactionController(TransactionService service) {
    this.service = service;
  }

  @GetMapping("/all/{sort}")
  public ResponseEntity<List<PayLoadDto>> getAllTransactions(@PathVariable final String sort) {
    log.info("GetAllTransactions with sort {}", sort);
    List<PayLoadDto> payLoadDtoList = service.getAllTransactions(sort);
    if (!payLoadDtoList.isEmpty()) {
      return ResponseEntity.ok(payLoadDtoList);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/{accountIban}")
  public ResponseEntity<PayLoadDto> getTransactionByAccountIban(
      @PathVariable @Valid final String accountIban) {
    Optional<PayLoadDto> payLoadDto = Optional.ofNullable(
        service.getTransactionByAccountIban(accountIban));
    log.info("GetTransactionByAccountIban by AccountIban {}", accountIban);
    return payLoadDto.map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }


  @PostMapping("/save")
  public ResponseEntity<PayLoadDto> saveTransaction(@RequestBody @Valid final PayLoadDto dto) {
    log.info("Save a new transaction: {}", dto);
    final var transactionCreated = Optional.ofNullable(service.saveTransaction(dto));
    return transactionCreated.map(ResponseEntity::ok)
        .orElse(ResponseEntity.internalServerError().build());
  }

  @PostMapping("/status")
  public ResponseEntity<StatusDto> verifyTransactionStored(
      @RequestBody @Valid final StatusDto dto) {
    log.info("Request received to verify transaction: {}", dto);
    final StatusDto response = service.verifyTransactionStored(dto);
    return ResponseEntity.ok(response);
  }

}
