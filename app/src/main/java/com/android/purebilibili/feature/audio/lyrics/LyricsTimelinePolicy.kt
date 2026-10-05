package com.android.purebilibili.feature.audio.lyrics

import kotlin.math.abs

internal const val LYRIC_AUXILIARY_ALIGNMENT_TOLERANCE_MS = 650L

internal fun normalizeLyricTimeline(lines: List<LyricLine>): List<LyricLine> {
    return lines
        .groupBy(LyricLine::startTimeMs)
        .toSortedMap()
        .map { (startTimeMs, simultaneous) ->
            val distinct = simultaneous.distinctBy(LyricLine::text)
            val representative = distinct.first()
            representative.copy(
                startTimeMs = startTimeMs,
                endTimeMs = simultaneous.maxOf(LyricLine::endTimeMs),
                text = distinct.joinToString("\n", transform = LyricLine::text),
                translations = simultaneous.flatMap(LyricLine::translations).distinct(),
                romanization = simultaneous.firstNotNullOfOrNull(LyricLine::romanization),
                spans = if (distinct.size == 1) representative.spans else emptyList()
            )
        }
}

internal fun mergeAuxiliaryLyrics(
    primary: List<LyricLine>,
    auxiliary: List<LyricLine>,
    toleranceMs: Long = LYRIC_AUXILIARY_ALIGNMENT_TOLERANCE_MS
): Map<Long, List<String>> {
    if (primary.isEmpty() || auxiliary.isEmpty()) return emptyMap()
    val merged = linkedMapOf<Long, MutableList<String>>()
    auxiliary.sortedBy(LyricLine::startTimeMs).forEach { auxiliaryLine ->
        val target = primary.minWithOrNull(
            compareBy<LyricLine> { abs(it.startTimeMs - auxiliaryLine.startTimeMs) }
                .thenBy(LyricLine::startTimeMs)
        )?.takeIf {
            abs(it.startTimeMs - auxiliaryLine.startTimeMs) <= toleranceMs
        }
        if (target != null && auxiliaryLine.text.isNotBlank()) {
            merged.getOrPut(target.startTimeMs) { mutableListOf() } += auxiliaryLine.text
        }
    }
    return merged.mapValues { (_, values) -> values.distinct() }
}
