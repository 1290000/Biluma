package com.android.purebilibili.feature.home.subscription

import com.android.purebilibili.core.plugin.feed.FeedReadingStore
import com.android.purebilibili.data.model.response.HistoryData
import com.android.purebilibili.data.repository.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** 回顾时间窗：今日 / 近七天 / 近一个月。 */
enum class SubscriptionRecapWindow(val label: String) {
    TODAY("今日"),
    LAST_SEVEN_DAYS("近七天"),
    LAST_MONTH("近一个月"),
}

/** 窗口起点：今日取当天零点，其余按滚动 24h 窗口。 */
fun resolveSubscriptionRecapWindowStart(nowMs: Long, window: SubscriptionRecapWindow): Long {
    return when (window) {
        SubscriptionRecapWindow.TODAY -> {
            val dayStart = LocalDate.ofInstant(Instant.ofEpochMilli(nowMs), ZoneId.systemDefault())
            dayStart.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
        SubscriptionRecapWindow.LAST_SEVEN_DAYS -> nowMs - 7L * 24 * 60 * 60 * 1000
        SubscriptionRecapWindow.LAST_MONTH -> nowMs - 30L * 24 * 60 * 60 * 1000
    }
}

data class SubscriptionRssRecapStats(
    val readCount: Int = 0,
    val topSourceKey: String? = null,
)

data class SubscriptionUpRecap(
    val mid: Long,
    val name: String,
    val face: String,
    val watchCount: Int,
    val totalDurationSec: Long,
)

data class SubscriptionVideoRecapStats(
    val videoCount: Int = 0,
    val totalDurationSec: Long = 0L,
    val finishedCount: Int = 0,
    val topUps: List<SubscriptionUpRecap> = emptyList(),
)

/** RSS 侧回顾：统计窗口内已读条数与最常读的订阅源（sourceId）。 */
fun aggregateSubscriptionRssRecap(
    readTimestamps: Map<String, Long>,
    windowStartMs: Long,
): SubscriptionRssRecapStats {
    val inWindow = readTimestamps.filterValues { it >= windowStartMs }
    if (inWindow.isEmpty()) return SubscriptionRssRecapStats()
    // key 格式为 "${sourceId}\u001f${id}"，取首段聚合来源。
    val topSourceKey = inWindow.keys
        .mapNotNull { it.split('\u001f').firstOrNull()?.takeIf(String::isNotBlank) }
        .groupingBy { it }
        .eachCount()
        .maxByOrNull { it.value }
        ?.key
    return SubscriptionRssRecapStats(readCount = inWindow.size, topSourceKey = topSourceKey)
}

/** 视频侧回顾：观看数、总时长与最近爱看的 UP 主（按观看次数排序，前 8 位）。 */
fun aggregateSubscriptionVideoRecap(
    items: List<HistoryData>,
    windowStartSec: Long,
): SubscriptionVideoRecapStats {
    val inWindow = items.filter { it.view_at >= windowStartSec }
    if (inWindow.isEmpty()) return SubscriptionVideoRecapStats()
    val durationOf: (HistoryData) -> Long = { item ->
        // progress == -1 表示看完，用完整时长；否则按已看进度计。
        if (item.progress == -1) item.duration.toLong()
        else item.progress.coerceAtLeast(0).toLong()
    }
    val totalDurationSec = inWindow.sumOf(durationOf)
    val topUps = inWindow
        .filter { it.author_mid > 0L }
        .groupBy { it.author_mid }
        .map { (mid, entries) ->
            SubscriptionUpRecap(
                mid = mid,
                name = entries.firstOrNull()?.author_name.orEmpty().ifBlank { "UP主" },
                face = entries.firstOrNull()?.author_face.orEmpty(),
                watchCount = entries.size,
                totalDurationSec = entries.sumOf(durationOf),
            )
        }
        .sortedWith(compareByDescending<SubscriptionUpRecap> { it.watchCount }.thenByDescending { it.totalDurationSec })
        .take(8)
    return SubscriptionVideoRecapStats(
        videoCount = inWindow.size,
        totalDurationSec = totalDurationSec,
        finishedCount = inWindow.count { it.progress == -1 },
        topUps = topUps,
    )
}

/**
 * 按时间窗拉满视频历史（游标分页，view_at 单调递减），供回顾聚合。
 * 上限 [maxPages] 页防止重度用户无限翻页；失败返回空列表（回顾视频块降级隐藏）。
 */
suspend fun fetchSubscriptionVideoRecapHistory(
    windowStartMs: Long,
    maxPages: Int = 10,
): List<HistoryData> = withContext(Dispatchers.IO) {
    val windowStartSec = windowStartMs / 1000L
    val collected = mutableListOf<HistoryData>()
    var cursorMax = 0L
    var cursorViewAt = 0L
    repeat(maxPages) {
        val result = HistoryRepository.getHistoryList(ps = 30, max = cursorMax, viewAt = cursorViewAt)
            .getOrNull() ?: return@repeat
        val page = result.list
        if (page.isEmpty()) return@repeat
        collected += page
        val last = page.last()
        if (last.view_at * 1000L < windowStartMs) return@repeat
        val cursor = result.cursor ?: return@repeat
        if (cursor.max <= 0L) return@repeat
        cursorMax = cursor.max
        cursorViewAt = cursor.view_at
    }
    collected
}

/** 回顾会话内缓存：同窗口避免重复翻页拉取。 */
object SubscriptionRecapCache {
    private var cachedWindowStartMs: Long = 0L
    private var cachedStats: SubscriptionVideoRecapStats? = null

    suspend fun videoRecap(windowStartMs: Long): SubscriptionVideoRecapStats {
        cachedStats?.takeIf { cachedWindowStartMs == windowStartMs }?.let { return it }
        val stats = aggregateSubscriptionVideoRecap(
            items = fetchSubscriptionVideoRecapHistory(windowStartMs),
            windowStartSec = windowStartMs / 1000L,
        )
        cachedWindowStartMs = windowStartMs
        cachedStats = stats
        return stats
    }

    suspend fun rssRecap(context: android.content.Context, windowStartMs: Long): SubscriptionRssRecapStats =
        aggregateSubscriptionRssRecap(
            readTimestamps = FeedReadingStore.loadTimestamps(context),
            windowStartMs = windowStartMs,
        )
}
