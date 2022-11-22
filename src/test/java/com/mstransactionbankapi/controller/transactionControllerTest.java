package com.mstransactionbankapi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class transactionControllerTest {

  @MockBean
  TransactionController controller;

  @Test
  void should_create_transaction_ok_test() {
     // controller.createTransaction();
  }

}