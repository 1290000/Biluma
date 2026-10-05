package com.android.purebilibili.feature.settings

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsSimplificationStructureTest {
    @Test
    fun appearanceRetainsIconAnimationAndHomeWallpaperWithoutRetiredOptions() {
        val source = loadSource("app/src/main/java/com/android/purebilibili/feature/settings/screen/AppearanceSettingsScreen.kt")
        listOf("列表条目样式", "启动动画样式", "使用开屏壁纸", "随机展示开屏壁纸").forEach {
            assertFalse(source.contains(it))
        }
        assertTrue(source.contains("开屏图标遮罩动画"))
        assertTrue(source.contains("HomeWallpaperPickerSheet("))
        assertTrue(source.contains("getHomeWallpaperUri(context)"))
    }

    @Test
    fun preferencesAlwaysUsePresetComponentsWithoutListStyleOverrides() {
        val source = loadSource("design-system/src/main/java/com/android/purebilibili/core/ui/components/AdaptivePreferenceComponents.kt")
        assertFalse(source.contains("AppListItemStyle"))
        assertTrue(source.contains("if (uiStyle == AppUiStyle.MATERIAL3)"))
        assertTrue(source.contains("MiuixSwitchPreference("))
        assertTrue(source.contains("Md3NativeListItemContent("))
    }

    @Test
    fun startupKeepsFlyoutAndNoLongerLoadsWallpaperOrMaidOverlays() {
        val source = loadSource("app/src/main/java/com/android/purebilibili/MainActivity.kt")
        assertTrue(source.contains("runColdStartSplash && shouldEnableSplashFlyoutAnimation("))
        assertFalse(source.contains("getStartupAnimationStyle"))
        assertFalse(source.contains("showMaidStartup"))
        assertFalse(source.contains("getSplashWallpaperUri"))
        assertFalse(source.contains("customSplashShouldRender"))
    }

    @Test
    fun commentCountAndSearchNoLongerExposeRetiredPreferences() {
        val settings = loadSource("app/src/main/java/com/android/purebilibili/feature/settings/screen/PlaybackSettingsScreen.kt")
        val content = loadSource("app/src/main/java/com/android/purebilibili/feature/video/screen/VideoContentSection.kt")
        val search = loadSource("app/src/main/java/com/android/purebilibili/feature/settings/SettingsSearchPolicy.kt")
        assertFalse(settings.contains("视频详情显示评论数"))
        assertFalse(settings.contains("降档弹窗仅提示一次"))
        assertFalse(content.contains("showVideoDetailCommentCount"))
        assertTrue(content.contains("remember(replyCount)"))
        assertTrue(content.contains("replyCount.coerceAtLeast(0)"))
        listOf("开屏壁纸", "随机壁纸", "仅弹窗一次", "视频详情评论数").forEach {
            assertFalse(search.contains(it))
        }
    }

    private fun loadSource(path: String): String =
        listOf(File(path), File("..", path))
            .first { it.isFile }
            .readText()
}
