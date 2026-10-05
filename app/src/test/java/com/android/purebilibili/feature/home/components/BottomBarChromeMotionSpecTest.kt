package com.android.purebilibili.feature.home.components

import com.android.purebilibili.core.ui.motion.AppMotionEasing
import kotlin.test.Test
import kotlin.test.assertEquals

class BottomBarChromeMotionSpecTest {

    @Test
    fun chromeMotion_preservesEachSemanticTiming() {
        assertEquals(260, bottomBarDockWidthMotionSpec<Float>().durationMillis)
        assertEquals(220, bottomBarChromeHeightMotionSpec<Float>().durationMillis)
        assertEquals(240, bottomBarClickPulseMotionSpec<Float>().durationMillis)
        assertEquals(240, bottomBarTapReleaseMotionSpec<Float>().durationMillis)
        assertEquals(260, bottomBarSettleReboundMotionSpec<Float>().durationMillis)
        assertEquals(AppMotionEasing.IosEaseInOut, bottomBarDockWidthMotionSpec<Float>().easing)
        assertEquals(AppMotionEasing.IosEaseInOut, bottomBarChromeHeightMotionSpec<Float>().easing)
    }

}
