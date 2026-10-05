package com.android.purebilibili.core.store

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences

internal object HomeNavigationSettingsMigration : DataMigration<Preferences> {
    private val obsoleteKeys = setOf(
        "home_hero_carousel_enabled",
        "home_hero_carousel_autoplay_enabled",
        "bottom_bar_search_enabled",
        "keep_home_top_search_with_bottom_search",
        "linked_dock_merge_on_scroll_enabled",
        "list_scoped_search_enabled",
        "sidebar_account_switcher_enabled",
        "bottom_bar_search_auto_expand_mode",
        "bottom_bar_search_layout_mode",
    )

    override suspend fun shouldMigrate(currentData: Preferences): Boolean =
        currentData.asMap().keys.any { it.name in obsoleteKeys }

    override suspend fun migrate(currentData: Preferences): Preferences {
        val migrated = currentData.toMutablePreferences()
        currentData.asMap().keys
            .filter { it.name in obsoleteKeys }
            .forEach { migrated.remove(it) }
        return migrated.toPreferences()
    }

    override suspend fun cleanUp() = Unit
}
