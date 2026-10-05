package com.android.purebilibili.feature.video.note

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VideoNoteVisibilityPolicyTest {

    @Test
    fun `disabled note setting should block note loading and card rendering`() {
        assertFalse(shouldLoadVideoNote(isVideoNoteEnabled = false, aid = 123L))
        assertFalse(shouldShowVideoNoteCard(isVideoNoteEnabled = false))
    }

    @Test
    fun `enabled note setting should allow note loading only for valid aid`() {
        assertTrue(shouldLoadVideoNote(isVideoNoteEnabled = true, aid = 123L))
        assertFalse(shouldLoadVideoNote(isVideoNoteEnabled = true, aid = 0L))
    }
}
