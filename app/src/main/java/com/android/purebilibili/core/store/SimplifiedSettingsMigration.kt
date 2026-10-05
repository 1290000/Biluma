package com.android.purebilibili.core.store

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey

internal val retiredSettingsKeys = setOf(
    "app_list_item_style",
    "startup_animation_style",
    "splash_icon_animation_enabled",
    "splash_enabled",
    "splash_random_enabled",
    "splash_wallpaper_uri",
    "splash_wallpaper_history",
    "splash_random_pool_uris",
    "splash_alignment_mobile",
    "splash_alignment_tablet",
    "quality_switch_failure_dialog_once_enabled",
    "show_video_detail_comment_count",
)

internal fun migrateSimplifiedSettings(
    currentData: Preferences,
    cachedSplashWallpaperUri: String = "",
): Preferences {
    val migrated = currentData.toMutablePreferences()
    val homeWallpaperKey = stringPreferencesKey("home_wallpaper_uri")
    val legacyWallpaperUri = currentData[stringPreferencesKey("splash_wallpaper_uri")]
        .orEmpty().ifBlank { cachedSplashWallpaperUri }.trim()
    if (currentData[homeWallpaperKey].isNullOrBlank() && legacyWallpaperUri.isNotEmpty()) {
        migrated[homeWallpaperKey] = legacyWallpaperUri
    }
    currentData.asMap().keys.filter { it.name in retiredSettingsKeys }.forEach { key ->
        migrated.remove(key)
    }
    return migrated.toPreferences()
}

internal class SimplifiedSettingsMigration(context: Context) : DataMigration<Preferences> {
    private val splashPreferences = context.getSharedPreferences("splash_prefs", Context.MODE_PRIVATE)
    private val themePreferences = context.getSharedPreferences("theme_cache", Context.MODE_PRIVATE)
    private val retiredSplashCacheKeys = setOf(
        "wallpaper_uri", "wallpaper_history", "random_pool_uris", "enabled", "random_enabled",
        "animation_style", "icon_animation_enabled", "alignment_mobile", "alignment_tablet",
    )

    override suspend fun shouldMigrate(currentData: Preferences): Boolean =
        currentData.asMap().keys.any { it.name in retiredSettingsKeys } ||
            retiredSplashCacheKeys.any(splashPreferences::contains) ||
            themePreferences.contains("app_list_item_style")

    override suspend fun migrate(currentData: Preferences): Preferences = migrateSimplifiedSettings(
        currentData = currentData,
        cachedSplashWallpaperUri = splashPreferences.getString("wallpaper_uri", "").orEmpty(),
    )

    override suspend fun cleanUp() {
        val splashEditor = splashPreferences.edit()
        retiredSplashCacheKeys.forEach { splashEditor.remove(it) }
        check(splashEditor.commit()) { "Unable to remove retired splash preferences" }
        check(themePreferences.edit().remove("app_list_item_style").commit()) {
            "Unable to remove retired list style preference"
        }
    }
}
