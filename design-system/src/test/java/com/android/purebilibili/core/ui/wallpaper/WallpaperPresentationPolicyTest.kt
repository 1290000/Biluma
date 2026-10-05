package com.android.purebilibili.core.ui.wallpaper

import com.android.purebilibili.core.ui.adaptive.AdaptiveWidthClass
import kotlin.test.Test
import kotlin.test.assertEquals

class WallpaperPresentationPolicyTest {

    @Test
    fun profileLayoutSeparatesCompactAndTabletWidths() {
        assertEquals(
            ProfileWallpaperLayout.TOP_BANNER_BLUR_BG,
            resolveProfileWallpaperLayout(AdaptiveWidthClass.Compact),
        )
        assertEquals(
            ProfileWallpaperLayout.POSTER_CARD_BLUR_BG,
            resolveProfileWallpaperLayout(AdaptiveWidthClass.Medium),
        )
    }
}
