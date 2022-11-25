package com.mstransactionbankapi.service;

import com.mstransactionbankapi.dto.PayLoadDto;
import com.mstransactionbankapi.dto.StatusDto;
import com.mstransactionbankapi.mapper.TransactionMapper;
import com.mstransactionbankapi.model.Channel;
import com.mstransactionbankapi.model.Status;
import com.mstransactionbankapi.model.TransactionModel;
import com.mstransactionbankapi.respository.TransactionRepository;
import jakarta.persistence.PersistenceException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class TransactionService {

  private final TransactionRepository repository;

  private final TransactionMapper mapper;

  public TransactionService(TransactionRepository repository, TransactionMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Transactional
  public PayLoadDto saveTransaction(PayLoadDto dto) {

    try {
      final var model = mapper.mapperToModel(dto);
      if (model.getReference() == null) {
        model.setReference(UUID.randomUUID().toString());
      }
      repository.save(model);
      return mapper.mapperToDto(model);
    } catch (PersistenceException exception) {
      log.error("Error to save a new transaction: {}", exception.getMessage());
      return null;
    }
  }

  @Transactional
  public PayLoadDto getTransactionByAccountIban(String accountIban) {

    Optional<TransactionModel> model = repository.findByAccountIban(accountIban);

    log.info("Found transaction {}  with AccountIban {}", accountIban, model);

    return model.map(mapper::mapperToDto)
        .orElse(null);

  }

  @Transactional
  public List<PayLoadDto> getAllTransactions(String sort) {
    final List<TransactionModel> transactions = repository.findAll(
        Sort.by(Sort.Direction.fromString(sort), "accountIban"));
    if (!transactions.isEmpty()) {
      return transactions.stream().map(mapper::mapperToDto).toList();
    } else {
      log.info("Not found transaction");
      return Collections.emptyList();
    }
  }

  public StatusDto verifyTransactionStored(final StatusDto dto) {
    log.info("Received request to verify transaction: {}", dto);
    TransactionModel model = mapper.mapperToModel(dto);

    var entity = repository.findByReference(
        model.getReference());

    if (entity.isPresent()) {
      final var transactionModel = checkTransactionStausChannel(entity.get());

      log.info("Transaction after check status: {} ", transactionModel);
      return mapper.mapperToStatusDto(transactionModel);
    } else {
      // case A
      //if the transaction is not Stored
      return StatusDto.builder()
          .reference(dto.getReference())
          .status(Status.INVALID)
          .build();
    }
  }

  private TransactionModel checkTransactionStausChannel(TransactionModel model) {

    final Instant dateToday = Instant.now().truncatedTo(ChronoUnit.MINUTES);
    //case B
    if (model.getDate()
        .isBefore(dateToday) && (model.getChannel() == Channel.ATM
        || model.getChannel() == Channel.CLIENT)) {
      model.setReference(model.getReference());
      model.setStatus(Status.SETTLED);
      model.setAmount(model.getAmount().subtract(model.getFee()));

    }
    //case C
    if (model.getDate().isBefore(dateToday) && model.getChannel() == Channel.INTERNAL) {
      model.setReference(model.getReference());
      model.setStatus(Status.SETTLED);
      model.setAmount(model.getAmount());
      model.setFee(model.getFee());
    }
    //case D
    if (model.getDate().equals(dateToday)
        && (model.getChannel() == Channel.ATM || model.getChannel() == Channel.CLIENT)) {
      model.setReference(model.getReference());
      model.setStatus(Status.PENDING);
      model.setAmount(model.getAmount().subtract(model.getFee()));
    }
    //case E
    if (model.getDate().equals(dateToday) && model.getChannel() == Channel.INTERNAL) {
      model.setReference(model.getReference());
      model.setStatus(Status.PENDING);
      model.setFee(model.getFee());
    }
    //case F
    if (model.getDate().isAfter(dateToday) && model.getChannel() == Channel.CLIENT) {
      model.setReference(model.getReference());
      model.setStatus(Status.FUTURE);
      model.setAmount(model.getAmount().subtract(model.getFee()));
    }
    //case G
    if (model.getDate().isAfter(dateToday) && model.getChannel() == Channel.ATM) {
      model.setReference(model.getReference());
      model.setStatus(Status.PENDING);
      model.setAmount(model.getAmount().subtract(model.getFee()));
    }
    //case H
    if (model.getDate().isAfter(dateToday) && model.getChannel() == Channel.INTERNAL) {
      model.setReference(model.getReference());
      model.setStatus(Status.FUTURE);
      model.setAmount(model.getAmount());
      model.setFee(model.getFee());
    }
    return model;
  }

}
