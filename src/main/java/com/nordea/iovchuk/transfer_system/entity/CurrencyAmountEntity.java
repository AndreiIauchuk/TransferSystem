package com.nordea.iovchuk.transfer_system.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "currency_amount", schema = "transfer_system")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrencyAmountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "currency", nullable = false, length = 3)
    @JsonProperty("currency")
    private String currency;

    @Column(name = "amount")
    @JsonProperty("amount")
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @ToString.Exclude
    private AccountEntity account;

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
