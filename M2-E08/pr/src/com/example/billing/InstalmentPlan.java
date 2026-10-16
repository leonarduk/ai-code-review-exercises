package com.example.billing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Splits an invoice total into equal instalments. */
public final class InstalmentPlan {

    private static final int CENT_SCALE = 2;

    private InstalmentPlan() {
    }

    /**
     * Splits {@code total} into {@code count} instalments that sum exactly to
     * {@code total}. Every instalment but the last is total / count rounded
     * toward zero to the cent; the last takes whatever remains.
     */
    public static List<BigDecimal> split(BigDecimal total, int count) {
        Objects.requireNonNull(total, "total");
        if (count < 1) {
            throw new IllegalArgumentException("count must be at least 1, got " + count);
        }
        if (total.stripTrailingZeros().scale() > CENT_SCALE) {
            throw new IllegalArgumentException("total has fractions of a cent: " + total);
        }
        BigDecimal regular = total.divide(BigDecimal.valueOf(count), CENT_SCALE, RoundingMode.DOWN);
        List<BigDecimal> instalments = new ArrayList<>(Collections.nCopies(count - 1, regular));
        BigDecimal last = total.subtract(regular.multiply(BigDecimal.valueOf(count - 1)));
        instalments.add(last.setScale(CENT_SCALE));
        return instalments;
    }
}
