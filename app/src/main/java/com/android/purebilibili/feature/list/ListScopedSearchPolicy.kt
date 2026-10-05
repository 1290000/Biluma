package com.android.purebilibili.feature.list

internal fun shouldHideListTopSearchBar(
    hasScopedSearchEntry: Boolean,
    isSearchDestination: Boolean = false,
): Boolean = hasScopedSearchEntry && !isSearchDestination

internal fun shouldShowListScopedSearchActiveBar(
    searchQuery: String,
): Boolean = searchQuery.isNotBlank()

internal fun resolveListScopedSearchActiveBarLabel(searchQuery: String): String {
    val normalized = searchQuery.trim()
    return if (normalized.isEmpty()) {
        "搜索中"
    } else {
        "搜索中：$normalized"
    }
}
