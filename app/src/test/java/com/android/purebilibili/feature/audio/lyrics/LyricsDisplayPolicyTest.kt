package com.android.purebilibili.feature.audio.lyrics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LyricsDisplayPolicyTest {

    @Test
    fun `pseudo secondary filters music-note wrapped rows`() {
        assertTrue(
            isPseudoSecondaryLine(
                primaryText = "心有灵犀的你也会感应到吗",
                secondaryText = "♪ 留下心有灵犀的你也会感应听到吗 ♪",
            )
        )
        assertFalse(
            isPseudoSecondaryLine(
                primaryText = "心有灵犀的你也会感应到吗",
                secondaryText = "Can you feel it too",
            )
        )
    }

    @Test
    fun `romanization only shows for phonetic scripts`() {
        assertFalse(needsPhoneticAnnotation("留下心有灵犀的你"))
        assertTrue(needsPhoneticAnnotation("こころ"))
        val (_, romanization) = resolveDisplaySecondaryRows(
            primaryText = "心有灵犀的你也会感应到吗",
            translation = "Can you feel it",
            romanization = "留下心有灵犀的你也会感应听到吗",
            showTranslation = true,
        )
        assertEquals(null, romanization)
    }
}
