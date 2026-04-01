package com.johan.misgastos.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AmountUtilsTest {

    @Test
    fun `parseAmountInputToCents parses integer input`() {
        assertEquals(125000L, parseAmountInputToCents("1250"))
    }

    @Test
    fun `parseAmountInputToCents parses decimal input with comma`() {
        assertEquals(129950L, parseAmountInputToCents("1299,50"))
    }

    @Test
    fun `parseAmountInputToCents parses decimal input with dot`() {
        assertEquals(1234L, parseAmountInputToCents("12.34"))
    }

    @Test
    fun `parseAmountInputToCents parses grouped values with mixed separators`() {
        assertEquals(123456L, parseAmountInputToCents("1.234,56"))
        assertEquals(123456L, parseAmountInputToCents("1,234.56"))
    }

    @Test
    fun `parseAmountInputToCents treats single separators with three trailing digits as grouped thousands`() {
        assertEquals(123400L, parseAmountInputToCents("1.234"))
        assertEquals(123400L, parseAmountInputToCents("1,234"))
    }

    @Test
    fun `parseAmountInputToCents rejects zero and invalid values`() {
        assertNull(parseAmountInputToCents("0"))
        assertNull(parseAmountInputToCents("abc"))
    }

    @Test
    fun `formatAmountInputFromCents removes unnecessary decimals`() {
        assertEquals("1250", formatAmountInputFromCents(125000L))
        assertEquals("1299,5", formatAmountInputFromCents(129950L))
    }

    @Test
    fun `formatCurrency renders values in COP`() {
        val formatted = formatCurrency(
            amountInCents = 125000L,
            currencyCode = "COP",
        )

        assertTrue(formatted.contains("1.250") || formatted.contains("1,250"))
    }
}
