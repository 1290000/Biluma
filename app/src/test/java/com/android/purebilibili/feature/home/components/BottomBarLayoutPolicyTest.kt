package com.android.purebilibili.feature.home.components

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BottomBarLayoutPolicyTest {

    @Test
    fun `floating five items keeps compact width with safe per-item size`() {
        val policy = resolveBottomBarLayoutPolicy(
            containerWidth = 393.dp,
            itemCount = 5,
            isTablet = false,
            labelMode = 0,
            isFloating = true
        )

        val perItemWidth = (policy.maxBarWidth - (policy.rowPadding * 2)) / 5
        assertTrue(policy.maxBarWidth.value > 340f)
        assertTrue(policy.horizontalPadding.value < 26f)
        assertTrue(perItemWidth.value >= 52f)
    }

    @Test
    fun `floating four items can use wider bar than five items`() {
        val policyForFour = resolveBottomBarLayoutPolicy(
            containerWidth = 393.dp,
            itemCount = 4,
            isTablet = false,
            labelMode = 0,
            isFloating = true
        )
        val policyForFive = resolveBottomBarLayoutPolicy(
            containerWidth = 393.dp,
            itemCount = 5,
            isTablet = false,
            labelMode = 0,
            isFloating = true
        )

        assertTrue(policyForFour.maxBarWidth.value > policyForFive.maxBarWidth.value)
    }

    @Test
    fun `bilipai floating width uses intrinsic item width when space allows`() {
        val width = resolveBiliPaiFloatingBottomBarWidth(
            containerWidth = 393.dp,
            itemCount = 4,
            minEdgePadding = 20.dp,
            labelMode = 0
        )

        assertEquals(312.dp, width)
    }

    @Test
    fun `bilipai floating width keeps safe edge padding on crowded phones`() {
        val width = resolveBiliPaiFloatingBottomBarWidth(
            containerWidth = 393.dp,
            itemCount = 5,
            minEdgePadding = 20.dp,
            labelMode = 0
        )

        assertEquals(353.dp, width)
    }

    @Test
    fun `bilipai floating width follows icon label presentation mode`() {
        val iconOnlyWidth = resolveBiliPaiFloatingBottomBarWidth(
            containerWidth = 393.dp,
            itemCount = 5,
            minEdgePadding = 20.dp,
            labelMode = 1
        )
        val iconAndTextWidth = resolveBiliPaiFloatingBottomBarWidth(
            containerWidth = 393.dp,
            itemCount = 5,
            minEdgePadding = 20.dp,
            labelMode = 0
        )
        val textOnlyWidth = resolveBiliPaiFloatingBottomBarWidth(
            containerWidth = 393.dp,
            itemCount = 5,
            minEdgePadding = 20.dp,
            labelMode = 2
        )

        assertEquals(328.dp, iconOnlyWidth)
        assertEquals(353.dp, iconAndTextWidth)
        assertEquals(348.dp, textOnlyWidth)
    }

    @Test
    fun `bilipai icon only width follows capsule corner radius`() {
        val compactCorners = resolveBiliPaiFloatingBottomBarWidth(
            containerWidth = 393.dp,
            itemCount = 4,
            minEdgePadding = 20.dp,
            labelMode = 1,
            cornerRadius = 28.dp
        )
        val rounderCorners = resolveBiliPaiFloatingBottomBarWidth(
            containerWidth = 393.dp,
            itemCount = 4,
            minEdgePadding = 20.dp,
            labelMode = 1,
            cornerRadius = 36.dp
        )

        assertEquals(232.dp, compactCorners)
        assertEquals(296.dp, rounderCorners)
    }

    @Test
    fun `bilipai item slot width matches indicator geometry on crowded phones`() {
        val slotWidth = resolveBiliPaiBottomBarItemSlotWidth(
            dockWidth = 353.dp,
            horizontalPadding = 4.dp,
            itemCount = 5
        )

        assertEquals(69.dp, slotWidth)
        assertEquals(
            314.5.dp,
            resolveBiliPaiBottomBarItemCenterX(
                itemIndex = 4,
                itemWidth = slotWidth,
                horizontalPadding = 4.dp
            )
        )
    }

    @Test
    fun `bottom bar search performance guards keep expensive layers transient`() {
        assertEquals(
            false,
            shouldRenderBottomBarRefractionCapture(
                glassEnabled = true,
                hasBackdrop = true,
                captureProgress = 0.01f,
                isFeedScrollInProgress = false
            )
        )
        assertEquals(
            true,
            shouldRenderBottomBarRefractionCapture(
                glassEnabled = true,
                hasBackdrop = true,
                captureProgress = 0.01f,
                isFeedScrollInProgress = false,
                isBottomBarInteractionActive = true
            )
        )
        assertEquals(
            false,
            shouldRenderBottomBarRefractionCapture(
                glassEnabled = true,
                hasBackdrop = true,
                captureProgress = 0.001f,
                isFeedScrollInProgress = false
            )
        )
        assertEquals(
            false,
            shouldRenderBottomBarRefractionCapture(
                glassEnabled = false,
                hasBackdrop = true,
                captureProgress = 0.3f,
                isFeedScrollInProgress = false
            )
        )
    }

    @Test
    fun `feed scrolling skips idle bottom bar refraction capture`() {
        assertEquals(
            false,
            shouldRenderBottomBarRefractionCapture(
                glassEnabled = true,
                hasBackdrop = true,
                captureProgress = 0.01f,
                isFeedScrollInProgress = true,
                isBottomBarInteractionActive = false
            )
        )
    }

    @Test
    fun `feed scrolling keeps bottom bar refraction capture during direct interaction`() {
        assertEquals(
            true,
            shouldRenderBottomBarRefractionCapture(
                glassEnabled = true,
                hasBackdrop = true,
                captureProgress = 0.01f,
                isFeedScrollInProgress = true,
                isBottomBarInteractionActive = true
            )
        )
    }

    @Test
    fun `docked mode stays full width with no horizontal inset`() {
        val policy = resolveBottomBarLayoutPolicy(
            containerWidth = 393.dp,
            itemCount = 5,
            isTablet = false,
            labelMode = 0,
            isFloating = false
        )

        assertEquals(0.dp, policy.horizontalPadding)
        assertEquals(393.dp, policy.maxBarWidth)
    }

    @Test
    fun `floating default bar trims height while keeping touch comfort`() {
        assertEquals(58f, resolveBottomBarFloatingHeightDp(labelMode = 1, isTablet = false))
        assertEquals(12f, resolveBottomBarBottomPaddingDp(isFloating = true, isTablet = false))
    }

    @Test
    fun `plain md3 floating bar uses official floating toolbar`() {
        assertEquals(
            true,
            shouldUseOfficialMd3FloatingToolbar(
                isFloating = true,
                liquidGlassEnabled = false,
            ),
        )
        assertEquals(
            false,
            shouldUseOfficialMd3FloatingToolbar(
                isFloating = true,
                liquidGlassEnabled = true,
            ),
        )
        assertEquals(
            false,
            shouldUseOfficialMd3FloatingToolbar(
                isFloating = false,
                liquidGlassEnabled = false,
            ),
        )
    }
}
