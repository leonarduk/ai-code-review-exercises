package com.example.allocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FeeAllocatorTest {

    private static Map<String, BigDecimal> balances(String... idsAndBalances) {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (int i = 0; i < idsAndBalances.length; i += 2) {
            map.put(idsAndBalances[i], new BigDecimal(idsAndBalances[i + 1]));
        }
        return map;
    }

    @Test
    void allocatesInProportionToBalance() {
        List<Allocation> result = FeeAllocator.allocate(
                new BigDecimal("1000.00"), balances("ACC-1", "600000.00", "ACC-2", "400000.00"));
        assertEquals(new BigDecimal("600.00"), result.get(0).amount());
        assertEquals(new BigDecimal("400.00"), result.get(1).amount());
    }

    @Test
    void allocationsSumToTheTotal() {
        BigDecimal total = new BigDecimal("100.00");
        List<Allocation> result = FeeAllocator.allocate(
                total, balances("A", "100.00", "B", "100.00", "C", "100.00"));
        BigDecimal sum = result.stream().map(Allocation::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(total, sum);
    }

    @Test
    void invalidInputIsRejected() {
        assertThrows(AllocationException.class,
                () -> FeeAllocator.allocate(new BigDecimal("10.005"), balances("A", "1.00")));
        assertThrows(AllocationException.class,
                () -> FeeAllocator.allocate(new BigDecimal("10.00"), balances()));
        assertThrows(AllocationException.class,
                () -> FeeAllocator.allocate(new BigDecimal("10.00"), balances("A", "-1.00", "B", "2.00")));
    }
}
