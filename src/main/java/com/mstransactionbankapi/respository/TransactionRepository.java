package com.mstransactionbankapi.respository;

import com.mstransactionbankapi.model.TransactionModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionModel, String> {

  Optional<TransactionModel> findByAccountIban(final String accountIban);

  Optional<TransactionModel> findByReference(final String reference);
}
