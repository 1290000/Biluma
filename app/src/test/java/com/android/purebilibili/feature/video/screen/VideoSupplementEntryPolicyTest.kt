package com.android.purebilibili.feature.video.screen

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VideoSupplementEntryPolicyTest {
    @Test
    fun aiSummaryEntryIsAlwaysPresentAndRemainsClickTriggered() {
        val source = sourceFile("ui/section/VideoSupplementSection.kt").readText()
        val actions = source.substringAfter("fun VideoSupplementStatsActions(")
            .substringBefore("private fun SupplementEntryIcon(")
        assertTrue(actions.contains("icon = Icons.Outlined.AutoAwesome"))
        assertTrue(actions.contains("onClick = onAiSummaryClick"))
        assertFalse(actions.contains("showAiSummary"))
        assertTrue(actions.contains("if (showNote)"))
    }

    @Test
    fun notesStartClosedForEachVideoAcrossPhoneAndTabletLayouts() {
        mapOf(
            "VideoContentSection.kt" to "info.bvid",
            "TabletVideoLayout.kt" to "info.bvid",
            "TabletCinemaLayout.kt" to "success.info.bvid",
        ).forEach { (name, videoIdentity) ->
            val source = sourceFile("screen/$name").readText()
            assertTrue(
                source.contains("var showNoteListSheet by remember($videoIdentity) { mutableStateOf(false) }"),
                name,
            )
            assertTrue(source.contains("showNoteListSheet = true"), name)
            assertFalse(source.contains("videoNoteDefaultCollapsed"), name)
        }
        val sheet = sourceFile("ui/section/VideoSupplementSection.kt").readText()
            .substringAfter("fun VideoNoteListSheet(")
            .substringBefore("private fun VideoNoteSheetFooter(")
        assertTrue(sheet.contains("if (!visible) return"))
        assertTrue(sheet.contains("VideoNoteCard("))
        assertFalse(sheet.contains("defaultCollapsed"))
    }

    private fun sourceFile(relativePath: String): File = listOf(
        File("src/main/java/com/android/purebilibili/feature/video", relativePath),
        File("app/src/main/java/com/android/purebilibili/feature/video", relativePath),
    ).first { it.exists() }
}
