package com.android.purebilibili.core.store

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SimplifiedSettingsMigrationTest {
    private val homeWallpaper = stringPreferencesKey("home_wallpaper_uri")
    private val splashWallpaper = stringPreferencesKey("splash_wallpaper_uri")

    @Test
    fun movesLegacyFallbackToHomeWallpaperWithoutEnablingSplash() {
        val original = preferencesOf(
            splashWallpaper to "  file:///data/user/0/com.biluma.app/files/splash/background.jpg  ",
            booleanPreferencesKey("splash_enabled") to false,
        )

        val migrated = migrateSimplifiedSettings(original)

        assertEquals("file:///data/user/0/com.biluma.app/files/splash/background.jpg", migrated[homeWallpaper])
        assertFalse(migrated.asMap().keys.any { it.name in retiredSettingsKeys })
        assertTrue(original.asMap().containsKey(splashWallpaper))
    }

    @Test
    fun preservesDedicatedHomeWallpaper() {
        val migrated = migrateSimplifiedSettings(
            preferencesOf(homeWallpaper to "content://home", splashWallpaper to "content://legacy"),
            cachedSplashWallpaperUri = "content://cache",
        )

        assertEquals("content://home", migrated[homeWallpaper])
    }

    @Test
    fun restoresLegacyCacheOnlyWhenHomeAndDataStoreWallpaperAreBlank() {
        val migrated = migrateSimplifiedSettings(
            preferencesOf(homeWallpaper to " ", splashWallpaper to ""),
            cachedSplashWallpaperUri = " file://cached-wallpaper ",
        )

        assertEquals("file://cached-wallpaper", migrated[homeWallpaper])
        assertEquals(
            "file://stored-wallpaper",
            migrateSimplifiedSettings(
                preferencesOf(splashWallpaper to "file://stored-wallpaper"),
                cachedSplashWallpaperUri = "file://cached-wallpaper",
            )[homeWallpaper],
        )
    }

    @Test
    fun removesRetiredOptionsButKeepsAlreadyShownRecord() {
        val iconAnimation = booleanPreferencesKey("splash_icon_animation_enabled")
        val dialogShown = booleanPreferencesKey("quality_switch_failure_dialog_shown")
        val dialogEnabled = booleanPreferencesKey("quality_switch_failure_dialog_enabled")
        val unrelated = stringPreferencesKey("unrelated_setting")
        val migrated = migrateSimplifiedSettings(
            preferencesOf(
                stringPreferencesKey("app_list_item_style") to "CUSTOM",
                stringPreferencesKey("startup_animation_style") to "blue_snow_maid",
                booleanPreferencesKey("splash_enabled") to true,
                booleanPreferencesKey("splash_random_enabled") to true,
                stringPreferencesKey("splash_wallpaper_history") to "file://old",
                stringPreferencesKey("splash_random_pool_uris") to "file://random",
                floatPreferencesKey("splash_alignment_mobile") to 0.4f,
                floatPreferencesKey("splash_alignment_tablet") to -0.5f,
                booleanPreferencesKey("quality_switch_failure_dialog_once_enabled") to false,
                booleanPreferencesKey("show_video_detail_comment_count") to false,
                iconAnimation to false,
                dialogShown to true,
                dialogEnabled to false,
                unrelated to "preserved",
            )
        )

        assertFalse(migrated.asMap().keys.any { it.name in retiredSettingsKeys })
        assertFalse(migrated.asMap().containsKey(iconAnimation))
        assertEquals(true, migrated[dialogShown])
        assertEquals(false, migrated[dialogEnabled])
        assertEquals("preserved", migrated[unrelated])
    }

    @Test
    fun migrationIsIdempotentAndDoesNotInventWallpaper() {
        val empty = emptyPreferences()
        assertEquals(empty, migrateSimplifiedSettings(empty))
        val migrated = migrateSimplifiedSettings(preferencesOf(splashWallpaper to "content://legacy"))
        assertEquals(migrated, migrateSimplifiedSettings(migrated))
    }

    @Test
    fun importingOldPreferencesCannotRestoreRetiredOptions() {
        val imported = preferencesOf(
            booleanPreferencesKey("show_video_detail_comment_count") to false,
            booleanPreferencesKey("quality_switch_failure_dialog_once_enabled") to false,
            stringPreferencesKey("app_list_item_style") to "CUSTOM",
        )

        assertEquals(emptyPreferences(), migrateSimplifiedSettings(imported))
    }
}
