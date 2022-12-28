package com.nordea.iovchuk.transfer_system.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "account", schema = "transfer_system")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "number", nullable = false, length = 50, unique = true)
    @JsonProperty("accountNumber")
    private String number;

    @OneToMany(
            mappedBy = "account",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @ToString.Exclude
    @JsonProperty("currencyAmounts")
    private List<CurrencyAmountEntity> currencyAmount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AccountEntity that = (AccountEntity) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 1402876057;
    }
}
