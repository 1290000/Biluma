package com.android.purebilibili

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StartupSplashPolicyTest {

    @Test
    fun doesNotStartLocalProxyDuringColdStartByDefault() {
        assertFalse(shouldStartLocalProxyOnAppLaunch())
    }

    @Test
    fun enablesSplashFlyoutOnlyAfterStartupPrivacyFlowCompleted() {
        assertFalse(
            shouldEnableSplashFlyoutAnimation(
                sdkInt = 30,
                hasCompletedOnboarding = false,
                hasAcceptedReleaseDisclaimer = false,
                splashIconAnimationEnabled = true
            )
        )
        assertFalse(
            shouldEnableSplashFlyoutAnimation(
                sdkInt = 30,
                hasCompletedOnboarding = false,
                hasAcceptedReleaseDisclaimer = true,
                splashIconAnimationEnabled = true
            )
        )
        assertFalse(
            shouldEnableSplashFlyoutAnimation(
                sdkInt = 30,
                hasCompletedOnboarding = true,
                hasAcceptedReleaseDisclaimer = false,
                splashIconAnimationEnabled = true
            )
        )
        assertFalse(
            shouldEnableSplashFlyoutAnimation(
                sdkInt = 30,
                hasCompletedOnboarding = true,
                hasAcceptedReleaseDisclaimer = true,
                splashIconAnimationEnabled = true
            )
        )
        assertTrue(
            shouldEnableSplashFlyoutAnimation(
                sdkInt = 31,
                hasCompletedOnboarding = true,
                hasAcceptedReleaseDisclaimer = true,
                splashIconAnimationEnabled = true
            )
        )
        assertFalse(
            shouldEnableSplashFlyoutAnimation(
                sdkInt = 31,
                hasCompletedOnboarding = true,
                hasAcceptedReleaseDisclaimer = true,
                splashIconAnimationEnabled = false
            )
        )
    }

    @Test
    fun systemSplashPreloadHold_runsForEveryColdStartEvenWithoutOptionalIcon() {
        assertTrue(
            shouldKeepSystemSplashForPreload(
                runColdStartSplash = true
            )
        )
        assertFalse(
            shouldKeepSystemSplashForPreload(
                runColdStartSplash = false
            )
        )
    }

    @Test
    fun coldStartSplash_runsOnlyWithoutSavedInstanceState() {
        assertTrue(shouldRunColdStartSplash(savedInstanceStatePresent = false))
        assertFalse(shouldRunColdStartSplash(savedInstanceStatePresent = true))
    }
}
