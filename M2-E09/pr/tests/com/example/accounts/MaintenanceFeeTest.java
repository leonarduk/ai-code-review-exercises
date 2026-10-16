package com.example.accounts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class MaintenanceFeeTest {

    private static List<BigDecimal> days(int count, String balance) {
        return new ArrayList<>(Collections.nCopies(count, new BigDecimal(balance)));
    }

    @Test
    void averageOfAFlatBalance() {
        assertEquals(new BigDecimal("1200.00"),
                MaintenanceFee.averageDailyBalance(days(30, "1200.00")));
    }

    @Test
    void averageOfAMonthWithADeposit() {
        // 10 days at 1,000.00 then 20 days at 7,000.00: 150,000.00 / 30
        List<BigDecimal> balances = days(10, "1000.00");
        balances.addAll(days(20, "7000.00"));
        assertEquals(new BigDecimal("5000.00"), MaintenanceFee.averageDailyBalance(balances));
    }

    @Test
    void feeIsChargedBelowTheThreshold() {
        assertEquals(new BigDecimal("12.00"), MaintenanceFee.forPeriod(days(30, "1200.00")));
    }

    @Test
    void feeIsWaivedAtTheThreshold() {
        assertEquals(new BigDecimal("0.00"), MaintenanceFee.forPeriod(days(31, "5000.00")));
    }

    @Test
    void aLastDayDepositNoLongerWaivesTheFee() {
        List<BigDecimal> balances = days(29, "100.00");
        balances.add(new BigDecimal("9100.00"));
        assertEquals(new BigDecimal("400.00"), MaintenanceFee.averageDailyBalance(balances));
        assertEquals(new BigDecimal("12.00"), MaintenanceFee.forPeriod(balances));
    }

    @Test
    void anEmptyPeriodIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> MaintenanceFee.forPeriod(List.of()));
    }
}
