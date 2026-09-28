package com.android.purebilibili.feature.video.danmaku

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DanmakuViewportPolicyTest {
    @Test
    fun `invalid geometry does not produce a placeholder viewport`() {
        assertNull(resolveDanmakuViewport(0, 608, 3f, 2392f))
        assertNull(resolveDanmakuViewport(1080, 608, 0f, 2392f))
        assertNull(resolveDanmakuViewport(1080, 608, 3f, Float.NaN))
    }

    @Test
    fun `rotation preserves scale and a smaller window really shrinks below three quarters`() {
        val landscape = requireNotNull(resolveDanmakuViewport(2392, 1080, 3f, 2392f))
        val portrait = requireNotNull(resolveDanmakuViewport(1080, 2392, 3f, 2392f))
        val inline = requireNotNull(resolveDanmakuViewport(1080, 608, 3f, 2392f))
        assertEquals(landscape.scale, portrait.scale, 0f)
        assertEquals(inline.widthPx.toFloat() / landscape.widthPx, inline.scale, 0.0001f)
        assertTrue(inline.scale < 0.75f)
    }

    @Test
    fun `proportional geometry retains line budget including scaled interline spacing`() {
        fun lines(scale: Float) = resolveDanmakuVisibleLineCount(
            visibleHeightPx = 500f * scale,
            areaRatioHint = 0.5f,
            fontSize = 20f * scale,
            strokeWidth = 1.5f * scale,
            strokeEnabled = true,
            lineHeight = 1.6f,
            massiveMode = true,
            viewportScale = scale
        )
        assertEquals(lines(1f), lines(0.5f))
        val scale = 0.5f
        val rowHeight = resolveDanmakuLayerLineHeightPx(20f * scale, 1.6f)
        val rowStep = rowHeight + resolveDanmakuLineMarginPx(20f * scale)
        val occupiedHeight = rowHeight + (lines(scale) - 1) * rowStep
        assertTrue(occupiedHeight <= 500f * scale)
    }
}
