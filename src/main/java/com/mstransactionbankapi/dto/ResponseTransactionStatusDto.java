package com.mstransactionbankapi.dto;

import com.mstransactionbankapi.model.Status;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record ResponseTransactionStatusDto(String reference, Status status, BigDecimal amount,
                                           BigDecimal fee) {

}
