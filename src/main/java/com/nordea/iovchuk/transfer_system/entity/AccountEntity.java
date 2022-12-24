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
@Table(name = "account", schema = "transfer_system")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class AccountEntity {

    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "number", nullable = false, length = 50, unique = true)
    private String number;

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
