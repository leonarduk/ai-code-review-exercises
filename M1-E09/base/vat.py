"""VAT rates and VAT-exclusive pricing."""

from decimal import Decimal, ROUND_HALF_EVEN

CENT = Decimal("0.01")

VAT_RATES = {
    "S": Decimal("0.20"),  # standard
    "R": Decimal("0.05"),  # reduced
    "Z": Decimal("0.00"),  # zero-rated
}


def vat_rate(code: str) -> Decimal:
    """The VAT rate for a VAT code; unknown codes raise ValueError."""
    try:
        return VAT_RATES[code]
    except KeyError:
        raise ValueError(f"unknown VAT code {code!r}") from None


def add_vat(net: Decimal, code: str) -> Decimal:
    """Gross price for a VAT-exclusive net price, VAT rounded half-even to the cent."""
    vat = (net * vat_rate(code)).quantize(CENT, rounding=ROUND_HALF_EVEN)
    return net + vat
