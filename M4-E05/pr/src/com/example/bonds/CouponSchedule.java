package com.example.bonds;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Unadjusted coupon schedule for fixed-rate bonds (BND-640). */
public final class CouponSchedule {

    private CouponSchedule() {
    }

    /** Coupon dates from the first coupon date to maturity, frequency times a year. */
    public static List<LocalDate> dates(FixedRateBond bond) {
        int months = monthsBetweenCoupons(bond.frequency());
        if (bond.maturityDate().isBefore(bond.firstCouponDate())) {
            throw new IllegalArgumentException("first coupon date is after maturity");
        }
        List<LocalDate> dates = new ArrayList<>();
        LocalDate date = bond.firstCouponDate();
        while (date.isBefore(bond.maturityDate())) {
            dates.add(date);
            date = date.plusMonths(months);
        }
        return List.copyOf(dates);
    }

    /** Every coupon pays notional x annual rate / frequency, rounded half-even to the cent. */
    public static List<Coupon> coupons(FixedRateBond bond) {
        BigDecimal amount = bond.notional()
                .multiply(bond.annualRate())
                .divide(BigDecimal.valueOf(bond.frequency()), 2, RoundingMode.HALF_EVEN);
        return dates(bond).stream().map(date -> new Coupon(date, amount)).toList();
    }

    private static int monthsBetweenCoupons(int frequency) {
        if (frequency <= 0 || 12 % frequency != 0) {
            throw new IllegalArgumentException("frequency must divide 12: " + frequency);
        }
        return 12 / frequency;
    }
}
