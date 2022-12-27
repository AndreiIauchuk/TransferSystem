package com.nordea.iovchuk.transfer_system.repository;

import com.nordea.iovchuk.transfer_system.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {

    boolean existsByNumber(final String number);
}
