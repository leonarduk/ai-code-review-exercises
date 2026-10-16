"""One-line descriptions of payment events for the application log.

Card numbers and IBANs are masked before anything is logged (SEC-311).
"""

import re

CARD_NUMBER_IN_TEXT = re.compile(r"\b(?:\d{4}[ -]?){3}\d{4}\b")


def mask_pan(pan: str) -> str:
    """Only the last four digits stay visible."""
    digits = re.sub(r"[ -]", "", pan)
    return "*" * (len(digits) - 4) + digits[-4:]


def mask_iban(iban: str) -> str:
    """Only the country code and the last four characters stay visible."""
    compact = iban.replace(" ", "").upper()
    return compact[:2] + "*" * (len(compact) - 6) + compact[-4:]


def redact(text: str) -> str:
    """Mask every card number that appears in free text."""
    return CARD_NUMBER_IN_TEXT.sub(lambda match: mask_pan(match.group()), text)


def log_line(event: dict) -> str:
    if event["method"] == "card":
        payer = mask_pan(event["payer"])
    else:
        payer = mask_iban(event["payer"])
    return (f"payment {event['payment_id']} {event['amount']} {event['currency']} "
            f"via {event['method']} from={payer} ref={redact(event['reference'])}")
