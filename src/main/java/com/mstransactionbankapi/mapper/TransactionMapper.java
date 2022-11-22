package com.mstransactionbankapi.mapper;

import com.mstransactionbankapi.dto.PayLoadDto;
import com.mstransactionbankapi.model.TransactionModel;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

  TransactionModel mapperToModel(PayLoadDto dto);

  PayLoadDto mapperToDto(TransactionModel model);

  List<PayLoadDto> mapperToListDto(List<TransactionModel> transactions);
}
