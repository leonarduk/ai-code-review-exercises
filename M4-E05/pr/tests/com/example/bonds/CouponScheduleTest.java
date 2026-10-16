package com.example.bonds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class CouponScheduleTest {

    private static FixedRateBond bond(LocalDate first, LocalDate maturity, int frequency) {
        return new FixedRateBond("XS0000000001", new BigDecimal("1000000.00"), new BigDecimal("0.0425"),
                first, maturity, frequency);
    }

    @Test
    void semiAnnualDates() {
        FixedRateBond b = bond(LocalDate.of(2026, 3, 15), LocalDate.of(2028, 3, 15), 2);
        assertEquals(List.of(LocalDate.of(2026, 3, 15), LocalDate.of(2026, 9, 15), LocalDate.of(2027, 3, 15),
                LocalDate.of(2027, 9, 15)), CouponSchedule.dates(b).subList(0, 4));
    }

    @Test
    void quarterlyCouponAmount() {
        FixedRateBond b = bond(LocalDate.of(2026, 1, 10), LocalDate.of(2027, 1, 10), 4);
        assertEquals(new BigDecimal("10625.00"), CouponSchedule.coupons(b).get(0).amount());
    }

    @Test
    void annualScheduleStartsOnTheFirstCouponDate() {
        FixedRateBond b = bond(LocalDate.of(2026, 6, 1), LocalDate.of(2030, 6, 1), 1);
        assertEquals(LocalDate.of(2026, 6, 1), CouponSchedule.dates(b).get(0));
        assertEquals(LocalDate.of(2027, 6, 1), CouponSchedule.dates(b).get(1));
    }

    @Test
    void frequencyMustDivideTwelve() {
        assertThrows(IllegalArgumentException.class,
                () -> CouponSchedule.dates(bond(LocalDate.of(2026, 1, 10), LocalDate.of(2027, 1, 10), 5)));
    }
}
