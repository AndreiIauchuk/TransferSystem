package com.nordea.iovchuk.transfer_system.repository;

import com.nordea.iovchuk.transfer_system.entity.AccountEntity;
import com.nordea.iovchuk.transfer_system.entity.CurrencyAmountEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class CurrencyAmountRepositoryTest {

    static final String CURRENCY = "CUR";

    @Autowired
    CurrencyAmountRepository repository;

    @Autowired
    AccountRepository accountRepository;

    @Test
    public void whenExistByAccNumberAndCurrency_thenTrue() {
        String accNumber = "number";
        CurrencyAmountEntity currencyAmountEntity = currencyAmountEntity();
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setNumber(accNumber);
        List<CurrencyAmountEntity> currencyAmountEntities = new ArrayList<>();
        currencyAmountEntities.add(currencyAmountEntity);
        accountEntity.setCurrencyAmount(currencyAmountEntities);
        currencyAmountEntity.setAccount(accountEntity);
        accountRepository.save(accountEntity);
        Optional<CurrencyAmountEntity> optionalCurrencyAmountEntity =
                repository.findByAccount_NumberAndCurrency(accNumber, CURRENCY);
        CurrencyAmountEntity foundCurrencyAmountEntity = optionalCurrencyAmountEntity.get();
        assertEquals(currencyAmountEntity, foundCurrencyAmountEntity);
    }

    @Test
    public void whenThreeAccountsAdded_thenSizeEqualsThree() {
        repository.save(currencyAmountEntity());
        repository.save(currencyAmountEntity());
        repository.save(currencyAmountEntity());
        List<CurrencyAmountEntity> accountEntities = repository.findAll();
        assertEquals(3, accountEntities.size());
    }

    @Test
    public void whenNoAccountAdded_thenEmpty() {
        List<CurrencyAmountEntity> currencyAmountEntities = repository.findAll();
        assertThat(currencyAmountEntities).isEmpty();
    }

    @Test
    void whenCurrencyIsNotPresent_thenThrowDataIntegrityViolationException() {
        CurrencyAmountEntity currencyAmountEntity = new CurrencyAmountEntity();
        assertThrows(DataIntegrityViolationException.class, () -> repository.save(currencyAmountEntity));
    }

    @Test
    void whenCurrencyIsTooLong_thenThrowDataIntegrityViolationException() {
        CurrencyAmountEntity currencyAmountEntity = new CurrencyAmountEntity();
        currencyAmountEntity.setCurrency("veryLongCurrency");
        assertThrows(DataIntegrityViolationException.class, () -> repository.save(currencyAmountEntity));
    }

    private CurrencyAmountEntity currencyAmountEntity() {
        CurrencyAmountEntity currencyAmountEntity = new CurrencyAmountEntity();
        currencyAmountEntity.setCurrency(CURRENCY);
        currencyAmountEntity.setAmount(BigDecimal.valueOf(1));
        return currencyAmountEntity;
    }
}
