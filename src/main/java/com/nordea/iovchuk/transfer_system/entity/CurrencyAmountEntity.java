package com.nordea.iovchuk.transfer_system.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "currency_amount", schema = "transfer_system")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CurrencyAmountEntity {

    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "account_id")
    private Integer accountId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CurrencyAmountEntity that = (CurrencyAmountEntity) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 272295389;
    }
}
