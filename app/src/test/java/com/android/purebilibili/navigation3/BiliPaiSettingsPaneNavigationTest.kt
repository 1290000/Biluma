package com.android.purebilibili.navigation3

import com.android.purebilibili.feature.settings.SettingsRootCategory
import com.android.purebilibili.feature.settings.resolveSettingsCategoryNavKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BiliPaiSettingsPaneNavigationTest {
    @Test
    fun everySidebarCategoryReplacesPreviousDetailAndKeepsSettingsRoot() {
        val root = listOf(BiliPaiNavKey.MainHost, BiliPaiNavKey.Settings)
        var stack = root
        val categories = listOf(
            SettingsRootCategory.APPEARANCE_THEME,
            SettingsRootCategory.PLAYBACK_QUALITY,
            SettingsRootCategory.HOME_RECOMMENDATION,
            SettingsRootCategory.NAVIGATION_INTERACTION,
            SettingsRootCategory.PRIVACY_PERMISSION,
            SettingsRootCategory.STORAGE_BACKUP,
            SettingsRootCategory.PLUGINS_EXTENSIONS,
            SettingsRootCategory.SYSTEM_ABOUT,
            SettingsRootCategory.APPEARANCE_THEME,
        )
        for (category in categories) {
            val target = resolveSettingsCategoryNavKey(category)
            stack = selectSettingsPaneNavKey(stack, target)
            assertEquals(root + target, stack)
            assertEquals(root, popBiliPaiNavKey(stack))
            assertTrue(isSettingsPaneNavigation(true, BiliPaiNavKey.Settings, target, "home"))
        }
    }

    @Test
    fun sidebarSelectionClearsNestedDetailsAndSearch() {
        val root = listOf(BiliPaiNavKey.MainHost, BiliPaiNavKey.Settings)
        val stack = root + listOf(
            BiliPaiNavKey.SettingsSearch,
            BiliPaiNavKey.AppearanceSettings,
            BiliPaiNavKey.IconSettings,
        )
        assertEquals(
            root + BiliPaiNavKey.PluginsSettings(),
            selectSettingsPaneNavKey(stack, BiliPaiNavKey.PluginsSettings()),
        )
    }

    @Test
    fun bottomSettingsTabIsASettingsParent() {
        assertTrue(isSettingsPaneNavigation(true, BiliPaiNavKey.MainHost, BiliPaiNavKey.PlaybackSettings, "settings"))
        assertFalse(isSettingsPaneNavigation(true, BiliPaiNavKey.MainHost, BiliPaiNavKey.Settings, "home"))
        assertFalse(isSettingsPaneNavigation(true, BiliPaiNavKey.MainHost, BiliPaiNavKey.PluginsSettings(), "home"))
    }

    @Test
    fun nestedSettingsAreInstantOnlyWhenBothPanesAreVisible() {
        assertTrue(isSettingsPaneNavigation(true, BiliPaiNavKey.AppearanceSettings, BiliPaiNavKey.IconSettings, null))
        assertFalse(isSettingsPaneNavigation(false, BiliPaiNavKey.Settings, BiliPaiNavKey.AppearanceSettings, null))
        assertFalse(isSettingsPaneNavigation(true, BiliPaiNavKey.Settings, BiliPaiNavKey.Home, null))
        assertFalse(isSettingsPaneNavigation(true, null, BiliPaiNavKey.Settings, null))
    }
}
