package com.johan.misgastos.ui.screens.settings

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CurrencyCodeValidationTest {

    @Test
    fun `isValidCurrencyCode accepts uppercase 3-letter values`() {
        assertTrue(isValidCurrencyCode("COP"))
        assertTrue(isValidCurrencyCode("USD"))
    }

    @Test
    fun `isValidCurrencyCode rejects values with invalid length or characters`() {
        assertFalse(isValidCurrencyCode("CO"))
        assertFalse(isValidCurrencyCode("USDD"))
        assertFalse(isValidCurrencyCode("C0P"))
        assertFalse(isValidCurrencyCode("U\$D"))
    }
}
