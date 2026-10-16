package com.example.bonds;

import java.math.BigDecimal;
import java.time.LocalDate;

/** One scheduled coupon payment. */
public record Coupon(LocalDate paymentDate, BigDecimal amount) {
}
