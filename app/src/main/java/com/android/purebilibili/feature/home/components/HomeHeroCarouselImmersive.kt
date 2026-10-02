package com.android.purebilibili.feature.home.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.android.purebilibili.core.ui.components.AppText
import com.android.purebilibili.core.util.FormatUtils
import com.android.purebilibili.data.model.response.VideoItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private const val HERO_IMMERSIVE_AUTO_ADVANCE_MS = 7_000L

/**
 * 沉浸式 hero 变体（折叠屏/平板展开态，容器宽 ≥ 840dp 时由 HomeCategoryPage 切换）。
 * 与 TV 首页同一套视觉语言：全幅封面 + 当前封面高斯模糊氛围层 + 底部渐隐，锐利层与
 * 模糊层同为 FillWidth 顶对齐、逐像素对位，无横向断层。
 * 数据与点击契约复用既有 hero 管线（selectHomeHeroCarouselItems 去重/门控/回调）。
 * [horizontalEscapeDp] 为网格 contentPadding 的水平值，用于越界绘制实现真全幅。
 */
@Composable
internal fun HomeHeroCarouselImmersive(
    videos: List<VideoItem>,
    horizontalEscapeDp: Dp,
    onVideoClick: (VideoItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (videos.isEmpty()) return
    var index by remember(videos) { mutableIntStateOf(0) }
    val current = videos.getOrNull(index)
    LaunchedEffect(videos.size) {
        if (videos.size < 2) return@LaunchedEffect
        while (isActive) {
            delay(HERO_IMMERSIVE_AUTO_ADVANCE_MS)
            index = (index + 1) % videos.size
        }
    }

    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2.2f)
            .escapeHorizontal(horizontalEscapeDp)
            .clipToBounds()
            .clickable { current?.let(onVideoClick) },
    ) {
        // 氛围层：极小尺寸请求 + blur；低版本系统无 RenderEffect 时小图拉伸本身即是柔和模糊
        current?.pic?.let { pic ->
            val ambientModel = remember(pic) {
                ImageRequest.Builder(context).data(pic.toHttpsUrl()).size(48).build()
            }
            AsyncImage(
                model = ambientModel,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopStart,
                modifier = Modifier.matchParentSize().blur(48.dp),
            )
        }
        // 底部渐隐入页面表面：横幅下缘与信息流无缝衔接，明暗主题各自成立
        Box(
            Modifier.matchParentSize().background(Brush.verticalGradient(
                0.5f to Color.Transparent,
                1f to MaterialTheme.colorScheme.surface,
            )),
        )
        // 锐利封面：DstIn 渐隐融入氛围层
        Crossfade(targetState = current?.pic, label = "hero_immersive") { pic ->
            if (pic.isNullOrBlank()) return@Crossfade
            val model = remember(pic) {
                ImageRequest.Builder(context).data(pic.toHttpsUrl()).build()
            }
            AsyncImage(
                model = model,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopStart,
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(0.35f to Color.Black, 1f to Color.Transparent),
                            blendMode = BlendMode.DstIn,
                        )
                    },
            )
        }
        Column(
            Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 28.dp, vertical = 24.dp),
        ) {
            AppText(
                text = current?.title.orEmpty(),
                style = MaterialTheme.typography.headlineSmall.copy(
                    shadow = Shadow(color = Color(0xB3000000), blurRadius = 16f),
                ),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            val meta = buildList {
                if (current != null && current.duration > 0) add(FormatUtils.formatDuration(current.duration))
                if (current != null && current.stat.view > 0) add(FormatUtils.formatStat(current.stat.view.toLong()) + "播放")
                if (current != null && current.stat.danmaku > 0) add(FormatUtils.formatStat(current.stat.danmaku.toLong()) + "弹幕")
            }.joinToString("  ·  ")
            if (meta.isNotBlank()) {
                AppText(
                    text = meta,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xE6FFFFFF),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 14.dp),
        ) {
            videos.forEachIndexed { i, _ ->
                Box(
                    Modifier
                        .size(width = if (i == index) 18.dp else 6.dp, height = 6.dp)
                        .clip(CircleShape)
                        .background(if (i == index) MaterialTheme.colorScheme.primary else Color(0x80FFFFFF)),
                )
            }
        }
    }
}

/** 网格 item 横向越界 contentPadding，实现真全幅；仍处于网格裁剪边界内。 */
private fun Modifier.escapeHorizontal(amount: Dp): Modifier = layout { measurable, constraints ->
    val extra = amount.roundToPx()
    if (extra <= 0 || constraints.maxWidth == Constraints.Infinity) {
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
    } else {
        val width = constraints.maxWidth + extra * 2
        val placeable = measurable.measure(
            Constraints(minWidth = width, maxWidth = width, minHeight = 0, maxHeight = constraints.maxHeight),
        )
        layout(constraints.maxWidth, placeable.height) { placeable.placeRelative(-extra, 0) }
    }
}

private fun String.toHttpsUrl(): String = if (startsWith("//")) "https:$this" else this
