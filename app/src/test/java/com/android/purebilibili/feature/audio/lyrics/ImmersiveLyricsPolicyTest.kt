package com.android.purebilibili.feature.audio.lyrics

import com.android.purebilibili.feature.audio.lyrics.halcyon.currentLyricIndexAt
import com.android.purebilibili.feature.audio.lyrics.halcyon.mapToHalcyonLyrics
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ImmersiveLyricsPolicyTest {

    @Test
    fun currentLineUsesAdjustedTimeAndKeepsFocusDuringGaps() {
        val document = LyricDocument(
            lines = listOf(
                LyricLine(1_000L, 2_000L, "第一句"),
                LyricLine(10_000L, 12_000L, "第二句")
            ),
            offsetMs = 500L
        )
        val lines = mapToHalcyonLyrics(document.lines)

        listOf(2_000L to 0, 5_000L to 0, 10_500L to 1).forEach { (positionMs, index) ->
            assertEquals(
                index,
                currentLyricIndexAt(positionMs - document.offsetMs, lines, suppressLeadingZero = true).index
            )
        }
        assertEquals(-1, currentLyricIndexAt(0L, emptyList(), suppressLeadingZero = true).index)
    }

    @Test
    fun wordTimingAndTranslationsSurviveImmersiveMapping() {
        val line = LyricLine(
            startTimeMs = 1_000L,
            endTimeMs = 3_000L,
            text = "Hello world",
            translations = listOf("你好世界"),
            spans = listOf(
                LyricSpan("Hello ", 1_000L, 2_000L),
                LyricSpan("world", 2_000L, 3_000L)
            )
        )
        val mapped = mapToHalcyonLyrics(listOf(line)).single()

        assertEquals("你好世界", mapped.translation)
        assertEquals(listOf(1_000L, 2_000L), mapped.words.map { it.startMs })
        assertEquals(listOf(2_000L, 3_000L), mapped.words.map { it.endMs })
        assertEquals(2_500L, mapped.copy(timeMs = mapped.words.last().startMs).timeMs + 500L)
    }

    @Test
    fun untimedChineseLyricsKeepCharacterLevelHighlighting() {
        val mapped = mapToHalcyonLyrics(listOf(LyricLine(1_000L, 3_000L, "你好"))).single()

        assertEquals(listOf("你", "好"), mapped.words.map { it.text })
        assertTrue(mapped.words.all { it.endMs > it.startMs })
        assertEquals(1_000L, mapped.words.first().startMs)
        assertEquals(3_000L, mapped.words.last().endMs)
    }
}
