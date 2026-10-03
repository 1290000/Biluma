package com.android.purebilibili.feature.video.ui.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.android.purebilibili.core.ui.components.AnimatedCountText
import com.android.purebilibili.core.ui.components.AppText
import com.android.purebilibili.danmaku.engine.DanmakuItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * 高赞弹幕悬浮条：对齐 B 站网页端"常看常新 ×1191"的体验。
 *
 * 数据来自播放器已加载的弹幕（DanmakuItem.likeCount），在当前播放位置附近的
 * 时间窗内取点赞 Top-N；每秒轮询一次播放位置刷新窗口。计数变化经
 * [AnimatedCountText] 呈现滚动 + 脉冲的跳动效果。点击条目一键跟发同款弹幕。
 */
private const val HOT_DANMAKU_WINDOW_BEFORE_MS = 15_000L
private const val HOT_DANMAKU_WINDOW_AFTER_MS = 45_000L
private const val HOT_DANMAKU_MIN_LIKES = 10L
private const val HOT_DANMAKU_MAX_ITEMS = 3

@Composable
fun HotDanmakuBar(
    getDanmakuList: () -> List<DanmakuItem>,
    player: Player?,
    onSendSame: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var danmakuVersion by remember { mutableLongStateOf(0L) }

    // 每秒刷新一次播放位置与已加载弹幕快照；快照仅在数量变化时触发重算
    LaunchedEffect(player) {
        if (player == null) return@LaunchedEffect
        while (isActive) {
            currentPositionMs = player.currentPosition.coerceAtLeast(0L)
            danmakuVersion = getDanmakuList().size.toLong()
            delay(1_000L)
        }
    }

    val danmakuList = remember(danmakuVersion) { getDanmakuList() }
    val hotItems = remember(danmakuList, currentPositionMs) {
        danmakuList.asSequence()
            .filter { it.likeCount >= HOT_DANMAKU_MIN_LIKES }
            .filter {
                it.showAtTime in (currentPositionMs - HOT_DANMAKU_WINDOW_BEFORE_MS)..(currentPositionMs + HOT_DANMAKU_WINDOW_AFTER_MS)
            }
            .filter { !it.text.isNullOrBlank() }
            .sortedByDescending { it.likeCount }
            .take(HOT_DANMAKU_MAX_ITEMS)
            .toList()
    }

    AnimatedVisibility(
        visible = hotItems.isNotEmpty(),
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow,
            ),
        ) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            hotItems.forEach { item ->
                HotDanmakuChip(
                    text = item.text.orEmpty(),
                    likeCount = item.likeCount,
                    onSendSame = { onSendSame(item.text.orEmpty()) },
                )
            }
        }
    }
}

@Composable
private fun HotDanmakuChip(
    text: String,
    likeCount: Long,
    onSendSame: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(onClick = onSendSame)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        AppText(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.92f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Color.White.copy(alpha = 0.16f))
                .padding(horizontal = 6.dp, vertical = 2.dp),
        ) {
            AppText(
                text = "×",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.72f),
            )
            AnimatedCountText(
                count = likeCount,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
            )
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = "一键发送同款弹幕",
                tint = Color.White.copy(alpha = 0.72f),
            )
        }
    }
}
