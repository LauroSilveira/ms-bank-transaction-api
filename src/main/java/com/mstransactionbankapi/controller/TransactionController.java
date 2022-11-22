package com.mstransactionbankapi.controller;

import com.mstransactionbankapi.dto.PayLoadDto;
import com.mstransactionbankapi.service.TransactionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
public class TransactionController {


  private final TransactionService service;

  public TransactionController(TransactionService service) {
    this.service = service;
  }


  @GetMapping("/all/{sort}")
  public ResponseEntity<List<PayLoadDto>> getAllTransactions(@PathVariable final String sort) {
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
    return payLoadDto.map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }


  @PostMapping
  public ResponseEntity<PayLoadDto> createTransaction(@RequestBody @Valid final PayLoadDto dto) {
    final var transactionCreated = Optional.ofNullable(service.createTransaction(dto));
    return transactionCreated.map(ResponseEntity::ok)
        .orElse(ResponseEntity.internalServerError().build());
  }
}
