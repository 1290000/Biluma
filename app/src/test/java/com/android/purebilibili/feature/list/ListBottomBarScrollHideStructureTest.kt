package com.android.purebilibili.feature.list

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ListBottomBarScrollHideStructureTest {

    @Test
    fun dynamicHistoryFavoriteAndWatchLaterShareScrollHidePolicy() {
        val dynamicSource = loadSource(
            "app/src/main/java/com/android/purebilibili/feature/dynamic/DynamicScreen.kt"
        )
        val commonListSource = loadSource(
            "app/src/main/java/com/android/purebilibili/feature/list/CommonListScreen.kt"
        )
        val watchLaterSource = loadSource(
            "app/src/main/java/com/android/purebilibili/feature/watchlater/WatchLaterScreen.kt"
        )
        val favoriteCategorySource = loadSource(
            "app/src/main/java/com/android/purebilibili/feature/list/FavoriteCategoryScreen.kt"
        )

        assertTrue(dynamicSource.contains("rememberBottomBarScrollHideConnection("))
        assertTrue(commonListSource.contains("rememberBottomBarScrollHideConnection("))
        assertTrue(watchLaterSource.contains("rememberBottomBarScrollHideConnection("))
        assertTrue(commonListSource.contains("shouldAutoHideBottomBarOnScroll("))
        assertTrue(watchLaterSource.contains("shouldAutoHideBottomBarOnScroll("))

        assertTrue(commonListSource.contains("gridState = favoriteCategoryGridState"))
        assertTrue(commonListSource.contains("gridState = favoritePagerGridStates.getOrPut(page)"))
        assertTrue(favoriteCategorySource.contains("state = gridState"))

        val activeScrollStateSource = commonListSource
            .substringAfter("val activeCommonListScrollState = remember(")
            .substringBefore("LaunchedEffect(activeCommonListScrollState)")
        assertTrue(activeScrollStateSource.contains("favoriteContentMode == FavoriteContentMode.PAGER"))
        assertTrue(activeScrollStateSource.contains("favoritePagerGridStates[pagerState.currentPage]"))
        assertTrue(activeScrollStateSource.contains("CommonListScrollState.List(subscribedFolderListState)"))
        assertTrue(activeScrollStateSource.contains("CommonListScrollState.Grid(favoriteCategoryGridState)"))
        assertTrue(activeScrollStateSource.contains("isFavoriteVideoSearchActive -> CommonListScrollState.Grid(primaryGridState)"))
        assertFalse(activeScrollStateSource.contains("favoriteFolderListState"))
    }

    private fun loadSource(relativePath: String): String {
        val root = File(System.getProperty("user.dir") ?: ".")
        val candidates = listOf(
            File(root, relativePath),
            File(root.parentFile, relativePath),
        )
        return candidates.firstOrNull { it.exists() }?.readText()
            ?: error("Missing source: $relativePath")
    }
}
