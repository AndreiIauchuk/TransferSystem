package com.nordea.iovchuk.transfer_system;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

class BrokenTest {

    @Test
    public void brokenTest() {
        System.out.println("HI MOM!");
        fail();
    }
}
