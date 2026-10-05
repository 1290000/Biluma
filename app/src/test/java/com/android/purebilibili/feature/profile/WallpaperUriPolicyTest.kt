package com.android.purebilibili.feature.profile

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WallpaperUriPolicyTest {

    @Test
    fun `normalizeWallpaperUrl keeps local content Uri unchanged`() {
        val uri = "content://com.android.providers.media.documents/document/image%3A42"

        assertEquals(uri, normalizeWallpaperUrl(uri))
    }

    @Test
    fun `user selected wallpaper uri detects content document`() {
        assertTrue(isUserSelectedWallpaperUri("content://com.android.providers.media.documents/document/image%3A42"))
        assertTrue(isUserSelectedWallpaperUri("file:///data/user/0/app/cache/wallpaper_imports/image.img"))
        assertFalse(isUserSelectedWallpaperUri("https://i0.hdslb.com/bfs/splash.jpg"))
    }
}
