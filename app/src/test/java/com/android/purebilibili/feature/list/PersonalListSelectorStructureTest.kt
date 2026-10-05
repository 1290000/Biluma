package com.android.purebilibili.feature.list

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PersonalListSelectorStructureTest {

    @Test
    fun favoriteFolderSelector_restoresAlpha5Dropdown() {
        val source = loadSource("app/src/main/java/com/android/purebilibili/feature/list/CommonListScreen.kt")
        val selectorSource = loadSource(
            "app/src/main/java/com/android/purebilibili/feature/list/FavoriteFolderSelector.kt",
        )

        assertTrue(source.contains("FavoriteFolderSelector("))
        assertFalse(source.contains("FavoriteFolderCardList("))
        assertTrue(selectorSource.contains("BottomBarMatchedReusableLiquidDock("))
        assertTrue(selectorSource.contains("AppDropdownMenu("))
        assertTrue(selectorSource.contains("folders.forEachIndexed"))
        assertTrue(selectorSource.contains("onFolderSelected(index)"))
        assertTrue(selectorSource.contains("onSubscribedSelected()"))
        assertTrue(selectorSource.contains("heightIn(min = AppChromeSizeTokens.MinimumTouchTarget)"))
    }

    @Test
    fun favoritePrimarySelectors_areTapFirstAndAlwaysReachable() {
        val source = loadSource(
            "app/src/main/java/com/android/purebilibili/feature/list/CommonListScreen.kt",
        )
        val categorySelector = source
            .substringAfter("if (favoriteViewModel != null) {")
            .substringBefore("if (favoriteViewModel != null && isSearchDestination")

        assertTrue(categorySelector.contains("AppLiquidAwareTabRow("))
        assertFalse(categorySelector.contains("AppNativeTabRow("))
        assertTrue(categorySelector.contains("FavoriteSection.entries.map"))
        assertFalse(categorySelector.contains("LazyRow("))
        assertFalse(source.contains("systemGestureExclusion"))
    }

    @Test
    fun personalListOverflowMenus_useMiuixWindowActionMenu() {
        val source = loadSource(
            "app/src/main/java/com/android/purebilibili/feature/list/CommonListScreen.kt",
        )

        assertTrue(source.contains("AppWindowActionMenu("))
        assertTrue(source.contains("label = \"新建收藏夹\""))
        assertTrue(source.contains("label = \"清空观看记录\""))
        assertTrue(source.contains("\"复制到收藏夹\" else \"移动到收藏夹\""))
        assertFalse(source.contains("showFavoriteManagementMenu"))
        assertFalse(source.contains("showHistoryManagementMenu"))
        assertFalse(source.contains("showFavoriteBatchMenu"))
    }

    @Test
    fun ownedFoldersSwitchInlineAndSubscribedFoldersKeepDetailNavigation() {
        val source = loadSource(
            "app/src/main/java/com/android/purebilibili/feature/list/CommonListScreen.kt",
        )
        val selectorCall = source.substringAfter("FavoriteFolderSelector(")
            .substringBefore("if (historyViewModel != null)")
        val subscribedContent = source.substringAfter("} else if (isSubscribedBrowse) {")
            .substringBefore("} else if (favoriteContentMode == FavoriteContentMode.PAGER)")

        assertTrue(selectorCall.contains("favoriteViewModel.switchFolder(index)"))
        assertTrue(selectorCall.contains("favoriteBrowseSection = FavoriteBrowseSection.OWNED"))
        assertFalse(selectorCall.contains("onFavoriteFolderClick"))
        assertTrue(subscribedContent.contains("onFavoriteFolderClick?.invoke("))
        assertTrue(subscribedContent.contains("onCollectionClick?.invoke(collectionRoute)"))
        assertTrue(subscribedContent.contains("resolveFavoriteFolderMediaId(folder)"))
    }

    @Test
    fun secondaryPersonalFilters_doNotRequireHorizontalDrag() {
        val categorySource = loadSource(
            "app/src/main/java/com/android/purebilibili/feature/list/FavoriteCategoryScreen.kt",
        )
        val watchLaterSource = loadSource(
            "app/src/main/java/com/android/purebilibili/feature/watchlater/WatchLaterScreen.kt",
        )
        val profileSource = loadSource(
            "app/src/main/java/com/android/purebilibili/feature/profile/ProfileScreen.kt",
        )

        assertTrue(categorySource.contains("AppThemeAdaptiveTabRow("))
        assertFalse(categorySource.contains("AppFilterChip("))
        assertTrue(watchLaterSource.contains("AppThemeAdaptiveTabRow("))
        assertFalse(watchLaterSource.contains("AppFilterChip("))
        val profileTabs = profileSource
            .substringAfter("private fun ProfileSpaceTabs(")
            .substringBefore("private fun ProfileSpaceTabBody(")
        assertTrue(profileTabs.contains("AppNativeTabRow("))
        assertFalse(profileTabs.contains("BottomBarLiquidSegmentedControl("))
    }

    private fun loadSource(path: String): String {
        val normalizedPath = path.removePrefix("app/")
        val sourceFile = listOf(
            File(path),
            File(normalizedPath),
            File("app/$normalizedPath"),
        ).firstOrNull { it.exists() }
        require(sourceFile != null) { "Cannot locate $path from ${File(".").absolutePath}" }
        return sourceFile.readText()
    }
}
