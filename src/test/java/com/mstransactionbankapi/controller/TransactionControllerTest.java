package com.mstransactionbankapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mstransactionbankapi.dto.PayLoadDto;
import com.mstransactionbankapi.dto.StatusDto;
import com.mstransactionbankapi.model.Channel;
import com.mstransactionbankapi.model.Status;
import com.mstransactionbankapi.service.TransactionService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@WebMvcTest
class TransactionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TransactionService service;

  @SpyBean
  private TransactionController controller;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void save_transaction_ok_test() throws Exception {
    //GIVEN
    var payLoadDto = PayLoadDto.builder()
        .accountIban("ES9820385778983000760237")
        .date(Instant.now())
        .amount(new BigDecimal("12.99"))
        .fee(new BigDecimal("3.18"))
        .build();

    //WHEN
    Mockito.when(service.saveTransaction(ArgumentMatchers.any())).thenReturn(payLoadDto);

    //THEN
    MvcResult mvcResult = this.mockMvc.perform(
            MockMvcRequestBuilders.post("/transaction/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payLoadDto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andReturn();

    final PayLoadDto response = this.objectMapper.readValue(
        mvcResult.getResponse().getContentAsString(), PayLoadDto.class);

    Assertions.assertEquals(response.getAccountIban(), response.getAccountIban());
    Assertions.assertEquals(response.getDate(), response.getDate());
    Assertions.assertEquals(response.getAmount(), response.getAmount());
  }

  @Test
  void should_retrurn_all_transactions_stored_test() throws Exception {
    //GIVEN
    List<PayLoadDto> payload = List.of(PayLoadDto.builder()
            .accountIban("ES9820385778983000760238")
            .date(Instant.now())
            .amount(new BigDecimal("12.99"))
            .fee(new BigDecimal("3.18"))
            .build(),
        PayLoadDto.builder()
            .accountIban("ES9820385778983000760239")
            .date(Instant.now())
            .amount(new BigDecimal("12.99"))
            .fee(new BigDecimal("3.18"))
            .build(),
        PayLoadDto.builder()
            .accountIban("ES9820385778983000760240")
            .date(Instant.now())
            .amount(new BigDecimal("12.99"))
            .fee(new BigDecimal("3.18"))
            .build());

    // WHEN

    Mockito.when(service.getAllTransactions("asc")).thenReturn(payload);

    this.mockMvc.perform(MockMvcRequestBuilders.get("/transaction/all/{sort}", "asc")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
  }

  @Test
  void should_retrurn_empty_collection_when_theres_nothing_stored_test() throws Exception {
    // GIVEN
    Mockito.when(service.getAllTransactions("asc")).thenReturn(Collections.emptyList());

    MvcResult response = this.mockMvc.perform(
            MockMvcRequestBuilders.get("/transaction/all/{sort}", "asc")
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andReturn();

    Assertions.assertTrue(response.getResponse().getContentAsString().isEmpty());
  }

  @Test
  void should_return_200_find_a_transaction_by_accountIban_test() throws Exception {
    //GIVEN
    var payload = PayLoadDto.builder()
        .accountIban("ES9820385778983000760238")
        .date(Instant.now())
        .amount(new BigDecimal("12.99"))
        .fee(new BigDecimal("3.18"))
        .build();

    //WHEN
    Mockito.when(service.getTransactionByAccountIban("ES9820385778983000760238"))
        .thenReturn(payload);

    //THEN
    this.mockMvc.perform(
            MockMvcRequestBuilders.get("/transaction/{accountIban}", "ES9820385778983000760238")
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.accountIban", Is.is("ES9820385778983000760238")));
  }

  @Test
  void should_return_not_found_get_transaction_by_accountIban_test() throws Exception {

    //WHEN
    Mockito.when(service.getTransactionByAccountIban(ArgumentMatchers.anyString()))
        .thenReturn(null);

    //THEN
    MvcResult response = this.mockMvc.perform(
            MockMvcRequestBuilders.get("/transaction/{accountIban}", "ES9820385778983000760238")
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andReturn();

    Assertions.assertEquals("", response.getResponse().getContentAsString());
  }

  @Test
  void should_invalid_transaction_test() throws Exception {
    //GIVEN
    final StatusDto request = StatusDto.builder()
        .reference(UUID.randomUUID().toString())
        .accountIban(UUID.randomUUID().toString())
        .amount(new BigDecimal("12.99"))
        .channel(Channel.CLIENT)
        .status(Status.INVALID)
        .build();

    //WHEN
    Mockito.when(service.verifyTransactionStored(ArgumentMatchers.any()))
        .thenReturn(request);

    //THEN
    MvcResult mvcResult = this.mockMvc.perform(
            MockMvcRequestBuilders.post("/transaction/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andReturn();

    final StatusDto response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
        StatusDto.class);

    Assertions.assertAll(
        () -> Assertions.assertEquals(request.getReference(), response.getReference()),
        () -> Assertions.assertEquals(request.getStatus(), response.getStatus()),
        () -> Assertions.assertEquals(Status.INVALID, response.getStatus()));
  }
}