package com.mstransactionbankapi.service;

import com.mstransactionbankapi.dto.PayLoadDto;
import com.mstransactionbankapi.mapper.TransactionMapper;
import com.mstransactionbankapi.model.TransactionModel;
import com.mstransactionbankapi.respository.TransactionRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

  private final TransactionRepository repository;

  private final TransactionMapper mapper;

  public TransactionService(TransactionRepository repository, TransactionMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Transactional
  public PayLoadDto createTransaction(PayLoadDto dto) {

    try {
      final var model = mapper.mapperToModel(dto);
      if (model.getReference() == null) {
        model.setReference(UUID.randomUUID().toString());
      }
      repository.save(model);

      return mapper.mapperToDto(model);
    } catch (RuntimeException exception) {
      return null;
    }
  }

  @Transactional
  public PayLoadDto getTransactionByAccountIban(String accountIban) {

    Optional<TransactionModel> model = repository.findByAccountIban(accountIban);
    return model.map(mapper::mapperToDto)
        .orElse(null);

  }

  @Transactional
  public List<PayLoadDto> getAllTransactions(String sort) {
    final List<TransactionModel> transactions = repository.findAll(Sort.by(Sort.Direction.fromString(sort), "accountIban"));
    if(!transactions.isEmpty()) {
      return transactions.stream().map(mapper::mapperToDto).toList();
    }else {
      return Collections.emptyList();
    }
  }
}
