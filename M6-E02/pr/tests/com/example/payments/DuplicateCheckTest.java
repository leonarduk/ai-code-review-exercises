package com.example.payments;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class DuplicateCheckTest {

    private static final LocalDate MONDAY = LocalDate.of(2026, 10, 19);

    private static PaymentInstruction pay(String id, String channel, String creditor, String amount, LocalDate date) {
        return new PaymentInstruction(id, channel, "GB29NWBK60161331926819", creditor, new BigDecimal(amount), "GBP", date);
    }

    @Test
    void theSamePaymentThroughTwoChannelsIsFlagged() {
        List<SuspectedDuplicate> result = DuplicateCheck.check(List.of(
                pay("F-1", "FILE", "GB33BUKB20201555555555", "250.00", MONDAY),
                pay("A-1", "API", "GB33BUKB20201555555555", "250.00", MONDAY)));
        assertEquals(List.of(new SuspectedDuplicate("A-1", "API", "F-1")), result);
    }

    @Test
    void ibansMatchWhateverTheSpacingAndCase() {
        List<SuspectedDuplicate> result = DuplicateCheck.check(List.of(
                pay("F-1", "FILE", "GB33 BUKB 2020 1555 5555 55", "99.95", MONDAY),
                pay("B-1", "BRANCH", "gb33bukb20201555555555", "99.95", MONDAY)));
        assertEquals(1, result.size());
    }

    @Test
    void aDifferentExecutionDateIsADifferentPayment() {
        assertEquals(List.of(), DuplicateCheck.check(List.of(
                pay("F-1", "FILE", "GB33BUKB20201555555555", "250.00", MONDAY),
                pay("F-2", "FILE", "GB33BUKB20201555555555", "250.00", MONDAY.plusDays(1)))));
    }

    @Test
    void everyRepeatPointsAtTheFirstInstruction() {
        List<SuspectedDuplicate> result = DuplicateCheck.check(List.of(
                pay("F-1", "FILE", "GB33BUKB20201555555555", "10.00", MONDAY),
                pay("A-1", "API", "GB33BUKB20201555555555", "10.00", MONDAY),
                pay("A-2", "API", "GB33BUKB20201555555555", "10.00", MONDAY)));
        assertEquals(List.of(new SuspectedDuplicate("A-1", "API", "F-1"), new SuspectedDuplicate("A-2", "API", "F-1")),
                result);
    }

    @Test
    void releasableKeepsTheFirstOfEachPayment() {
        IntakeQueue queue = new IntakeQueue();
        queue.add(pay("F-1", "FILE", "GB33BUKB20201555555555", "10.00", MONDAY));
        queue.add(pay("A-1", "API", "GB33BUKB20201555555555", "10.00", MONDAY));
        queue.add(pay("A-2", "API", "GB94BARC10201530093459", "10.00", MONDAY));
        assertEquals(List.of("F-1", "A-2"),
                DuplicateCheck.releasable(queue).stream().map(PaymentInstruction::instructionId).toList());
    }
}
