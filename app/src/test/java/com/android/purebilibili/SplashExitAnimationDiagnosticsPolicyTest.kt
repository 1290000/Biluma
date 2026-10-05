package com.android.purebilibili

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SplashExitAnimationDiagnosticsPolicyTest {

    @Test
    fun warmResumeLoggingEnabledOnlyAfterFirstResumeAndWithoutConfigChange() {
        assertTrue(
            shouldLogWarmResume(
                hasCompletedInitialResume = true,
                isChangingConfigurations = false
            )
        )
        assertFalse(
            shouldLogWarmResume(
                hasCompletedInitialResume = false,
                isChangingConfigurations = false
            )
        )
        assertFalse(
            shouldLogWarmResume(
                hasCompletedInitialResume = true,
                isChangingConfigurations = true
            )
        )
    }
}
