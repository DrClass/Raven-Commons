package io.github.drclass.commons.math;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class TestConstant {
    @Test
    void calculatePi() {
        assertEquals(Constant.Pi(15), BigDecimal.valueOf(Constant.PI), "Generated value of pi should equal " + Constant.PI);
    }
}
