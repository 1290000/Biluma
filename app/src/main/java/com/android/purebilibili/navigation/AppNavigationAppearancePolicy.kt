package com.android.purebilibili.navigation

import com.android.purebilibili.core.store.HomeSettings

internal data class AppNavigationAppearance(
    val cardTransitionEnabled: Boolean,
    val bottomBarBlurEnabled: Boolean,
    val bottomBarLabelMode: Int,
)

internal fun resolveEffectiveNavigationBottomBarBlur(
    homeSettings: HomeSettings,
): Boolean = homeSettings.isBottomBarBlurEnabled

internal fun resolveAppNavigationAppearance(
    homeSettings: HomeSettings,
): AppNavigationAppearance {
    return AppNavigationAppearance(
        cardTransitionEnabled = homeSettings.cardTransitionEnabled,
        bottomBarBlurEnabled = resolveEffectiveNavigationBottomBarBlur(
            homeSettings = homeSettings,
        ),
        bottomBarLabelMode = homeSettings.bottomBarLabelMode,
    )
}
