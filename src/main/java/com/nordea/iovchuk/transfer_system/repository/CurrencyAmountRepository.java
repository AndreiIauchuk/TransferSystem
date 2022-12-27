package com.nordea.iovchuk.transfer_system.repository;

import com.nordea.iovchuk.transfer_system.entity.CurrencyAmountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyAmountRepository extends JpaRepository<CurrencyAmountEntity, Integer> {
}
