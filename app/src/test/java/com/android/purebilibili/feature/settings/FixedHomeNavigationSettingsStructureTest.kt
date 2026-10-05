package com.android.purebilibili.feature.settings

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FixedHomeNavigationSettingsStructureTest {
    @Test
    fun retiredSwitchesAndTheirSearchEntriesAreAbsent() {
        val appearance = source("feature/settings/screen/AppearanceSettingsScreen.kt")
        val navigation = source("feature/settings/screen/BottomBarSettingsScreen.kt")
        val search = source("feature/settings/SettingsSearchPolicy.kt")
        val retiredTitles = listOf(
            "首页顶部轮播封面", "轮播默认播放", "底栏搜索联动",
            "保留首页顶部搜索条", "下滑合体", "列表精简搜索", "侧边栏账号切换",
        )
        retiredTitles.forEach { title ->
            assertFalse(appearance.contains(title), title)
            assertFalse(navigation.contains(title), title)
            assertFalse(search.contains(title), title)
        }
        assertFalse(navigation.contains("setBottomBarFloating"))
        assertTrue(navigation.contains("setTabletUseSidebar"))
    }

    @Test
    fun homeAlwaysUsesTheRegularFeedAndKeepsTopSearchSpace() {
        val home = source("feature/home/HomeScreen.kt")
        val category = source("feature/home/HomeCategoryPage.kt")
        val header = source("feature/home/components/HomeHeader.kt")
        assertFalse(home.contains("HomeHeroCarousel"))
        assertFalse(category.contains("home_hero_carousel"))
        assertTrue(category.contains("val visibleGridVideos = categoryState.videos"))
        assertTrue(home.contains("val searchBarHeightDp = homeTopPresetStyle.searchBarHeight"))
        assertTrue(header.contains("val searchBarHeightDp = resolveHomeTopSearchBarHeight(topChromePolicy)"))
    }

    @Test
    fun linkedSearchIsPermanentAndDownwardScrollCannotMergeTheDock() {
        val bottom = source("feature/home/components/BottomBar.kt")
        val linked = source("feature/home/components/LinkedBottomDock.kt")
        assertTrue(linked.contains("BiliPaiBottomBarSearchVisualContent("))
        assertFalse(linked.contains("searchEnabled"))
        assertFalse(bottom.contains("bottomBarSearchEnabled"))
        assertFalse(linked.contains("mergeOnScrollDownEnabled"))
        assertFalse(linked.contains("collapseRequested"))
        assertFalse(source("navigation/AppNavigation.kt").contains("collapseLinkedPlaybackDock"))
        val scrollEffect = linked.substringAfter("LaunchedEffect(currentItem, hasAudio")
            .substringBefore("fun expand()")
        assertTrue(scrollEffect.contains("updatePhase(LinkedDockPhase.Expanded)"))
        assertFalse(scrollEffect.contains("updatePhase(LinkedDockPhase.Playback)"))
        assertTrue(linked.contains("resolveLinkedDockPhaseOnSearchDismiss("))
    }

    @Test
    fun accountSwitcherAndScopedSearchDoNotReadRetiredPreferences() {
        val navigation = source("navigation/AppNavigation.kt")
        val onboarding = source("feature/onboarding/OnboardingSettingsGuidePolicy.kt")
        assertTrue(navigation.contains("onAccountSwitchClick = { sidebarAccountSwitcherVisible = true }"))
        assertFalse(navigation.contains("sidebarAccountSwitcherEnabled"))
        assertFalse(onboarding.contains("setBottomBarSearchEnabled"))
        assertTrue(navigation.contains("hasListScopedSearchEntry = isBottomPagerHosted"))
        assertTrue(navigation.contains("!useSideNavigation &&"))
        assertFalse(navigation.contains("&& isBottomBarFloating"))
    }

    private fun source(relativePath: String): String {
        val path = "src/main/java/com/android/purebilibili/" + relativePath
        return listOf(File("app/" + path), File(path)).first(File::exists).readText()
    }
}
