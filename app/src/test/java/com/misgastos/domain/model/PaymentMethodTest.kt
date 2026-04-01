package com.misgastos.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PaymentMethodTest {

    @Test
    fun `fromValue resolves debit credit and legacy card values`() {
        assertEquals(PaymentMethod.DEBIT_CARD, PaymentMethod.fromValue("debit_card"))
        assertEquals(PaymentMethod.CREDIT_CARD, PaymentMethod.fromValue("credit_card"))
        assertEquals(PaymentMethod.CARD, PaymentMethod.fromValue("card"))
    }

    @Test
    fun `selectableEntries expose debit and credit but hide legacy generic card`() {
        assertTrue(PaymentMethod.selectableEntries.contains(PaymentMethod.DEBIT_CARD))
        assertTrue(PaymentMethod.selectableEntries.contains(PaymentMethod.CREDIT_CARD))
        assertFalse(PaymentMethod.selectableEntries.contains(PaymentMethod.CARD))
    }
}
