package com.example.beneficiaries;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/** Holds large payments to beneficiaries added in the last 24 hours for a fraud review (FRD-145). */
public final class PaymentScreening {

    public enum Decision {
        RELEASED,
        HELD
    }

    static final Duration COOLING_OFF = Duration.ofHours(24);
    static final BigDecimal REVIEW_ABOVE = new BigDecimal("1000.00");

    private final BeneficiaryBook book;
    private final Clock clock;

    public PaymentScreening(BeneficiaryBook book, Clock clock) {
        this.book = book;
        this.clock = clock;
    }

    public Decision screen(PaymentOrder order) {
        Instant now = clock.instant();
        Beneficiary payee = book.find(order.creditorIban())
                .orElse(book.add(order.creditorIban(), order.creditorName(), now));
        boolean recentlyAdded = Duration.between(payee.addedAt(), now).compareTo(COOLING_OFF) < 0;
        if (recentlyAdded && order.amount().compareTo(REVIEW_ABOVE) > 0) {
            return Decision.HELD;
        }
        return Decision.RELEASED;
    }
}
