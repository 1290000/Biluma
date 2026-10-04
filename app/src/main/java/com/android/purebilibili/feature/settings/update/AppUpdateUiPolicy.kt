package com.android.purebilibili.feature.settings

internal enum class AppUpdateDialogMode {
    NONE,
    UPDATE_AVAILABLE,
    CHANGELOG
}

internal fun resolveAppUpdateDialogMode(
    isUpdateAvailable: Boolean,
    shouldOpenReleaseNotes: Boolean,
    hasAvailableRelease: Boolean = true
): AppUpdateDialogMode {
    return when {
        !hasAvailableRelease -> AppUpdateDialogMode.NONE
        isUpdateAvailable -> AppUpdateDialogMode.UPDATE_AVAILABLE
        shouldOpenReleaseNotes -> AppUpdateDialogMode.CHANGELOG
        else -> AppUpdateDialogMode.NONE
    }
}

internal fun resolveAutoCheckUpdateSubtitle(autoCheckEnabled: Boolean): String {
    return if (autoCheckEnabled) {
        "进入应用时自动检查新版本"
    } else {
        "关闭后仅手动检查"
    }
}

internal fun shouldRunAppEntryAutoCheck(
    autoCheckEnabled: Boolean,
    gateAllowsCheck: Boolean
): Boolean = autoCheckEnabled && gateAllowsCheck
