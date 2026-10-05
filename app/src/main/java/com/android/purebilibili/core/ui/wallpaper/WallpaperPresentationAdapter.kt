package com.android.purebilibili.core.ui.wallpaper

import com.android.purebilibili.core.ui.adaptive.toAdaptiveWidthClass
import com.android.purebilibili.core.ui.wallpaper.resolveProfileWallpaperLayout as resolveSharedProfileWallpaperLayout
import com.android.purebilibili.core.util.WindowWidthSizeClass

fun resolveProfileWallpaperLayout(
    widthSizeClass: WindowWidthSizeClass,
): ProfileWallpaperLayout = resolveSharedProfileWallpaperLayout(
    widthClass = widthSizeClass.toAdaptiveWidthClass(),
)
