package com.android.purebilibili.feature.list

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FavoriteFolderPagerStructureTest {

    @Test
    fun folderSwitchesRetainPr840PreJumpBeforeAnimation() {
        val source = loadSource()
        val pager = favoritePagerSource(source)
        val initialSync = pager.indexOf("if (!hasSyncedFavoritePager)")
        val preJump = pager.indexOf("resolveFavoriteFolderSwitchPreJumpPage(")
        val animation = pager.indexOf("animatePagerSelection(pagerState, targetPage)")

        assertTrue(initialSync >= 0)
        assertTrue(preJump > initialSync)
        assertTrue(animation > preJump)
        assertTrue(pager.contains("preJumpPage.coerceIn(0, pagerState.pageCount - 1)"))
        assertTrue(pager.contains("userScrollEnabled = false"))
        assertTrue(pager.contains("beyondViewportPageCount = 0"))
        assertTrue(source.contains("initialPage = selectedFolderIndex.coerceIn("))
        assertTrue(pager.contains("pagerState.currentPageOffsetFraction != 0f"))
    }

    @Test
    fun eachFolderKeepsItsOwnLoadingPaginationAndScrollState() {
        val pager = favoritePagerSource(loadSource())

        assertTrue(pager.contains("favoriteVm.getFolderUiState(page).collectAsStateWithLifecycle()"))
        assertTrue(pager.contains("favoriteVm.loadFolder(page)"))
        assertTrue(pager.contains("favoriteVm.retryFolder(page)"))
        assertTrue(pager.contains("favoriteVm.loadMoreForFolder(page)"))
        assertTrue(pager.contains("favoritePagerGridStates.getOrPut(page)"))
        assertTrue(pager.contains("playFavoriteVideo(folderUiState.items, bvid, cid, coverUrl, page, false)"))
        assertTrue(pager.contains("favoriteBatchMode = isFavoriteBatchMode && page == selectedFolderIndex"))
        assertTrue(pager.contains("onFavoriteToggleSelect = toggleFavoriteResourceSelection"))
        assertTrue(pager.contains("searchQuery = searchQuery"))
        assertTrue(pager.contains("pinchEnabled = pinchToZoomColumnsEnabled"))
    }

    @Test
    fun singleFolderDoesNotRequirePager() {
        val singleFolder = loadSource()
            .substringAfter("} else if (favoriteContentMode == FavoriteContentMode.SINGLE_FOLDER) {")
            .substringBefore("} else {\n                    if (historyViewModel != null)")

        assertFalse(singleFolder.contains("HorizontalPager("))
        assertTrue(singleFolder.contains("favoriteVm.getFolderUiState(0).collectAsStateWithLifecycle()"))
        assertTrue(singleFolder.contains("favoriteVm.loadFolder(0)"))
        assertTrue(singleFolder.contains("favoriteVm.loadMoreForFolder(0)"))
        assertTrue(singleFolder.contains("gridState = primaryGridState"))
        assertTrue(singleFolder.contains("searchQuery = searchQuery"))
    }

    @Test
    fun searchAndFolderLoadingUseTheVisibleContentState() {
        val source = loadSource()
        val loading = source.substringAfter("val loadingChromeContent = when {")
            .substringBefore("val commonListChromeSource")
        val scroll = source.substringAfter("val activeCommonListScrollState = remember(")
            .substringBefore("LaunchedEffect(activeCommonListScrollState)")

        assertTrue(source.contains("isSearchDestination && favoriteSection == FavoriteSection.VIDEO && searchQuery.isNotBlank()"))
        assertTrue(source.contains("if (isFavoriteVideoSearchActive)"))
        assertTrue(scroll.contains("isFavoriteVideoSearchActive -> CommonListScrollState.Grid(primaryGridState)"))
        assertTrue(loading.contains("isFavoriteVideoSearchActive ->"))
        assertTrue(loading.contains("favoriteSearchUiState.isLoading && favoriteSearchUiState.items.isEmpty()"))
        assertTrue(loading.contains("selectedFolderUiState.isLoading && selectedFolderUiState.items.isEmpty()"))
        assertTrue(loading.contains("singleFolderUiState.isLoading && singleFolderUiState.items.isEmpty()"))
    }

    @Test
    fun favoriteHeaderUsesScopedSearchAndRetainsColumnToggleAndCompactBatchActions() {
        val source = loadSource()
        val favoriteActions = source.substringAfter("if (favoriteViewModel != null && favoriteSection == FavoriteSection.VIDEO) {")
            .substringBefore("if (historyViewModel != null)")

        val searchPolicy = source.substringAfter("val hideListTopSearchBar = ")
            .substringBefore("val showListScopedSearchActiveBar")

        assertTrue(searchPolicy.trimStart().startsWith("shouldHideListTopSearchBar("))
        assertTrue(searchPolicy.contains("hasScopedSearchEntry = listScopedSearchChannel != null"))
        assertTrue(searchPolicy.contains("isSearchDestination = isSearchDestination"))
        assertTrue(source.contains("VideoListLayoutToggle("))
        assertTrue(source.contains("singleColumn = personalListColumns == 1"))
        assertTrue(favoriteActions.contains("AppWindowActionMenu("))
        assertTrue(favoriteActions.contains("label = \"复制到收藏夹\""))
        assertTrue(favoriteActions.contains("label = \"移动到收藏夹\""))
    }

    private fun favoritePagerSource(source: String): String = source
        .substringAfter("} else if (favoriteContentMode == FavoriteContentMode.PAGER) {")
        .substringBefore("} else if (favoriteContentMode == FavoriteContentMode.SINGLE_FOLDER)")

    private fun loadSource(): String = listOf(
        File("app/src/main/java/com/android/purebilibili/feature/list/CommonListScreen.kt"),
        File("src/main/java/com/android/purebilibili/feature/list/CommonListScreen.kt"),
    ).first(File::exists).readText().replace("\r\n", "\n")
}
