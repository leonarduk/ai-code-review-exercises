package com.example.payments;

/** An instruction held for review because it repeats an earlier one. */
public record SuspectedDuplicate(String instructionId, String channel, String firstInstructionId) {
}
