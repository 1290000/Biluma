package com.android.purebilibili.feature.profile

internal fun normalizeWallpaperUrl(url: String?): String {
    if (url.isNullOrBlank()) return ""
    var normalized = url.trim()
    if (normalized.startsWith("//")) {
        normalized = "https:$normalized"
    } else if (normalized.startsWith("http://")) {
        normalized = normalized.replaceFirst("http://", "https://")
    }
    return normalized
}

internal fun isUserSelectedWallpaperUri(uri: String?): Boolean {
    if (uri.isNullOrBlank()) return false
    val normalized = uri.trim()
    return normalized.startsWith("content://") || normalized.startsWith("file://")
}
