package com.mstransactionbankapi.mapper;

import com.mstransactionbankapi.dto.RequestTransactionStatusDto;
import com.mstransactionbankapi.dto.ResponseTransactionStatusDto;
import com.mstransactionbankapi.model.TransactionModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionStatusMapper {

  TransactionModel mapperToModel(RequestTransactionStatusDto dto);

  ResponseTransactionStatusDto mapperToDto(TransactionModel model);
}
