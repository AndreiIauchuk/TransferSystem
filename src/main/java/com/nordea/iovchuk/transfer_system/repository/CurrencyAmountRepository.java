package com.nordea.iovchuk.transfer_system.repository;

import com.nordea.iovchuk.transfer_system.entity.CurrencyAmountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyAmountRepository extends JpaRepository<CurrencyAmountEntity, Integer> {

    Optional<CurrencyAmountEntity> findByAccount_NumberAndCurrency(
            final String accountNumber,
            final String currency
    );
}
