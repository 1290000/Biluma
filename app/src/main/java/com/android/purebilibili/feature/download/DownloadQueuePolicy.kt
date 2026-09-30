package com.android.purebilibili.feature.download

internal fun isDownloadTaskActive(task: DownloadTask): Boolean {
    return when (task.status) {
        DownloadStatus.PENDING,
        DownloadStatus.DOWNLOADING,
        DownloadStatus.MERGING -> true
        else -> false
    }
}

internal fun resolveNextQueuedDownloadTaskId(tasks: Collection<DownloadTask>): String? {
    if (tasks.any(::isDownloadTaskActive)) return null

    return tasks
        .asSequence()
        .filter { it.status == DownloadStatus.QUEUED }
        .sortedWith(compareBy<DownloadTask> { it.createdAt }.thenBy { it.id })
        .map { it.id }
        .firstOrNull()
}

/**
 * 批量任务串行排队，入队时解析的 DASH 地址可能几小时后才被消费；
 * 距任务创建超过阈值时，执行前先刷新一次播放地址。
 */
internal fun shouldRefreshStaleDownloadUrls(
    createdAtMs: Long,
    nowMs: Long,
    thresholdMs: Long = STALE_DOWNLOAD_URL_REFRESH_THRESHOLD_MS
): Boolean = createdAtMs in 1 until nowMs && (nowMs - createdAtMs) >= thresholdMs

internal const val STALE_DOWNLOAD_URL_REFRESH_THRESHOLD_MS = 30L * 60_000L

internal enum class DownloadWorkerCancellationDecision {
    FINISH,
    RETRY
}

internal fun resolveDownloadWorkerCancellationDecision(
    status: DownloadStatus?
): DownloadWorkerCancellationDecision {
    return when (status) {
        null,
        DownloadStatus.PAUSED,
        DownloadStatus.COMPLETED -> DownloadWorkerCancellationDecision.FINISH
        else -> DownloadWorkerCancellationDecision.RETRY
    }
}
