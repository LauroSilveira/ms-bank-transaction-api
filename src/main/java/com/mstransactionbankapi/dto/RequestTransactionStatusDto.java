package com.mstransactionbankapi.dto;

import com.mstransactionbankapi.model.Channel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RequestTransactionStatusDto(@NotBlank @NotNull String reference,
                                          Channel channel) {

}
