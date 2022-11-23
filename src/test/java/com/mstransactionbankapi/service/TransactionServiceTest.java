package com.mstransactionbankapi.service;

import com.mstransactionbankapi.dto.PayLoadDto;
import com.mstransactionbankapi.dto.StatusDto;
import com.mstransactionbankapi.mapper.TransactionMapperImpl;
import com.mstransactionbankapi.model.Channel;
import com.mstransactionbankapi.model.Status;
import com.mstransactionbankapi.model.TransactionModel;
import com.mstransactionbankapi.respository.TransactionRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class TransactionServiceTest {

  @SpyBean
  private TransactionService service;

  @Autowired
  private TransactionRepository repository;

  @SpyBean
  private TransactionMapperImpl mapper;


  @Test
  void saveTransactionTest() {
    //Given
    var dto = PayLoadDto.builder()
        .reference(UUID.randomUUID().toString())
        .date(Instant.now())
        .amount(new BigDecimal("3.12"))
        .fee(new BigDecimal("2.3"))
        .channel(Channel.CLIENT)
        .build();

    final var model = mapper.mapperToModel(dto);

    Mockito.when(mapper.mapperToModel(dto)).thenReturn(model);

    //When
    var payLoadDto = service.saveTransaction(dto);

    //Then
    Assertions.assertAll(() -> Assertions.assertNotNull(payLoadDto),
        () -> Assertions.assertEquals(dto.getReference(), payLoadDto.getReference()),
        () -> Assertions.assertEquals(dto.getDate(), payLoadDto.getDate()),
        () -> Assertions.assertEquals(dto.getAmount(), payLoadDto.getAmount()),
        () -> Assertions.assertEquals(dto.getFee(), payLoadDto.getFee()),
        () -> Assertions.assertEquals(dto.getChannel(), payLoadDto.getChannel())
    );
  }

  @Test
  void getTransactionByAccountIbanTest() {
    //Given
    var dto = PayLoadDto.builder()
        .reference(UUID.randomUUID().toString())
        .accountIban("123456")
        .date(Instant.now())
        .amount(new BigDecimal("3.12"))
        .fee(new BigDecimal("2.3"))
        .channel(Channel.CLIENT)
        .build();

    final var model = mapper.mapperToModel(dto);
    this.repository.save(model);

    //When
    PayLoadDto payLoadDto = service.getTransactionByAccountIban(dto.getAccountIban());

    //Then
    Assertions.assertAll(() -> Assertions.assertNotNull(payLoadDto),
        () -> Assertions.assertEquals(dto.getReference(), payLoadDto.getReference()),
        () -> Assertions.assertEquals(dto.getDate(), payLoadDto.getDate()),
        () -> Assertions.assertEquals(dto.getAmount(), payLoadDto.getAmount()),
        () -> Assertions.assertEquals(dto.getFee(), payLoadDto.getFee()),
        () -> Assertions.assertEquals(dto.getChannel(), payLoadDto.getChannel())
    );
  }

  @Test
  void getAllTransactionsByAscOrderTest() {
    //Given
    List<TransactionModel> modelList = List.of(TransactionModel.builder()
            .reference(UUID.randomUUID().toString())
            .accountIban("123456")
            .date(Instant.now())
            .amount(new BigDecimal("3.12"))
            .fee(new BigDecimal("2.3"))
            .channel(Channel.CLIENT)
            .build(),
        TransactionModel.builder()
            .reference(UUID.randomUUID().toString())
            .accountIban("654321")
            .date(Instant.now())
            .amount(new BigDecimal("3.12"))
            .fee(new BigDecimal("2.3"))
            .channel(Channel.CLIENT)
            .build(),
        TransactionModel.builder()
            .reference(UUID.randomUUID().toString())
            .accountIban("78910")
            .date(Instant.now())
            .amount(new BigDecimal("3.12"))
            .fee(new BigDecimal("2.3"))
            .channel(Channel.CLIENT)
            .build()
    );
    this.repository.saveAll(modelList);

    //when
    final List<PayLoadDto> payLoadDtoList = this.service.getAllTransactions("asc");

    Assertions.assertAll(() -> Assertions.assertNotNull(payLoadDtoList),
        () -> Assertions.assertEquals(3, payLoadDtoList.size()));
  }

  @Test
  void shouldInvalidTransactionTest() {
    //case A
    //Given
    var request = StatusDto.builder()
        .reference(UUID.randomUUID().toString())
        .channel(Channel.CLIENT)
        .build();

    //when
    final StatusDto response = this.service.verifyTransactionStored(request);

    //Then
    Assertions.assertAll(
        () -> Assertions.assertEquals(request.getReference(), response.getReference()),
        () -> Assertions.assertEquals(Status.INVALID, response.getStatus()));

  }

  @Test
  void checkTrnasactionDateIsBeforeTodayFromClientChannelTest() {
    final String reference = UUID.randomUUID().toString();
    //case B
    //Given
    var request = StatusDto.builder()
        .reference(reference)
        .channel(Channel.CLIENT)
        .build();

    var transactionModel = TransactionModel.builder()
        .reference(reference)
        .date(Instant.parse("2017-12-25T20:30:50Z"))
        .channel(Channel.CLIENT)
        .amount(new BigDecimal("19.59"))
        .fee(new BigDecimal("2.30"))
        .build();

    this.repository.save(transactionModel);
    //When
    final StatusDto response = this.service.verifyTransactionStored(request);
    //Then
    Assertions.assertEquals(request.getReference(), response.getReference());
    Assertions.assertEquals(Status.SETTLED, response.getStatus());
    Assertions.assertEquals(transactionModel.getAmount().subtract(transactionModel.getFee()),
        response.getAmount());
  }

  @Test
  void checkTransactionDateIsBeforeTodayFromInternalChannelTest() {
    final String reference = UUID.randomUUID().toString();
    //case C
    //Given
    var request = StatusDto.builder()
        .reference(reference)
        .channel(Channel.INTERNAL)
        .build();

    var transactionModel = TransactionModel.builder()
        .reference(reference)
        .date(Instant.parse("2017-12-25T20:30:50Z"))
        .channel(Channel.INTERNAL)
        .amount(new BigDecimal("19.59"))
        .fee(new BigDecimal("2.30"))
        .build();

    this.repository.save(transactionModel);
    //When
    final StatusDto response = this.service.verifyTransactionStored(request);
    //Then
    Assertions.assertEquals(request.getReference(), response.getReference());
    Assertions.assertEquals(Status.SETTLED, response.getStatus());
    Assertions.assertEquals(new BigDecimal("19.59"), response.getAmount());
    Assertions.assertEquals(new BigDecimal("2.30"), response.getFee());
  }

  @Test
  void checkTransactionDateIsEqualsFromATMChannelTest() {
    final String reference = UUID.randomUUID().toString();
    //case D
    //Given
    var request = StatusDto.builder()
        .reference(reference)
        .channel(Channel.ATM)
        .build();

    var transactionModel = TransactionModel.builder()
        .reference(reference)
        .date(Instant.now().truncatedTo(ChronoUnit.MINUTES))
        .channel(Channel.ATM)
        .amount(new BigDecimal("19.59"))
        .fee(new BigDecimal("2.30"))
        .build();

    this.repository.save(transactionModel);
    //When
    final StatusDto response = this.service.verifyTransactionStored(request);
    //Then
    Assertions.assertEquals(request.getReference(), response.getReference());
    Assertions.assertEquals(Status.PENDING, response.getStatus());
    Assertions.assertEquals(transactionModel.getAmount().subtract(transactionModel.getFee()),
        response.getAmount());
  }

  @Test
  void checkTransactionDateIsEqualsFromInternalChannelTest() {
    final String reference = UUID.randomUUID().toString();
    //case E
    //Given
    var request = StatusDto.builder()
        .reference(reference)
        .channel(Channel.INTERNAL)
        .build();

    var transactionModel = TransactionModel.builder()
        .reference(reference)
        .date(Instant.now().truncatedTo(ChronoUnit.MINUTES))
        .channel(Channel.INTERNAL)
        .amount(new BigDecimal("19.59"))
        .fee(new BigDecimal("2.30"))
        .build();

    this.repository.save(transactionModel);

    //When
    final StatusDto response = this.service.verifyTransactionStored(request);
    //Then
    Assertions.assertEquals(request.getReference(), response.getReference());
    Assertions.assertEquals(Status.PENDING, response.getStatus());
    Assertions.assertEquals(transactionModel.getAmount(), response.getAmount());
    Assertions.assertEquals(transactionModel.getFee(), response.getFee());
  }

  @Test
  void checkTransactionDateIsGraterThanTodayFromClientChannelTest() {
    final String reference = UUID.randomUUID().toString();
    //case F
    //Given
    var request = StatusDto.builder()
        .reference(reference)
        .channel(Channel.CLIENT)
        .build();

    var transactionModel = TransactionModel.builder()
        .reference(reference)
        .date(LocalDateTime.of(2025, Month.JANUARY, 12,
            22, 20).toInstant(ZoneOffset.UTC))
        .channel(Channel.CLIENT)
        .amount(new BigDecimal("19.59"))
        .fee(new BigDecimal("2.30"))
        .build();

    this.repository.save(transactionModel);

    //When
    final StatusDto response = this.service.verifyTransactionStored(request);
    //Then
    Assertions.assertEquals(request.getReference(), response.getReference());
    Assertions.assertEquals(Status.FUTURE, response.getStatus());
    Assertions.assertEquals(transactionModel.getAmount().subtract(transactionModel.getFee()),
        response.getAmount());
  }

  @Test
  void checkTransactionDateIsGraterThanTodayFromATMChannelTest() {
    final String reference = UUID.randomUUID().toString();
    //case H
    //Given
    var request = StatusDto.builder()
        .reference(reference)
        .channel(Channel.ATM)
        .build();

    var transactionModel = TransactionModel.builder()
        .reference(reference)
        .date(LocalDateTime.of(2023, Month.DECEMBER, 12,
            22, 20).toInstant(ZoneOffset.UTC))
        .channel(Channel.ATM)
        .amount(new BigDecimal("19.59"))
        .fee(new BigDecimal("2.30"))
        .build();

    this.repository.save(transactionModel);

    //When
    final StatusDto response = this.service.verifyTransactionStored(request);
    //Then
    Assertions.assertEquals(request.getReference(), response.getReference());
    Assertions.assertEquals(Status.PENDING, response.getStatus());
    Assertions.assertEquals(transactionModel.getAmount().subtract(transactionModel.getFee()),
        response.getAmount());
  }

  @Test
  void checkTransactionDateIsGraterThanTodayFromInternalhannelTest() {
    final String reference = UUID.randomUUID().toString();
    //case H
    //Given
    var request = StatusDto.builder()
        .reference(reference)
        .channel(Channel.INTERNAL)
        .build();

    var transactionModel = TransactionModel.builder()
        .reference(reference)
        .date(LocalDateTime.of(2022, Month.DECEMBER, 12,
            22, 20).toInstant(ZoneOffset.UTC))
        .channel(Channel.INTERNAL)
        .amount(new BigDecimal("19.59"))
        .fee(new BigDecimal("2.30"))
        .build();

    this.repository.save(transactionModel);

    //When
    final StatusDto response = this.service.verifyTransactionStored(request);
    //Then
    Assertions.assertEquals(request.getReference(), response.getReference());
    Assertions.assertEquals(Status.FUTURE, response.getStatus());
    Assertions.assertEquals(transactionModel.getAmount(), response.getAmount());
    Assertions.assertEquals(transactionModel.getFee(), response.getFee());
  }
}