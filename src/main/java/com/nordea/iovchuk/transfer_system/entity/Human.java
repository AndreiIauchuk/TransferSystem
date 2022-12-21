package com.nordea.iovchuk.transfer_system.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Human implements Serializable {

    private final String name;
    private final String surname;
}
