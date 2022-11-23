package com.mstransactionbankapi.dto;

import com.mstransactionbankapi.model.Channel;
import com.mstransactionbankapi.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayLoadDto {

  private String reference;
  @NotNull
  @NotBlank
  private String accountIban;
  private Instant date;
  @NonNull
  private BigDecimal amount;
  private BigDecimal fee;
  private Status status;
  private Channel channel;
  private String description;

}
