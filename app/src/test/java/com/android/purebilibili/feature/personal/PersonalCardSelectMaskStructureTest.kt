package com.android.purebilibili.feature.personal

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PersonalCardSelectMaskStructureTest {

    private fun readSource(relative: String): String = listOf(
        File("app/src/main/java/com/android/purebilibili/$relative"),
        File("src/main/java/com/android/purebilibili/$relative"),
    ).first(File::exists).readText()

    @Test
    fun frameKeepsCheckboxChromeOptIn() {
        val source = readSource("feature/personal/PersonalMediaCard.kt")
        assertTrue(source.contains("fun PersonalCardSelectMask("))
        assertTrue(source.contains("showSelectionCheckbox: Boolean = false"))
        assertTrue(source.contains("if (selected && showSelectionCheckbox)"))
        assertTrue(source.contains("AppCheckbox("))
    }

    @Test
    fun historyAndWatchLaterKeepMaskInsideCoverOverlay() {
        listOf(
            "feature/list/HistoryPersonalCard.kt",
            "feature/watchlater/WatchLaterScreen.kt",
        ).forEach { relative ->
            val source = readSource(relative)
            assertTrue(
                source.contains("PersonalCardSelectMask(selected"),
                "缺少选择遮罩接入: $relative",
            )
        }
    }

    @Test
    fun favoritesUseAlpha5CheckboxWithoutCoverMask() {
        val source = readSource("feature/list/FavoritePersonalCard.kt")
        assertTrue(source.contains("showSelectionCheckbox = true"))
        assertFalse(source.contains("PersonalCardSelectMask("))
    }

    @Test
    fun historyCardDropsWholeCardSelectionTintAndDoublePadding() {
        val source = readSource("feature/list/HistoryPersonalCard.kt")
        assertFalse(source.contains("primary.copy(alpha = 0.10f)"))
        assertFalse(source.contains("padding(horizontal = 12.dp, vertical = 5.dp)"))
    }
}
