package com.example.status;

import java.math.BigDecimal;

/** Reserved funds on customer accounts. */
public interface Funds {

    /** Takes a reserved amount off the account: the payment has left the bank. */
    void debit(String account, BigDecimal amount);

    /** Gives a reserved amount back to the customer: the payment will not be made. */
    void release(String account, BigDecimal amount);
}
