package com.mstransactionbankapi.dto;

import com.mstransactionbankapi.model.Channel;
import com.mstransactionbankapi.model.Status;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatusDto {

  private String reference;
  private String accountIban;
  private Instant date;
  private BigDecimal amount;
  private BigDecimal fee;
  private Status status;
  private Channel channel;
  private String description;

}
