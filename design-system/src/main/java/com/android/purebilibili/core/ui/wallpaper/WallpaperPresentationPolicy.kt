package com.android.purebilibili.core.ui.wallpaper

import com.android.purebilibili.core.ui.adaptive.AdaptiveWidthClass

enum class ProfileWallpaperLayout {
    TOP_BANNER_BLUR_BG,
    POSTER_CARD_BLUR_BG,
}

fun resolveProfileWallpaperLayout(widthClass: AdaptiveWidthClass): ProfileWallpaperLayout {
    return when (widthClass) {
        AdaptiveWidthClass.Compact -> ProfileWallpaperLayout.TOP_BANNER_BLUR_BG
        AdaptiveWidthClass.Medium,
        AdaptiveWidthClass.Expanded,
        AdaptiveWidthClass.Large,
        AdaptiveWidthClass.ExtraLarge -> ProfileWallpaperLayout.POSTER_CARD_BLUR_BG
    }
}
