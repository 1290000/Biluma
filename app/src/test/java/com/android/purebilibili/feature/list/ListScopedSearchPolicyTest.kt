package com.android.purebilibili.feature.list

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ListScopedSearchPolicyTest {
    @Test
    fun hostedListsUseScopedSearchWithoutASetting() {
        assertTrue(shouldHideListTopSearchBar(hasScopedSearchEntry = true))
    }

    @Test
    fun standaloneListsAndSearchDestinationsKeepAnInput() {
        assertFalse(shouldHideListTopSearchBar(hasScopedSearchEntry = false))
        assertFalse(
            shouldHideListTopSearchBar(
                hasScopedSearchEntry = true,
                isSearchDestination = true,
            )
        )
    }

    @Test
    fun activeBarRequiresANonBlankQuery() {
        assertTrue(shouldShowListScopedSearchActiveBar(searchQuery = "  靖  "))
        assertFalse(shouldShowListScopedSearchActiveBar(searchQuery = "   "))
        assertFalse(shouldShowListScopedSearchActiveBar(searchQuery = ""))
    }

    @Test
    fun activeBarLabelUsesTrimmedQuery() {
        assertEquals("搜索中：靖", resolveListScopedSearchActiveBarLabel("  靖  "))
        assertEquals("搜索中", resolveListScopedSearchActiveBarLabel("   "))
    }
}
