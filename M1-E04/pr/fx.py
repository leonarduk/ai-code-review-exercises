"""Currency conversion helpers."""

from __future__ import annotations

from decimal import Decimal, ROUND_HALF_UP
from typing import Mapping

CENT = Decimal("0.01")
USD = "USD"

# ISO 4217 minor units for the currencies we quote.
MINOR_UNITS: dict[str, int] = {"CHF": 2, "EUR": 2, "GBP": 2, "JPY": 0, "USD": 2}


class UnknownCurrencyError(KeyError):
    """The currency is not supported, or no rate is available for it."""


def to_usd(amount: Decimal, usd_per_unit: Decimal) -> Decimal:
    """Convert ``amount`` to USD, given the USD price of one unit."""
    return (amount * usd_per_unit).quantize(CENT)


def minor_units(currency: str) -> int:
    try:
        return MINOR_UNITS[currency]
    except KeyError:
        raise UnknownCurrencyError(currency) from None


class RateTable:
    """USD value of one unit of each currency, built from market quotes.

    Quotes use market convention ``BASE/QUOTE``: the price of one unit of
    BASE in QUOTE, e.g. ``EUR/USD 1.0850`` or ``USD/JPY 151.20``.
    """

    def __init__(self, quotes: Mapping[str, Decimal]):
        self._usd_per_unit: dict[str, Decimal] = {USD: Decimal(1)}
        for pair, rate in quotes.items():
            base, quote = pair.split("/")
            if quote == USD:
                self._usd_per_unit[base] = rate
            elif base == USD:
                self._usd_per_unit[quote] = rate
            else:
                raise ValueError(f"only USD pairs are supported, got {pair}")

    def usd_per_unit(self, currency: str) -> Decimal:
        try:
            return self._usd_per_unit[currency]
        except KeyError:
            raise UnknownCurrencyError(currency) from None


def round_to_minor_units(amount: Decimal, currency: str) -> Decimal:
    quantum = Decimal(1).scaleb(-minor_units(currency))
    return amount.quantize(quantum, rounding=ROUND_HALF_UP)


def convert(amount: Decimal, source: str, target: str, rates: RateTable) -> Decimal:
    """Convert ``amount`` from ``source`` to ``target``, crossing through USD.

    The result is rounded to the target currency's minor units.
    """
    if source == target:
        return round_to_minor_units(amount, target)
    usd = to_usd(amount, rates.usd_per_unit(source))
    return round_to_minor_units(usd / rates.usd_per_unit(target), target)
