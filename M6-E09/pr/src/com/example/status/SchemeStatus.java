package com.example.status;

/** Transaction status codes in the scheme's pain.002 status reports. */
public enum SchemeStatus {
    /** Accepted for processing. */
    ACCP,
    /** Pending at the scheme. */
    PDNG,
    /** Accepted, settlement in process. */
    ACSP,
    /** Settlement completed. */
    ACSC,
    /** Rejected. */
    RJCT
}
