package com.android.purebilibili.feature.download

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DownloadQueuePolicyTest {

    @Test
    fun staleUrlRefresh_triggersAfterThreshold() {
        val createdAt = 1_000_000L
        val threshold = STALE_DOWNLOAD_URL_REFRESH_THRESHOLD_MS

        assertEquals(
            true,
            shouldRefreshStaleDownloadUrls(
                createdAtMs = createdAt,
                nowMs = createdAt + threshold
            )
        )
        assertEquals(
            false,
            shouldRefreshStaleDownloadUrls(
                createdAtMs = createdAt,
                nowMs = createdAt + threshold - 1L
            )
        )
        // 刚入队/时钟异常时不刷新
        assertEquals(
            false,
            shouldRefreshStaleDownloadUrls(createdAtMs = createdAt, nowMs = createdAt)
        )
        assertEquals(
            false,
            shouldRefreshStaleDownloadUrls(createdAtMs = createdAt, nowMs = createdAt - 1L)
        )
    }

    @Test
    fun activeTask_blocksQueuedTaskDispatch() {
        val tasks = listOf(
            baseTask.copy(status = DownloadStatus.DOWNLOADING, createdAt = 1L),
            baseTask.copy(cid = 2L, status = DownloadStatus.QUEUED, createdAt = 2L)
        )

        assertNull(resolveNextQueuedDownloadTaskId(tasks))
    }

    @Test
    fun idleQueue_dispatchesOldestQueuedTask() {
        val older = baseTask.copy(cid = 2L, status = DownloadStatus.QUEUED, createdAt = 10L)
        val newer = baseTask.copy(cid = 3L, status = DownloadStatus.QUEUED, createdAt = 20L)

        assertEquals(older.id, resolveNextQueuedDownloadTaskId(listOf(newer, older)))
    }

    @Test
    fun workerCancellationFinishesOnlyForUserStableStates() {
        assertEquals(
            DownloadWorkerCancellationDecision.FINISH,
            resolveDownloadWorkerCancellationDecision(null)
        )
        assertEquals(
            DownloadWorkerCancellationDecision.FINISH,
            resolveDownloadWorkerCancellationDecision(DownloadStatus.PAUSED)
        )
        assertEquals(
            DownloadWorkerCancellationDecision.FINISH,
            resolveDownloadWorkerCancellationDecision(DownloadStatus.COMPLETED)
        )
        assertEquals(
            DownloadWorkerCancellationDecision.RETRY,
            resolveDownloadWorkerCancellationDecision(DownloadStatus.DOWNLOADING)
        )
        assertEquals(
            DownloadWorkerCancellationDecision.RETRY,
            resolveDownloadWorkerCancellationDecision(DownloadStatus.PENDING)
        )
    }

    private val baseTask = DownloadTask(
        bvid = "BV1queue",
        cid = 1L,
        title = "缓存视频",
        cover = "cover",
        ownerName = "UP",
        ownerFace = "",
        duration = 120,
        quality = 80,
        qualityDesc = "1080P",
        videoUrl = "video",
        audioUrl = "audio"
    )
}
