package com.nordea.iovchuk.transfer_system.repository;

import com.nordea.iovchuk.transfer_system.entity.AccountEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class AccountRepositoryTest {

    static final String NUMBER = "NUMBER";

    @Autowired
    AccountRepository repository;

    @Test
    public void whenExistByNumber_thenTrue() {
        repository.save(accountEntity(1));
        assertTrue(repository.existsByNumber(NUMBER + 1));
    }

    @Test
    public void whenThreeAccountsAdded_thenSizeEqualsThree() {
        repository.save(accountEntity(1));
        repository.save(accountEntity(2));
        repository.save(accountEntity(3));
        List<AccountEntity> accountEntities = repository.findAll();
        assertEquals(3, accountEntities.size());
    }

    @Test
    public void whenNoAccountAdded_thenEmpty() {
        List<AccountEntity> accountEntities = repository.findAll();
        assertThat(accountEntities).isEmpty();
    }

    @Test
    void whenNumberIsNotPresent_thenThrowDataIntegrityViolationException() {
        AccountEntity accountEntity = new AccountEntity();
        assertThrows(DataIntegrityViolationException.class, () -> repository.save(accountEntity));
    }

    @Test
    void whenNumberIsAlreadyExists_thenThrowDataIntegrityViolationException() {
        repository.save(accountEntity(1));
        assertThrows(DataIntegrityViolationException.class, () -> repository.save(accountEntity(1)));
    }

    private AccountEntity accountEntity(int number) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setNumber(NUMBER + number);
        return accountEntity;
    }
}
