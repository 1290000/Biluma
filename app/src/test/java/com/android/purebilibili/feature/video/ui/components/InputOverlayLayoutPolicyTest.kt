package com.android.purebilibili.feature.video.ui.components

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InputOverlayLayoutPolicyTest {

    @Test
    fun compactWindows_keepFullWidthInputOverlay() {
        assertEquals(390, resolveBottomInputOverlayMaxWidthDp(390))
        assertEquals(599, resolveBottomInputOverlayMaxWidthDp(599))
    }

    @Test
    fun mediumAndAboveWindows_capInputOverlayAt640Dp() {
        assertEquals(640, resolveBottomInputOverlayMaxWidthDp(600))
        assertEquals(640, resolveBottomInputOverlayMaxWidthDp(840))
        assertEquals(640, resolveBottomInputOverlayMaxWidthDp(1280))
        assertEquals(640, resolveBottomInputOverlayMaxWidthDp(1920))
    }

    @Test
    fun inputDialogs_routeThroughUnifiedHingeSafeOverlayHost() {
        val dialogSources = listOf(
            "app/src/main/java/com/android/purebilibili/feature/video/ui/components/CommentInputDialog.kt",
            "app/src/main/java/com/android/purebilibili/feature/video/ui/components/DanmakuSendDialog.kt"
        ).map { path ->
            listOf(
                File(path),
                File(path.removePrefix("app/"))
            ).first { it.exists() }.readText()
        }

        dialogSources.forEach { source ->
            assertTrue(source.contains("HingeSafeInputOverlayHost("))
            assertTrue(source.contains("widthIn(max = inputOverlayMaxWidthDp.dp)"))
            assertTrue(source.contains("resolveBottomInputOverlayMaxWidthDp("))
        }
    }

    @Test
    fun fullscreenVideo_keepsPlayerInsideFirstSafePaneUnderHalfOpenPosture() {        val source = listOf(
            File("app/src/main/java/com/android/purebilibili/feature/video/screen/VideoDetailScreenStateHolder.kt"),
            File("src/main/java/com/android/purebilibili/feature/video/screen/VideoDetailScreenStateHolder.kt")
        ).first { it.exists() }.readText()
        val mainContent = source.substring(
            source.indexOf("fun BoxScope.VideoDetailRouteSheetMainContent()"),
            source.indexOf("VideoDetailRouteSheetOverlayContent")
        )

        // 全屏的两条渲染路径（连续过渡 movable 内容与 VideoPlayerSection）都要在
        // 半开姿态下收进铰链安全区，不能只覆盖非全屏分支。
        // shouldAvoidHinge 共 3 处：全屏两条路径各 1 处 + 非全屏平板分支 1 处。
        assertEquals(
            3,
            mainContent.split("appWindowAdaptiveInfo.shouldAvoidHinge").size - 1
        )
        assertEquals(
            2,
            mainContent.split("AppHingePaneLayout(").size - 1,
            "全屏分支的两条播放器路径都应接入 AppHingePaneLayout"
        )
    }

    @Test
    fun secondBatchPages_wrapContentInHingeSafeContainer() {
        listOf(
            "feature/download/DownloadListScreen.kt",
            "feature/list/CommonListScreen.kt",
            "feature/article/ArticleDetailScreen.kt",
            "feature/space/SpaceScreen.kt",
        ).forEach { path ->
            val source = listOf(
                File("app/src/main/java/com/android/purebilibili/$path"),
                File("src/main/java/com/android/purebilibili/$path")
            ).first { it.exists() }.readText()
            assertTrue(
                source.contains("adaptive.AppHingeSafeContent("),
                "$path 未接入页级铰链安全容器"
            )
        }
    }

    @Test
    fun offlineAndPluginPlayers_keepMediaInsideFirstSafePaneUnderHalfOpenPosture() {
        listOf(
            "feature/download/OfflineVideoPlayerScreen.kt" to "AppHingePaneLayout(",
            "feature/plugin/js/ExternalMediaPlayerScreen.kt" to "AppHingePaneLayout(",
        ).forEach { (path, marker) ->
            val source = listOf(
                File("app/src/main/java/com/android/purebilibili/$path"),
                File("src/main/java/com/android/purebilibili/$path")
            ).first { it.exists() }.readText()
            assertTrue(
                source.contains("shouldAvoidHinge") && source.contains(marker),
                "$path 未在半开姿态接入安全 pane 布局"
            )
        }
    }
}
