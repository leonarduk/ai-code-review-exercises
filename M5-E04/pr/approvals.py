"""Second-approval and daily-limit rules for outgoing payments (PAY-2291)."""

from decimal import ROUND_HALF_EVEN, Decimal

from money import Money

APPROVAL_THRESHOLD = Money(Decimal("10000.00"), "USD")
DAILY_LIMIT = Money(Decimal("50000.00"), "USD")
CENT = Decimal("0.01")


class UnknownCurrency(Exception):
    """No USD rate for the currency."""


class ApprovalPolicy:
    def __init__(self, usd_per_unit: dict[str, Decimal]):
        self._rates = {**usd_per_unit, "USD": Decimal("1")}

    def to_usd(self, money: Money) -> Money:
        rate = self._rates.get(money.currency)
        if rate is None:
            raise UnknownCurrency(money.currency)
        return Money((money.amount * rate).quantize(CENT, rounding=ROUND_HALF_EVEN), "USD")

    def needs_second_approval(self, payment: Money) -> bool:
        """Payments worth 10,000.00 USD or more need a second approver."""
        return payment >= APPROVAL_THRESHOLD

    def within_daily_limit(self, paid_today: list[Money], payment: Money) -> bool:
        """The day's payments, including this one, may total at most 50,000.00 USD."""
        total = sum((self.to_usd(m) for m in paid_today), Money(Decimal("0.00"), "USD"))
        return total + self.to_usd(payment) <= DAILY_LIMIT
