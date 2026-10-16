package com.example.deposits;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/** A money-market deposit booked by Treasury. Interest is simple, ACT/360. */
public record Deposit(BigDecimal principal, BigDecimal annualRate, LocalDate valueDate, LocalDate maturityDate) {

    private static final BigDecimal DAY_COUNT_BASIS = BigDecimal.valueOf(360);

    public Deposit {
        Objects.requireNonNull(principal, "principal");
        Objects.requireNonNull(annualRate, "annualRate");
        Objects.requireNonNull(valueDate, "valueDate");
        Objects.requireNonNull(maturityDate, "maturityDate");
        if (principal.signum() <= 0) {
            throw new IllegalArgumentException("principal must be positive");
        }
        if (!maturityDate.isAfter(valueDate)) {
            throw new IllegalArgumentException("maturity date must be after the value date");
        }
    }

    /** Days in the deposit period: value date inclusive, maturity date exclusive. */
    public int days() {
        return Period.between(valueDate, maturityDate).getDays();
    }

    /** Interest payable at maturity, rounded to the cent with banker's rounding. */
    public BigDecimal interest() {
        return principal
                .multiply(annualRate)
                .multiply(BigDecimal.valueOf(days()))
                .divide(DAY_COUNT_BASIS, 2, RoundingMode.HALF_EVEN);
    }

    /** Principal plus interest, as paid out at maturity. */
    public BigDecimal maturityAmount() {
        return principal.add(interest());
    }
}
