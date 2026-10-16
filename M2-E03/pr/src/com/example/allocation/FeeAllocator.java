package com.example.allocation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Pro-rata allocation of a platform fee across client accounts. */
public final class FeeAllocator {

    private static final int CENT_SCALE = 2;

    private FeeAllocator() {
    }

    /**
     * Splits {@code total} across accounts in proportion to their balances.
     *
     * <p>Each share is rounded to the cent, half-even. The rounding residue is
     * added to the account with the largest balance (the first such account on
     * a tie), so the allocated amounts always sum to {@code total}. The result
     * keeps the iteration order of {@code balances}.
     */
    public static List<Allocation> allocate(BigDecimal total, Map<String, BigDecimal> balances) {
        validate(total, balances);
        BigDecimal totalBalance = balances.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalBalance.signum() == 0) {
            throw new AllocationException("cannot allocate across a zero total balance");
        }

        Map<String, BigDecimal> amounts = new LinkedHashMap<>();
        for (Map.Entry<String, BigDecimal> account : balances.entrySet()) {
            BigDecimal weight = account.getValue().divide(totalBalance, RoundingMode.HALF_EVEN);
            amounts.put(account.getKey(), total.multiply(weight).setScale(CENT_SCALE, RoundingMode.HALF_EVEN));
        }

        BigDecimal allocated = amounts.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        amounts.merge(largestBalance(balances), total.subtract(allocated), BigDecimal::add);

        List<Allocation> result = new ArrayList<>();
        amounts.forEach((id, amount) -> result.add(new Allocation(id, balances.get(id), amount)));
        return result;
    }

    private static void validate(BigDecimal total, Map<String, BigDecimal> balances) {
        if (total.compareTo(total.setScale(CENT_SCALE, RoundingMode.HALF_EVEN)) != 0) {
            throw new AllocationException("total " + total + " is not a whole number of cents");
        }
        if (balances.isEmpty()) {
            throw new AllocationException("no accounts to allocate across");
        }
        if (balances.values().stream().anyMatch(b -> b.signum() < 0)) {
            throw new AllocationException("balances must not be negative");
        }
    }

    private static String largestBalance(Map<String, BigDecimal> balances) {
        String largest = null;
        for (Map.Entry<String, BigDecimal> account : balances.entrySet()) {
            if (largest == null || account.getValue().compareTo(balances.get(largest)) > 0) {
                largest = account.getKey();
            }
        }
        return largest;
    }
}
