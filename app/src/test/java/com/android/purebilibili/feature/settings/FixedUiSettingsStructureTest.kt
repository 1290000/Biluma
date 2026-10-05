package com.android.purebilibili.feature.settings

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FixedUiSettingsStructureTest {
    @Test
    fun startupAnimationCannotBeRestoredThroughSettings() {
        val settings = loadSource("core/store/SettingsManager.kt")
        val activity = loadSource("MainActivity.kt")
        val appearance = loadSource("feature/settings/screen/AppearanceSettingsScreen.kt")
        listOf("StartupAnimationStyle", "SplashIconAnimationEnabled").forEach { retiredName ->
            assertFalse(settings.contains(retiredName))
            assertFalse(appearance.contains(retiredName))
        }
        assertFalse(activity.contains("splashFlyout"))
        assertFalse(activity.contains("MaidAnimation.WELCOME"))
        assertTrue(activity.contains("splashScreen.setOnExitAnimationListener"))
        assertFalse(activity.contains("shouldShowCustomSplashOverlay"))
    }

    @Test
    fun videoTagsAlwaysUseTheSmallestSizeWithoutStoredOverrides() {
        val settings = loadSource("core/store/SettingsManager.kt")
        val playback = loadSource("feature/settings/screen/PlaybackSettingsScreen.kt")
        val info = loadSource("feature/video/ui/section/VideoInfoSection.kt")
        assertFalse(settings.contains("video_tag_size_preset"))
        assertFalse(playback.contains("VideoTagSizePreset"))
        assertFalse(info.contains("getVideoTagSizePreset"))
        assertTrue(info.contains("AppTagChipSize.SMALL"))
    }

    @Test
    fun compactPlayerHasNoClassicBranchAndKeepsSharingInTheMenu() {
        val settings = loadSource("core/store/SettingsManager.kt")
        val overlay = loadSource("feature/video/ui/overlay/VideoPlayerOverlay.kt")
        assertFalse(settings.contains("compact_player_chrome"))
        assertFalse(overlay.contains("compactPlayerChrome"))
        assertTrue(overlay.contains("onClick = { showMoreMenu = false; onShare() }"))
        assertFalse(overlay.contains("onClick = onShare,"))
    }

    @Test
    fun bottomNavigationHasNoDockedRendererOrStoredSwitches() {
        val settings = loadSource("core/store/SettingsManager.kt")
        val bottomBar = loadSource("feature/home/components/BottomBar.kt")
        val floatingItem = loadSource("feature/home/components/FloatingBottomBar.kt")
        assertFalse(settings.contains("bottom_bar_floating"))
        assertFalse(settings.contains("navigation_icon_cross_scale_enabled"))
        assertFalse(bottomBar.contains("MiuixDockedBottomBarItem"))
        assertFalse(bottomBar.contains("navigationIconCrossScaleEnabled"))
        assertFalse(floatingItem.contains("iconCrossScaleEnabled"))
        assertTrue(bottomBar.contains("BiliPaiFloatingBottomBar("))
        assertTrue(floatingItem.contains("resolveNavigationIconCrossScale("))
    }

    private fun loadSource(path: String): String {
        val relativePath = "src/main/java/com/android/purebilibili/$path"
        return listOf(File("app/$relativePath"), File(relativePath))
            .first { it.exists() }.readText().replace("\r\n", "\n")
    }
}
