package com.example.ledger;

/** Tells the broker an event is done with; an event never acknowledged is redelivered. */
@FunctionalInterface
public interface Acknowledgement {

    void acknowledge();
}
