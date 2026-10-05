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
        assertTrue(activity.contains("shouldShowCustomSplashOverlay"))
    }

    private fun loadSource(path: String): String {
        val relativePath = "src/main/java/com/android/purebilibili/$path"
        return listOf(File("app/$relativePath"), File(relativePath))
            .first { it.exists() }.readText().replace("\r\n", "\n")
    }
}
