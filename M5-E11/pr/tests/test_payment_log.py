from payment_log import log_line, mask_iban, mask_pan, redact


def test_card_numbers_show_only_the_last_four_digits():
    assert mask_pan("4111111111111111") == "************1111"
    assert mask_pan("4111 1111 1111 1111") == "************1111"


def test_ibans_show_only_the_country_and_last_four():
    assert mask_iban("DE89 3704 0044 0532 0130 00") == "DE****************3000"


def test_card_numbers_in_free_text_are_masked():
    assert redact("customer typed 5500-0000-0000-0004 here") == "customer typed ************0004 here"
    assert redact("invoice 2291") == "invoice 2291"


def test_log_line_masks_the_payer_and_the_reference():
    line = log_line({"payment_id": "pay_1", "amount": "25.00", "currency": "EUR", "method": "card",
                     "payer": "4111111111111111", "reference": "order 4111 1111 1111 1111"})
    assert line == "payment pay_1 25.00 EUR via card from=************1111 ref=order ************1111"
    line = log_line({"payment_id": "pay_2", "amount": "80.00", "currency": "EUR", "method": "sepa",
                     "payer": "DE89370400440532013000", "reference": "rent"})
    assert "from=DE****************3000" in line
