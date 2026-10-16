package com.example.marketdata;

import java.time.Instant;
import java.util.Objects;

/** Captures a published benchmark fixing (WM/R 4pm, the monthly index close) into the rates store. */
public record FixCaptureJob(String name, String source) {

    public FixCaptureJob {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
    }

    public String describe(Instant runAt) {
        return name + " from " + source + " at " + runAt;
    }
}
