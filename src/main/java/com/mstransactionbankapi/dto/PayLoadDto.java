package com.mstransactionbankapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record PayLoadDto(String reference,
                         @NotNull @NotBlank
                         String accountIban,
                         Instant date,
                         @NonNull
                         BigDecimal amount,
                         BigDecimal fee,
                         String description) {

}
