package com.mstransactionbankapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
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

  private String description;
}
