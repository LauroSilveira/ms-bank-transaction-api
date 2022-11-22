package com.mstransactionbankapi.controller;

import com.mstransactionbankapi.dto.RequestTransactionStatusDto;
import com.mstransactionbankapi.dto.ResponseTransactionStatusDto;
import com.mstransactionbankapi.mapper.TransactionStatusMapper;
import com.mstransactionbankapi.model.Status;
import com.mstransactionbankapi.model.TransactionModel;
import com.mstransactionbankapi.respository.TransactionRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TransactionStatusService {

  private final TransactionRepository repository;

  private final TransactionStatusMapper mapper;

  public TransactionStatusService(TransactionRepository repository,
      final TransactionStatusMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  public ResponseTransactionStatusDto verifyTransactionStored(
      final RequestTransactionStatusDto dto) {

    final TransactionModel model = mapper.mapperToModel(dto);
    var transactionRetrieved = repository.findByReference(
        model.getReference());

    if (transactionRetrieved.isPresent()) {
      return transactionRetrieved.map(mapper::mapperToDto).get();
    } else {
      return ResponseTransactionStatusDto.builder()
          .reference(dto.reference())
          .status(Status.INVALID)
          .build();
    }
  }
}
