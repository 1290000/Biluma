package com.android.purebilibili.feature.video.note

internal fun shouldLoadVideoNote(
    isVideoNoteEnabled: Boolean,
    aid: Long
): Boolean {
    return isVideoNoteEnabled && aid > 0L
}

internal fun shouldShowVideoNoteCard(isVideoNoteEnabled: Boolean): Boolean {
    return isVideoNoteEnabled
}
