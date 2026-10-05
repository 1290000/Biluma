package com.android.purebilibili.feature.video.screen

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QualitySwitchFailureDialogStructureTest {

    @Test
    fun `quality downgrade dialog always persists acknowledgment without a toggle`() {
        val source = loadSource(
            "app/src/main/java/com/android/purebilibili/feature/video/screen/VideoDetailOverlayHost.kt"
        ).substringAfter("fun VideoDetailQualitySwitchFailureDialog(")

        assertTrue(source.contains(".markQualitySwitchFailureDialogShown(context)"))
        assertTrue(source.contains("qualitySwitchFailureDialogShown == false"))
        assertFalse(source.contains("onceForCurrentDialog"))
        assertFalse(source.contains("setQualitySwitchFailureDialogOnceEnabled"))
        assertFalse(source.contains("仅提示一次"))
    }

    @Test
    fun `dialog waits for the persisted acknowledgment before deciding visibility`() {
        val adapter = loadSource(
            "app/src/main/java/com/android/purebilibili/feature/video/screen/VideoDetailPlayerSettingsOverlayAdapter.kt"
        )
        val host = loadSource(
            "app/src/main/java/com/android/purebilibili/feature/video/screen/VideoDetailOverlayHost.kt"
        )

        assertTrue(adapter.contains("collectAsStateWithLifecycle(initialValue = null"))
        assertTrue(host.contains("if (qualitySwitchFailureDialogShown == null) return@LaunchedEffect"))
    }

    private fun loadSource(path: String): String {
        val normalizedPath = path.removePrefix("app/")
        val sourceFile = listOf(File(path), File(normalizedPath))
            .firstOrNull { it.exists() }
        require(sourceFile != null) { "Cannot locate $path from ${File(".").absolutePath}" }
        return sourceFile.readText()
    }
}
