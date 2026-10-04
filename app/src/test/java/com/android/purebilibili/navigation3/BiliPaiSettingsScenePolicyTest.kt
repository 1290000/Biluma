package com.android.purebilibili.navigation3

import com.android.purebilibili.feature.settings.SettingsRootCategory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BiliPaiSettingsScenePolicyTest {
    private val main = BiliPaiNavKey.MainHost
    private val settings = BiliPaiNavKey.Settings
    private val appearance = BiliPaiNavKey.AppearanceSettings
    private val icon = BiliPaiNavKey.IconSettings

    @Test
    fun categoryAndNestedNavigationKeepOneOuterSettingsIdentity() {
        val expected = listOf(main, settings)
        listOf(
            listOf(main, settings),
            listOf(main, settings, appearance),
            listOf(main, settings, appearance, icon),
            listOf(main, settings, BiliPaiNavKey.PluginsSettings()),
            listOf(main, settings, BiliPaiNavKey.SettingsCategory(SettingsRootCategory.SYSTEM_ABOUT)),
        ).forEach { assertEquals(expected, resolveSettingsSceneDisplayStack(it)) }
    }

    @Test
    fun settingsTabDetailsAlsoKeepOneIdentityAcrossSidebarSelections() {
        assertEquals(listOf(main, settings), resolveSettingsSceneDisplayStack(listOf(main, appearance)))
        assertEquals(listOf(main, settings), resolveSettingsSceneDisplayStack(listOf(main, BiliPaiNavKey.PluginsSettings())))
        assertEquals(listOf(main, settings), resolveSettingsSceneDisplayStack(listOf(main, appearance, icon)))
    }

    @Test
    fun unrelatedRoutesRemainOutsideTheSettingsScene() {
        val stack = listOf(main, settings, appearance, BiliPaiNavKey.Login)
        assertEquals(listOf(main, settings, BiliPaiNavKey.Login), resolveSettingsSceneDisplayStack(stack))
        assertEquals(listOf(settings, appearance), resolveSettingsSceneSegment(stack, settings))
    }

    @Test
    fun independentSettingsSegmentsRemainDistinctUnderExternalNavigation() {
        val plugins = BiliPaiNavKey.PluginsSettings(importUrl = "https://example.com/plugin.json")
        val stack = listOf(main, settings, appearance, BiliPaiNavKey.Login, plugins)
        assertEquals(listOf(main, settings, BiliPaiNavKey.Login, plugins), resolveSettingsSceneDisplayStack(stack))
        assertEquals(listOf(plugins), resolveSettingsSceneSegment(stack, plugins))
    }

    @Test
    fun wideCategoryRootBackExitsWhileNestedPageBackPopsOneLevel() {
        assertTrue(shouldExitSettingsSceneOnBack(listOf(main, settings, appearance), true))
        assertFalse(shouldExitSettingsSceneOnBack(listOf(main, settings, appearance, icon), true))
        assertEquals(listOf(main), exitSettingsSceneStack(listOf(main, settings, appearance)))
        assertEquals(listOf(main, settings, appearance), popSettingsSceneStack(listOf(main, settings, appearance, icon)))
    }

    @Test
    fun foldingKeepsTheSelectedPageAndChangesOnlyBackDestination() {
        val stack = listOf(main, settings, appearance)
        assertEquals(listOf(appearance), resolveSettingsDetailStack(resolveSettingsSceneSegment(stack, settings)))
        assertTrue(shouldExitSettingsSceneOnBack(stack, true))
        assertFalse(shouldExitSettingsSceneOnBack(stack, false))
        assertEquals(listOf(main, settings), popSettingsSceneStack(stack))
    }

    @Test
    fun narrowSettingsTabBackReturnsToItsVirtualCategoryList() {
        assertEquals(listOf(main, settings), popSettingsSceneStack(listOf(main, appearance)))
        assertEquals(listOf(main), exitSettingsSceneStack(listOf(main, appearance)))
        assertTrue(shouldExitSettingsSceneOnBack(listOf(main, settings), false))
    }

    @Test
    fun returningToCategoryListReusesAnExistingSettingsRoot() {
        val plugins = BiliPaiNavKey.PluginsSettings(importUrl = "https://example.com/plugin.json")
        val stack = listOf(main, settings, appearance, BiliPaiNavKey.Login, plugins)
        assertEquals(listOf(main, settings), popSettingsSceneStack(stack))
    }

    @Test
    fun exitingSettingsReturnsToTheActualEntrySource() {
        val source = BiliPaiNavKey.VideoDetail("BV1")
        assertEquals(listOf(main, source), exitSettingsSceneStack(listOf(main, source, settings, appearance)))
        assertFalse(shouldExitSettingsSceneOnBack(listOf(main, source), true))
    }
}
