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
