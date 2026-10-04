package com.android.purebilibili.navigation3

import androidx.compose.animation.core.LinearEasing
import com.android.purebilibili.feature.settings.isSettingsSubtreeRoute
import top.yukonga.miuix.kmp.nav.transition.NavMotion
import top.yukonga.miuix.kmp.nav.transition.NavSettleSpec
import top.yukonga.miuix.kmp.nav.transition.navGraphicsTransition

/** 从设置底栏进入场景时，避免在官方面板导航外叠加整页过渡。 */
internal val SettingsPaneTransition = navGraphicsTransition(
    motion = NavMotion(
        commit = NavSettleSpec.Tween(0, LinearEasing),
        cancel = NavSettleSpec.Tween(0, LinearEasing),
        programmatic = NavSettleSpec.Tween(0, LinearEasing),
    ),
    scrim = { 0f },
) { }

internal fun isSettingsPaneNavigation(
    persistentPanes: Boolean,
    fromKey: BiliPaiNavKey?,
    toKey: BiliPaiNavKey?,
    activeMainHostRoute: String?,
): Boolean {
    if (!persistentPanes || toKey == null || !isSettingsSubtreeRoute(toKey.routeBase)) return false
    val fromRoute = if (fromKey == BiliPaiNavKey.MainHost) activeMainHostRoute else fromKey?.routeBase
    return isSettingsSubtreeRoute(fromRoute)
}
