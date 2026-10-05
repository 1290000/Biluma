package com.android.purebilibili.core.ui.components

import kotlin.test.Test
import kotlin.test.assertTrue

class AppTagChipMetricsPolicyTest {

    @Test
    fun smallIsTighterThanStandard() {
        val standard = resolveAppTagChipMetrics(AppTagChipSize.STANDARD)
        val small = resolveAppTagChipMetrics(AppTagChipSize.SMALL)

        assertTrue(small.fontScale < standard.fontScale)
        assertTrue(small.horizontalPadding < standard.horizontalPadding)
        assertTrue(small.verticalPadding < standard.verticalPadding)
        assertTrue(small.itemSpacingHorizontal < standard.itemSpacingHorizontal)
        assertTrue(small.itemSpacingVertical < standard.itemSpacingVertical)
    }


}
