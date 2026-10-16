package com.example.bonds;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/** A plain fixed-rate bond: equal coupons, frequency payments a year. */
public record FixedRateBond(String isin, BigDecimal notional, BigDecimal annualRate, LocalDate firstCouponDate,
        LocalDate maturityDate, int frequency) {

    public FixedRateBond {
        Objects.requireNonNull(isin, "isin");
        Objects.requireNonNull(notional, "notional");
        Objects.requireNonNull(annualRate, "annualRate");
        Objects.requireNonNull(firstCouponDate, "firstCouponDate");
        Objects.requireNonNull(maturityDate, "maturityDate");
    }
}
