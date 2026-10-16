"""Currency-aware rounding, parsing and formatting of monetary amounts."""

from __future__ import annotations

from decimal import Decimal, InvalidOperation, ROUND_HALF_EVEN

# ISO 4217 minor units for the currencies we settle in.
MINOR_UNITS: dict[str, int] = {
    "CHF": 2,
    "EUR": 2,
    "GBP": 2,
    "JPY": 0,
    "KWD": 3,
    "USD": 2,
}


class MoneyError(ValueError):
    """Base class for monetary validation errors."""


class UnsupportedCurrencyError(MoneyError):
    """Raised for a currency code we do not settle in."""


class InvalidAmountError(MoneyError):
    """Raised when an amount string cannot be parsed."""


def minor_units(currency: str) -> int:
    """Number of decimal places used by ``currency``."""
    return MINOR_UNITS.get(currency.upper(), 2)


def quantum(currency: str) -> Decimal:
    """The smallest unit of ``currency``, e.g. Decimal('0.01') for USD."""
    return Decimal(1).scaleb(-minor_units(currency))


def round_amount(amount: Decimal, currency: str) -> Decimal:
    """Round ``amount`` to the minor units of ``currency`` (banker's rounding)."""
    return amount.quantize(quantum(currency), rounding=ROUND_HALF_EVEN)


def parse_amount(text: str, currency: str) -> Decimal:
    """Parse an amount string from an upload file, rounded for ``currency``.

    The decimal separator is a dot; no thousands separators.
    """
    try:
        value = Decimal(text.strip())
    except InvalidOperation as exc:
        raise InvalidAmountError(f"cannot parse amount {text!r}: {exc.message}") from exc
    if not value.is_finite():
        raise InvalidAmountError(f"amount must be finite, got {text!r}")
    return round_amount(value, currency)


def format_amount(amount: Decimal, currency: str) -> str:
    """Format ``amount`` with thousands separators, e.g. '1,234.50 USD'."""
    places = minor_units(currency)
    return f"{round_amount(amount, currency):,.{places}f} {currency.upper()}"
