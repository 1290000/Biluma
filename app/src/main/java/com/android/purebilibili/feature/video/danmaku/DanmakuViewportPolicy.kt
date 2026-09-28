package com.android.purebilibili.feature.video.danmaku

/** Geometry shared by every video-danmaku layer; independent of the display-area setting. */
data class DanmakuViewport(
    val widthPx: Int,
    val heightPx: Int,
    val density: Float,
    val scale: Float,
    val fontSizeBoost: Float = 1f
) {
    init {
        require(widthPx > 0 && heightPx > 0)
        require(density.isFinite() && density > 0f)
        require(scale.isFinite() && scale > 0f && scale <= 1f)
        require(fontSizeBoost.isFinite() && fontSizeBoost > 0f)
    }
}

/** Inline players cover a fraction of the screen, so lift the text without moving the geometry. */
internal const val INLINE_DANMAKU_FONT_SIZE_BOOST = 1.5f

internal fun resolveDanmakuFontSizeBoost(isFullscreen: Boolean): Float =
    if (isFullscreen) 1f else INLINE_DANMAKU_FONT_SIZE_BOOST

/** Use the same long-edge basis even when inline and fullscreen aspect ratios differ. */
fun resolveDanmakuViewport(
    widthPx: Int,
    heightPx: Int,
    density: Float,
    referenceLongSidePx: Float,
    fontSizeBoost: Float = 1f
): DanmakuViewport? {
    if (widthPx <= 0 || heightPx <= 0 || !density.isFinite() || density <= 0f ||
        !referenceLongSidePx.isFinite() || referenceLongSidePx <= 0f ||
        !fontSizeBoost.isFinite() || fontSizeBoost <= 0f
    ) return null
    return DanmakuViewport(
        widthPx = widthPx,
        heightPx = heightPx,
        density = density,
        scale = (maxOf(widthPx, heightPx) / referenceLongSidePx).coerceAtMost(1f),
        fontSizeBoost = fontSizeBoost
    )
}
