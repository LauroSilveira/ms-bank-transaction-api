package com.mstransactionbankapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.io.Serializable;
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
@Entity
@NoArgsConstructor
public class TransactionModel implements Serializable {

  @Id
  private String reference;

  private String accountIban;

  private Instant date;

  private BigDecimal amount;

  private BigDecimal fee;

  @Enumerated(EnumType.STRING)
  private Status status;

  @Enumerated(EnumType.STRING)
  private Channel channel;

  private String description;
}
