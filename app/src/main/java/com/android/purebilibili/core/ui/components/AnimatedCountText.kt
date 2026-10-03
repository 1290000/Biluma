package com.android.purebilibili.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.Text

/**
 * 数值变化时带滚动 + 脉冲缩放的计数文本：
 * 数值经弹性 spring 插值滚动到目标，同时整体放大再回落（1 -> 1.25 -> 1），
 * 用于高赞弹幕计数、点赞数等需要"跳一下"反馈的场景。
 */
@Composable
fun AnimatedCountText(
    count: Long,
    modifier: Modifier = Modifier,
    prefix: String = "",
    style: TextStyle = TextStyle.Default,
    color: Color = Color.Unspecified,
    maxLines: Int = 1,
) {
    val target = count.coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
    val animatedValue by animateIntAsState(
        targetValue = target,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "animated_count_value",
    )
    val pulseScale = remember { Animatable(1f) }
    var previousCount by remember { mutableLongStateOf(count) }
    LaunchedEffect(count) {
        if (count != previousCount) {
            previousCount = count
            pulseScale.snapTo(1.25f)
            pulseScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
            )
        }
    }
    Text(
        text = prefix + animatedValue.toString(),
        style = style,
        color = color,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.graphicsLayer {
            scaleX = pulseScale.value
            scaleY = pulseScale.value
            transformOrigin = TransformOrigin(0f, 0.5f)
        },
    )
}
