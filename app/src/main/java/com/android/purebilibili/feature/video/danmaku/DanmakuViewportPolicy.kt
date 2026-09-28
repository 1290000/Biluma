package com.android.purebilibili.feature.video.danmaku

/** Geometry shared by every video-danmaku layer; independent of the display-area setting. */
data class DanmakuViewport(
    val widthPx: Int,
    val heightPx: Int,
    val density: Float,
    val scale: Float
) {
    init {
        require(widthPx > 0 && heightPx > 0)
        require(density.isFinite() && density > 0f)
        require(scale.isFinite() && scale > 0f && scale <= 1f)
    }
}

/** Use the same long-edge basis even when inline and fullscreen aspect ratios differ. */
fun resolveDanmakuViewport(
    widthPx: Int,
    heightPx: Int,
    density: Float,
    referenceLongSidePx: Float
): DanmakuViewport? {
    if (widthPx <= 0 || heightPx <= 0 || !density.isFinite() || density <= 0f ||
        !referenceLongSidePx.isFinite() || referenceLongSidePx <= 0f
    ) return null
    return DanmakuViewport(
        widthPx = widthPx,
        heightPx = heightPx,
        density = density,
        scale = (maxOf(widthPx, heightPx) / referenceLongSidePx).coerceAtMost(1f)
    )
}
