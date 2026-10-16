"""One-line descriptions of payment events for the application log."""


def log_line(event: dict) -> str:
    return (f"payment {event['payment_id']} {event['amount']} {event['currency']} "
            f"via {event['method']} from={event['payer']} ref={event['reference']}")
