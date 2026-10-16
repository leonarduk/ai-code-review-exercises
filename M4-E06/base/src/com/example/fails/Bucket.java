package com.example.fails;

/** How the operations team handles a settlement fail of a given age. */
public enum Bucket {
    CURRENT,
    MONITOR,
    ESCALATE,
    BUY_IN
}
