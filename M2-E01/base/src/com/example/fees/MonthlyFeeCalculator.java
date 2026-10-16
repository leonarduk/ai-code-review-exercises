package com.example.fees;

import java.util.Optional;

/** Monthly management fee, charged in arrears on assets under management. */
public final class MonthlyFeeCalculator {

    private final FeeSchedule schedule;

    public MonthlyFeeCalculator(FeeSchedule schedule) {
        this.schedule = schedule;
    }

    public double monthlyFee(double assetsUnderManagement) {
        return Math.round(assetsUnderManagement * schedule.annualRate() / 12 * 100) / 100.0;
    }

    public Optional<InvoiceLine> invoiceLine(String accountId, double assetsUnderManagement) {
        double fee = monthlyFee(assetsUnderManagement);
        if (fee == 0.0) {
            return Optional.empty();
        }
        return Optional.of(new InvoiceLine(accountId, "Management fee", fee));
    }
}
