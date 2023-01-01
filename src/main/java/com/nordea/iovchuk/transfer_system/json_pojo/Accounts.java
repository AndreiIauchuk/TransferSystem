package com.nordea.iovchuk.transfer_system.json_pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nordea.iovchuk.transfer_system.entity.AccountEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;

@Getter
@Setter
public class Accounts {

    @JsonProperty("accounts")
    private LinkedList<AccountEntity> accountEntities;
}
