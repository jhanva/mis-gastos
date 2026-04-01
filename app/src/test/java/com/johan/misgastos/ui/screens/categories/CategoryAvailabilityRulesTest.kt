package com.johan.misgastos.ui.screens.categories

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CategoryAvailabilityRulesTest {

    @Test
    fun `wouldLeaveNoActiveCategories rejects deactivating the last active category`() {
        val categories = listOf(activeCategory(id = 1L))

        val result = wouldLeaveNoActiveCategories(
            categories = categories,
            categoryId = 1L,
            targetIsActive = false,
        )

        assertTrue(result)
    }

    @Test
    fun `wouldLeaveNoActiveCategories allows deactivating when another active category exists`() {
        val categories = listOf(
            activeCategory(id = 1L),
            activeCategory(id = 2L),
        )

        val result = wouldLeaveNoActiveCategories(
            categories = categories,
            categoryId = 1L,
            targetIsActive = false,
        )

        assertFalse(result)
    }

    @Test
    fun `deletingCategoryWouldLeaveNoActive rejects deleting the last active category`() {
        val categories = listOf(activeCategory(id = 1L))

        assertTrue(deletingCategoryWouldLeaveNoActive(categories, categoryId = 1L))
    }

    @Test
    fun `deletingCategoryWouldLeaveNoActive allows deleting an inactive category`() {
        val categories = listOf(
            activeCategory(id = 1L),
            inactiveCategory(id = 2L),
        )

        assertFalse(deletingCategoryWouldLeaveNoActive(categories, categoryId = 2L))
    }

    private fun activeCategory(
        id: Long,
    ): CategoryAvailabilityItem {
        return CategoryAvailabilityItem(
            id = id,
            isActive = true,
        )
    }

    private fun inactiveCategory(id: Long): CategoryAvailabilityItem {
        return activeCategory(id = id).copy(isActive = false)
    }
}
