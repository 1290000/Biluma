package com.android.purebilibili.navigation

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppNavigationStartupRouteStructureTest {

    @Test
    fun startupDoesNotReadRetiredPortraitFeedPreferences() {
        val navigation = sourceFile("navigation/AppNavigation.kt").readText()
        val settings = sourceFile("core/store/SettingsManager.kt").readText()

        assertTrue(navigation.contains("if (agreementRequired) ScreenRoutes.Onboarding.route else ScreenRoutes.Home.route"))
        assertFalse(navigation.contains("resolvedPortraitStartupRoute"))
        assertFalse(navigation.contains("LaunchToPortraitFeedOnStartup"))
        assertFalse(settings.contains("launch_to_portrait_feed_on_startup"))
        assertFalse(settings.contains("portrait_startup_cache"))
    }

    private fun sourceFile(relativePath: String): File {
        val roots = listOf(
            File("src/main/java/com/android/purebilibili"),
            File("app/src/main/java/com/android/purebilibili"),
        )
        return roots.map { File(it, relativePath) }.firstOrNull { it.exists() }
            ?: error("找不到 $relativePath")
    }
}
