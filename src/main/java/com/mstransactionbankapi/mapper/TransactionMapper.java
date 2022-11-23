package com.mstransactionbankapi.mapper;

import com.mstransactionbankapi.dto.PayLoadDto;
import com.mstransactionbankapi.dto.StatusDto;
import com.mstransactionbankapi.model.TransactionModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

  TransactionModel mapperToModel(PayLoadDto payLoadDto);

  PayLoadDto mapperToDto(TransactionModel transactionModel);

  StatusDto mapperToStatusDto(TransactionModel transactionModel);

  TransactionModel mapperToModel(StatusDto statusDto);
}
