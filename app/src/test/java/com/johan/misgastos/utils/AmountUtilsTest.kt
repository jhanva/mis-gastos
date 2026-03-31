package com.johan.misgastos.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
    fun `parseAmountInputToCents rejects zero and invalid values`() {
        assertNull(parseAmountInputToCents("0"))
        assertNull(parseAmountInputToCents("abc"))
    }

    @Test
    fun `formatAmountInputFromCents removes unnecessary decimals`() {
        assertEquals("1250", formatAmountInputFromCents(125000L))
        assertEquals("1299.5", formatAmountInputFromCents(129950L))
    }
}
