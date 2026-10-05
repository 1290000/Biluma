package com.android.purebilibili.feature.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import com.android.purebilibili.core.ui.motion.iosMorphTween

internal fun <T> bottomBarDockWidthMotionSpec(): TweenSpec<T> = iosMorphTween(260)

internal fun <T> bottomBarChromeHeightMotionSpec(): TweenSpec<T> = iosMorphTween(220)

internal fun <T> bottomBarClickPulseMotionSpec(): TweenSpec<T> = tween(
    durationMillis = 240,
    easing = LinearEasing,
)

internal fun <T> bottomBarTapReleaseMotionSpec(): TweenSpec<T> = tween(
    durationMillis = 240,
    easing = FastOutSlowInEasing,
)

internal fun <T> bottomBarSettleReboundMotionSpec(): TweenSpec<T> = tween(
    durationMillis = 260,
    easing = LinearEasing,
)
