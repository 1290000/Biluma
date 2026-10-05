package com.android.purebilibili.core.store

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HomeNavigationSettingsMigrationTest {
    private val obsoleteBooleanKeys = listOf(
        "home_hero_carousel_enabled",
        "home_hero_carousel_autoplay_enabled",
        "bottom_bar_search_enabled",
        "keep_home_top_search_with_bottom_search",
        "linked_dock_merge_on_scroll_enabled",
        "list_scoped_search_enabled",
        "sidebar_account_switcher_enabled",
    )
    private val obsoleteModeKeys = listOf(
        "bottom_bar_search_auto_expand_mode",
        "bottom_bar_search_layout_mode",
    )

    @Test
    fun migrationRemovesBothLegacyValuesAndPreservesUnrelatedPreferences() = runBlocking {
        for (enabled in listOf(false, true)) {
            val preferences = emptyPreferences().toMutablePreferences()
            obsoleteBooleanKeys.forEach { preferences[booleanPreferencesKey(it)] = enabled }
            obsoleteModeKeys.forEach { preferences[intPreferencesKey(it)] = 1 }
            val sidebarKey = booleanPreferencesKey("tablet_use_sidebar")
            val floatingKey = booleanPreferencesKey("bottom_bar_floating")
            preferences[sidebarKey] = false
            preferences[floatingKey] = true

            assertTrue(HomeNavigationSettingsMigration.shouldMigrate(preferences))
            val migrated = HomeNavigationSettingsMigration.migrate(preferences)

            assertEquals(setOf(sidebarKey.name, floatingKey.name), migrated.asMap().keys.map { it.name }.toSet())
            assertEquals(false, migrated[sidebarKey])
            assertEquals(true, migrated[floatingKey])
            assertFalse(HomeNavigationSettingsMigration.shouldMigrate(migrated))
            assertEquals(migrated, HomeNavigationSettingsMigration.migrate(migrated))
            assertEquals(11, preferences.asMap().size)
        }
    }

    @Test
    fun legacyBackupsCannotChangeHomeOrNavigationBehavior() {
        val defaults = emptyPreferences()
        for (enabled in listOf(false, true)) {
            val restored = defaults.toMutablePreferences()
            obsoleteBooleanKeys.forEach { restored[booleanPreferencesKey(it)] = enabled }
            obsoleteModeKeys.forEach { restored[intPreferencesKey(it)] = 1 }
            assertEquals(mapHomeSettingsFromPreferences(defaults), mapHomeSettingsFromPreferences(restored))
            assertEquals(
                SettingsManager.mapAppNavigationSettingsFromPreferences(defaults),
                SettingsManager.mapAppNavigationSettingsFromPreferences(restored),
            )
        }
    }

    @Test
    fun cleanInstallNeedsNoMigrationAndSharingExcludesRetiredKeys() = runBlocking {
        assertFalse(HomeNavigationSettingsMigration.shouldMigrate(emptyPreferences()))
        val retiredKeys = (obsoleteBooleanKeys + obsoleteModeKeys).toSet()
        assertTrue(SettingsManager.getShareableSettingsEntryDefinitions().none { it.storageKey in retiredKeys })
    }
}
